package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.service.AiLayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-layout")
@RequiredArgsConstructor
public class AiLayoutController {

    private final AiLayoutService aiLayoutService;

    @PostMapping("/generate")
    public ApiResponse<Map<String, Object>> generateLayout(@RequestBody Map<String, Object> payload) {
        Map<String, Object> layout = aiLayoutService.generateLayout(payload);
        return ApiResponse.ok("AI layout generated successfully", layout);
    }
}
