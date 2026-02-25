package cap2.service;

import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.dto.request.RegisterRequest;
import cap2.dto.response.RegisterResponse;
import cap2.repository.ProfileRepository;
import cap2.repository.UserRepository;
import cap2.schema.Profile;
import cap2.schema.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest req) {

        String email = req.getEmail().trim().toLowerCase();
        log.info("Registering user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
        // Create and save the User entity
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Saved user with ID: {}", savedUser.getId());

        // Create and save the Profile entity
        Profile profile = new Profile();
        profile.setUserId(savedUser.getId());
        profile.setFullName(req.getFullName());
        profile.setPhone(req.getPhone());
        profile.setAddress(req.getAddress());

        Profile savedProfile = profileRepository.save(profile);
        log.info("Saved profile with ID: {}", savedProfile.getId());

        return new RegisterResponse(savedUser.getId(), savedUser.getEmail());
    }
}