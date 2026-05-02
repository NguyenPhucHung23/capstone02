package cap2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping({"/health", "/"})
    public String health() {
        return "API is running OK. Welcome to Capstone 2!";
    }
}
