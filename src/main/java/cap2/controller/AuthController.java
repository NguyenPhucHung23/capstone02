package cap2.controller;

import cap2.dto.request.ForgotPasswordRequest;
import cap2.dto.request.LoginRequest;
import cap2.dto.request.ResetPasswordRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.LoginResponse;
import cap2.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.ok("Login successful", response);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String otp = authService.forgotPassword(request);
        return ApiResponse.ok("OTP has been generated. Please check your email or use the returned OTP for testing.", otp);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password has been reset successfully", null);
    }
}
