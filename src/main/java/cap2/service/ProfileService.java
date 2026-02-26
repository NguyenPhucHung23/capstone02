package cap2.service;

import cap2.dto.request.UpdateProfileRequest;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ProfileResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.ProfileRepository;
import cap2.repository.UserRepository;
import cap2.schema.Profile;
import cap2.schema.User;
import cap2.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {

    ProfileRepository profileRepository;
    UserRepository userRepository;

    public PageResponse<ProfileResponse> getAllProfiles(int page, int size) {
        SecurityUtils.checkAdminRole();

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        Page<Profile> profilePage = profileRepository.findAll(pageable);

        return PageResponse.<ProfileResponse>builder()
                .content(profilePage.getContent().stream()
                        .map(this::mapToProfileResponse)
                        .toList())
                .page(profilePage.getNumber())
                .size(profilePage.getSize())
                .totalElements(profilePage.getTotalElements())
                .totalPages(profilePage.getTotalPages())
                .first(profilePage.isFirst())
                .last(profilePage.isLast())
                .build();
    }

    // Lấy profile của chính mình
    public ProfileResponse getMyProfile() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Profile profile = profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return mapToProfileResponse(profile);
    }

    // Lấy profile theo ID (chỉ xem của mình hoặc ADMIN)
    public ProfileResponse getProfileById(String id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        SecurityUtils.checkPermission(profile.getUserId());
        return mapToProfileResponse(profile);
    }

    // Lấy profile theo userId (chỉ xem của mình hoặc ADMIN)
    public ProfileResponse getProfileByUserId(String userId) {
        SecurityUtils.checkPermission(userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        return mapToProfileResponse(profile);
    }

    // Cập nhật profile của chính mình
    public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Profile profile = profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        updateProfileFields(profile, request);
        Profile savedProfile = profileRepository.save(profile);
        log.info("Updated my profile: {}", savedProfile.getId());
        return mapToProfileResponse(savedProfile);
    }

    // Cập nhật profile theo ID (chỉ sửa của mình hoặc ADMIN)
    public ProfileResponse updateProfile(String id, UpdateProfileRequest request) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        SecurityUtils.checkPermission(profile.getUserId());

        updateProfileFields(profile, request);
        Profile savedProfile = profileRepository.save(profile);
        log.info("Updated profile: {}", savedProfile.getId());
        return mapToProfileResponse(savedProfile);
    }

    // Xóa profile của chính mình
    public void deleteMyProfile() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Profile profile = profileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        profileRepository.delete(profile);
        log.info("Deleted my profile: {}", profile.getId());
    }

    // Xóa profile theo ID (chỉ xóa của mình hoặc ADMIN)
    public void deleteProfile(String id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        SecurityUtils.checkPermission(profile.getUserId());
        profileRepository.delete(profile);
        log.info("Deleted profile: {}", id);
    }

    // === Helper methods ===

    private void updateProfileFields(Profile profile, UpdateProfileRequest request) {
        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getDistrict() != null) {
            profile.setDistrict(request.getDistrict());
        }
        if (request.getWard() != null) {
            profile.setWard(request.getWard());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
    }


    private ProfileResponse mapToProfileResponse(Profile profile) {
        // Lấy email từ User
        String email = userRepository.findById(profile.getUserId())
                .map(User::getEmail)
                .orElse(null);

        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .email(email)
                .address(profile.getAddress())
                .city(profile.getCity())
                .district(profile.getDistrict())
                .ward(profile.getWard())
                .gender(profile.getGender())
                .build();
    }
}
