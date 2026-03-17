package cap2.controller;

import cap2.dto.request.DesignCreateRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.DesignResponse;
import cap2.service.DesignRequestService;
import cap2.util.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/design-requests")
@RequiredArgsConstructor
public class DesignRequestController {

    private final DesignRequestService designRequestService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DesignResponse> createDesignRequest(
            @RequestPart("request") @Valid DesignCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        DesignResponse response = designRequestService.createDesignRequest(request, image);
        return ApiResponse.ok("Design request created successfully", response);
    }

    @GetMapping("/my")
    public ApiResponse<Page<DesignResponse>> getMyDesignRequests(
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {
        String userId = SecurityUtils.getCurrentUserId();
        Page<DesignResponse> designRequests = designRequestService.getDesignRequestsByUserId(userId, pageable);
        return ApiResponse.ok(designRequests);
    }

    @GetMapping("/{id}")
    public ApiResponse<DesignResponse> getDesignRequestById(@PathVariable String id) {
        DesignResponse designRequest = designRequestService.getDesignRequestById(id);
        return ApiResponse.ok(designRequest);
    }
}
