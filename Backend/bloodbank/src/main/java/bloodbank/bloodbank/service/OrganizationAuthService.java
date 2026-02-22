package bloodbank.bloodbank.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bloodbank.bloodbank.entity.Organization;
import bloodbank.bloodbank.repository.OrganizationRepository;

@Service
public class OrganizationAuthService implements UserDetailsService {

    private final OrganizationRepository organizationRepository;

    public OrganizationAuthService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Organization org = organizationRepository.findByUsernameIgnoreCase(username);
    if (org == null) throw new UsernameNotFoundException("Organization not found");

        return new org.springframework.security.core.userdetails.User(
                org.getUsername(),
                org.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ORGANIZATION"))
        );
    }
}
