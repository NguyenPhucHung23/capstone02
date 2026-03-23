package cap2.controller;

import cap2.dto.request.BehaviorEventRequest;
import cap2.service.BehaviorService;
import cap2.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/behaviors")
@RequiredArgsConstructor
public class BehaviorController {

    private final BehaviorService behaviorService;

    @PostMapping
    public ResponseEntity<Void> trackBehavior(@RequestBody BehaviorEventRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        behaviorService.saveEvent(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
