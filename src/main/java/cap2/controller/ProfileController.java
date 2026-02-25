package cap2.controller;

import cap2.dto.request.UpdateProfileRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ProfileResponse;
import cap2.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    ProfileService profileService;

    // Lấy profile của chính mình
    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile() {
        ProfileResponse response = profileService.getMyProfile();
        return ApiResponse.ok("Get my profile successfully", response);
    }

    // Cập nhật profile của chính mình
    @PutMapping("/me")
    public ApiResponse<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateMyProfile(request);
        return ApiResponse.ok("Update my profile successfully", response);
    }

    // Xóa profile của chính mình
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMyProfile() {
        profileService.deleteMyProfile();
        return ApiResponse.ok("Delete my profile successfully", null);
    }

    // === Admin APIs ===

    // Lấy danh sách tất cả profiles (chỉ ADMIN)
    @GetMapping
    public ApiResponse<PageResponse<ProfileResponse>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProfileResponse> response = profileService.getAllProfiles(page, size);
        return ApiResponse.ok("Get all profiles successfully", response);
    }

    // Lấy profile theo ID (chỉ ADMIN hoặc chính mình)
    @GetMapping("/{id}")
    public ApiResponse<ProfileResponse> getProfileById(@PathVariable String id) {
        ProfileResponse response = profileService.getProfileById(id);
        return ApiResponse.ok("Get profile successfully", response);
    }

    // Cập nhật profile theo ID (chỉ ADMIN hoặc chính mình)
    @PutMapping("/{id}")
    public ApiResponse<ProfileResponse> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(id, request);
        return ApiResponse.ok("Update profile successfully", response);
    }

    // Xóa profile theo ID (chỉ ADMIN hoặc chính mình)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProfile(@PathVariable String id) {
        profileService.deleteProfile(id);
        return ApiResponse.ok("Delete profile successfully", null);
    }
}
