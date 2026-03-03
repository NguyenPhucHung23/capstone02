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

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile() {
        ProfileResponse response = profileService.getMyProfile();
        return ApiResponse.ok("Lấy hồ sơ thành công", response);
    }

    @PutMapping("/me")
    public ApiResponse<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateMyProfile(request);
        return ApiResponse.ok("Cập nhật hồ sơ thành công", response);
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMyProfile() {
        profileService.deleteMyProfile();
        return ApiResponse.ok("Xóa hồ sơ thành công", null);
    }

    @GetMapping
    public ApiResponse<PageResponse<ProfileResponse>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProfileResponse> response = profileService.getAllProfiles(page, size);
        return ApiResponse.ok("Lấy danh sách hồ sơ thành công", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProfileResponse> getProfileById(@PathVariable String id) {
        ProfileResponse response = profileService.getProfileById(id);
        return ApiResponse.ok("Lấy hồ sơ thành công", response);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProfileResponse> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(id, request);
        return ApiResponse.ok("Cập nhật hồ sơ thành công", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProfile(@PathVariable String id) {
        profileService.deleteProfile(id);
        return ApiResponse.ok("Xóa hồ sơ thành công", null);
    }
}
