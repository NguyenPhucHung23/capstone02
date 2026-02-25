package cap2.service;

import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.dto.request.RegisterRequest;
import cap2.dto.request.UpdateUserRequest;
import cap2.dto.response.PageResponse;
import cap2.dto.response.RegisterResponse;
import cap2.dto.response.UserResponse;
import cap2.repository.ProfileRepository;
import cap2.repository.UserRepository;
import cap2.schema.Profile;
import cap2.schema.Role;
import cap2.schema.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    ProfileRepository profileRepository;
    PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        log.info("Registering user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Saved user with ID: {}", savedUser.getId());

        Profile profile = Profile.builder()
                .userId(savedUser.getId())
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .gender(req.getGender())
                .build();

        profileRepository.save(profile);

        return new RegisterResponse(savedUser.getId(), savedUser.getEmail());
    }

    // Lấy danh sách user theo trang (chỉ ADMIN)
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        checkAdminRole();

        Pageable pageable = PageRequest.of(page, size, Sort.by("email").ascending());
        Page<User> userPage = userRepository.findAll(pageable);

        return PageResponse.<UserResponse>builder()
                .content(userPage.getContent().stream()
                        .map(this::mapToUserResponse)
                        .toList())
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    // Lấy thông tin user hiện tại (từ token)
    public UserResponse getMyInfo() {
        String currentUserId = getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return mapToUserResponse(user);
    }

    // Lấy user theo ID (chỉ xem của mình hoặc ADMIN xem tất cả)
    public UserResponse getUserById(String id) {
        checkPermission(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return mapToUserResponse(user);
    }

    // Cập nhật user (chỉ sửa của mình hoặc ADMIN sửa tất cả)
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        checkPermission(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
            }
            user.setEmail(newEmail);
        }

        // Chỉ ADMIN mới được đổi role
        if (request.getRole() != null) {
            checkAdminRole();
            try {
                user.setRole(Role.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new AppException(ErrorCode.INVALID_ROLE);
            }
        }

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    // Xóa user (chỉ xóa của mình hoặc ADMIN xóa tất cả)
    @Transactional
    public void deleteUser(String id) {
        checkPermission(id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        profileRepository.deleteByUserId(id);
        userRepository.delete(user);
        log.info("Deleted user and profile: {}", id);
    }

    // Lấy userId từ token hiện tại
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // Kiểm tra có phải ADMIN không
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    // Kiểm tra quyền: phải là chính mình hoặc ADMIN
    private void checkPermission(String targetUserId) {
        String currentUserId = getCurrentUserId();
        if (!currentUserId.equals(targetUserId) && !isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    // Kiểm tra phải là ADMIN
    private void checkAdminRole() {
        if (!isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}