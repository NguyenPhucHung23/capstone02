package cap2.controller;

import cap2.dto.request.DesignCreateRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.DesignResponse;
import cap2.service.DesignRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/design-requests")
@RequiredArgsConstructor
public class DesignRequestController {

    private final DesignRequestService designRequestService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<DesignResponse> createDesignRequest(
            @RequestPart("request") String requestStr,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        DesignCreateRequest request = objectMapper.readValue(requestStr, DesignCreateRequest.class);

        DesignResponse response = designRequestService.createDesignRequest(request, files);
        return ApiResponse.ok("Design request created successfully", response);
    }
}
