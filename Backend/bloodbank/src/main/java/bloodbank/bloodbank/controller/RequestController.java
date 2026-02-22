package bloodbank.bloodbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.repository.OrganizationRepository;
import bloodbank.bloodbank.repository.UserRepository;

@RestController
@RequestMapping("/api/user")
public class RequestController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Autowired
    public RequestController(UserRepository userRepository,
                             OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    // Request creation logic is handled in UserDashboardController to keep API surface consistent
    // ...existing code...
}