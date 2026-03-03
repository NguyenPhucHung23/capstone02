package cap2.controller;

import cap2.dto.request.RegisterRequest;
import cap2.dto.request.UpdateUserRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.RegisterResponse;
import cap2.dto.response.UserResponse;
import cap2.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.register(request);
        return ApiResponse.ok("Đăng ký thành công", response);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        UserResponse response = userService.getMyInfo();
        return ApiResponse.ok("Lấy thông tin thành công", response);
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserResponse> response = userService.getAllUsers(page, size);
        return ApiResponse.ok("Lấy danh sách người dùng thành công", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ApiResponse.ok("Lấy thông tin người dùng thành công", response);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ApiResponse.ok("Cập nhật người dùng thành công", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.ok("Xóa người dùng thành công", null);
    }
}