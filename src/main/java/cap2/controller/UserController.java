package cap2.controller;

import cap2.dto.request.RegisterRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.RegisterResponse;
import cap2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = userService.register(request);
        return ApiResponse.ok("Register successfully" ,response);
    }
}