package cap2.service;

import cap2.dto.request.ForgotPasswordRequest;
import cap2.dto.request.LoginRequest;
import cap2.dto.request.ResetPasswordRequest;
import cap2.dto.response.LoginResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.UserRepository;
import cap2.schema.User;
import cap2.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthService {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final JwtUtil jwtUtil;
    final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String fromEmail;

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        // Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        // Tạo JWT token
        String token = jwtUtil.generateToken(user);
        log.info("Login successful for user: {}", user.getId());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .role(user.getRole().name())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Generate 6-digit OTP
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String otp = String.format("%06d", number);

        user.setOtp(otp);
        user.setOtpExpires(LocalDateTime.now().plusHours(1)); // OTP expires in 1 hour
        userRepository.save(user);

        // Send email
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP for password reset is: " + otp);
            mailSender.send(message);
            log.info("OTP sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        if (user.getOtpExpires().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED_TOKEN);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpires(null);

        userRepository.save(user);
        log.info("Password reset successfully for user: {}", user.getId());
    }
}
