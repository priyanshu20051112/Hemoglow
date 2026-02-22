package bloodbank.bloodbank.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bloodbank.bloodbank.entity.Donation;
import bloodbank.bloodbank.entity.Event;
import bloodbank.bloodbank.entity.EventParticipant;
import bloodbank.bloodbank.entity.Request;
import bloodbank.bloodbank.entity.User;
import bloodbank.bloodbank.repository.DonationRepository;
import bloodbank.bloodbank.repository.EventParticipantRepository;
import bloodbank.bloodbank.repository.EventRepository;
import bloodbank.bloodbank.repository.RequestRepository;
import bloodbank.bloodbank.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String normalized = username == null ? null : username.trim().toLowerCase();
    User user = userRepository.findByUsernameIgnoreCase(normalized)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserRole()))
        );
    }

    // ===== User CRUD =====
    public User saveUser(User user) { 
        // Normalize username to avoid case-sensitivity issues and trim input
        if (user.getUsername() != null) user.setUsername(user.getUsername().trim().toLowerCase());
        return userRepository.save(user);
    }

    public boolean userEXists(String username) { 
        String normalized = username == null ? null : username.trim().toLowerCase();
        return userRepository.findByUsernameIgnoreCase(normalized).isPresent(); 
    }

    public User findByUsername(String username) {
    String normalized = username == null ? null : username.trim().toLowerCase();
    return userRepository.findByUsernameIgnoreCase(normalized)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Return all users (debug helper)
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Updated method with id + new data
    public User updateUser(Long id, User updatedData) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) return null;

        if (updatedData.getUsername() != null) {
            existing.setUsername(updatedData.getUsername());
        }

        if (updatedData.getEmail() != null) {
            // check duplicate email
            boolean emailUsed = userRepository.findByEmail(updatedData.getEmail())
                                .filter(u -> !u.getId().equals(id))
                                .isPresent();
            if (emailUsed) {
                throw new RuntimeException("Email already in use");
            }
            existing.setEmail(updatedData.getEmail());
        }

        if (updatedData.getUserRole() != null) {
            existing.setUserRole(updatedData.getUserRole());
        }

        // keep password, donations, requests unchanged
        return userRepository.save(existing);
    }

    // ===== Requests =====
    public List<Request> getUserRequests(Long userId){
        return requestRepository.findByUserId(userId);
    }

    public Request addRequest(Request request){
        return requestRepository.save(request);
    }

    // ===== Donations =====
    public List<Donation> getUserDonations(Long userId){
        return donationRepository.findByUserId(userId);
    }

    public Donation addDonation(Donation donation){
        return donationRepository.save(donation);
    }

    // ===== Events =====
    public List<Event> getAllEvents(){ 
        return eventRepository.findAll(); 
    }

    public boolean joinEvent(Long userId, Long eventId){
        if(participantRepository.existsByUserIdAndEventId(userId, eventId)) return false;

        EventParticipant ep = new EventParticipant();
        ep.setUser(getUser(userId));
        ep.setEvent(eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found")));
        participantRepository.save(ep);
        return true;
    }

    public List<EventParticipant> getUserEventParticipants(Long userId){
        return participantRepository.findByUserId(userId);
    }
    public boolean existsByEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
    }
    
}
