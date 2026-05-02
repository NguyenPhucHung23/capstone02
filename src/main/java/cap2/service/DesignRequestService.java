package cap2.service;

import cap2.dto.request.DesignCreateRequest;
import cap2.dto.response.AiProductResponse;
import cap2.dto.response.AiRecommendResponse;
import cap2.dto.response.DesignResponse;
import cap2.exception.NotFoundException;
import cap2.repository.DesignRequestRepository;
import cap2.schema.DesignRequest;
import cap2.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesignRequestService {

    private final DesignRequestRepository designRequestRepository;
    private final RestTemplate restTemplate;

    @Value("${ai.api.url}")
    private String aiApiUrl;

    public DesignResponse createDesignRequest(DesignCreateRequest request, MultipartFile image) throws IOException {
        String userId = SecurityUtils.getCurrentUserId();

        DesignRequest.Dimensions dimensions = DesignRequest.Dimensions.builder()
                .width(request.getDimensions().getWidth())
                .length(request.getDimensions().getLength())
                .height(request.getDimensions().getHeight())
                .build();

        DesignRequest designRequest = DesignRequest.builder()
                .userId(userId)
                .roomType(request.getRoomType())
                .dimensions(dimensions)
                .style(request.getStyle())
                .furnitureDensity(request.getFurnitureDensity())
                .gender(request.getGender())
                .age(request.getAge())
                .build();

        // Call AI service
        AiRecommendResponse aiResponse = callAiService(designRequest, image);

        if (aiResponse != null && aiResponse.getAnalysis() != null) {
            designRequest.setReasoning(aiResponse.getAnalysis().getReasoning());
        }

        List<String> recommendedProductIds = aiResponse != null ?
                aiResponse.getProducts().stream().map(AiProductResponse::getId).collect(Collectors.toList())
                : Collections.emptyList();
        designRequest.setRecommendedProductIds(recommendedProductIds);

        DesignRequest savedDesignRequest = designRequestRepository.save(designRequest);
        log.info("Design request {} created for user {}", savedDesignRequest.getId(), userId);


        return convertToDesignResponse(savedDesignRequest, aiResponse);
    }

    private AiRecommendResponse callAiService(DesignRequest designRequest, MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("room_type", designRequest.getRoomType());
        body.add("style", designRequest.getStyle());
        body.add("width", designRequest.getDimensions().getWidth());
        body.add("length", designRequest.getDimensions().getLength());
        body.add("height", designRequest.getDimensions().getHeight());
        body.add("furniture_density", designRequest.getFurnitureDensity());
        body.add("gender", designRequest.getGender());
        body.add("age", designRequest.getAge());
        body.add("user_id", designRequest.getUserId());

        if (image != null && !image.isEmpty()) {
            ByteArrayResource resource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };
            body.add("image", resource);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<AiRecommendResponse> response = restTemplate.postForEntity(aiApiUrl, requestEntity, AiRecommendResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage(), e);
            return null;
        }
    }

    public Page<DesignResponse> getDesignRequestsByUserId(String userId, Pageable pageable) {
        Page<DesignRequest> designRequests = designRequestRepository.findByUserId(userId, pageable);
        return designRequests.map(dr -> convertToDesignResponse(dr, null)); // AI response not needed for history
    }

    public DesignResponse getDesignRequestById(String id) {
        DesignRequest designRequest = designRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(cap2.exception.ErrorCode.DESIGN_REQUEST_NOT_FOUND));
        SecurityUtils.checkPermission(designRequest.getUserId());
        return convertToDesignResponse(designRequest, null); // AI response not needed for detail view
    }

    private DesignResponse convertToDesignResponse(DesignRequest designRequest, AiRecommendResponse aiResponse) {

    DesignResponse.DimensionsResponse dimensionsResponse = DesignResponse.DimensionsResponse.builder()
            .width(designRequest.getDimensions().getWidth())
            .length(designRequest.getDimensions().getLength())
            .height(designRequest.getDimensions().getHeight())
            .build();

    List<String> dominantColors = Collections.emptyList();
    String colorTone = null;
    String detectedStyle = null;

    if (aiResponse != null &&
        aiResponse.getAnalysis() != null &&
        aiResponse.getAnalysis().getImageAnalysis() != null) {

        dominantColors = aiResponse.getAnalysis().getImageAnalysis().getDominantColors();
        colorTone = aiResponse.getAnalysis().getImageAnalysis().getColorTone();
        detectedStyle = aiResponse.getAnalysis().getImageAnalysis().getDetectedStyle();
    }

    return DesignResponse.builder()
            .id(designRequest.getId())
            .roomType(designRequest.getRoomType())
            .dimensions(dimensionsResponse)
            .style(designRequest.getStyle())
            .furnitureDensity(designRequest.getFurnitureDensity())
            .gender(designRequest.getGender())
            .imageUrl(designRequest.getImageUrl())
            .reasoning(designRequest.getReasoning())
            .recommendedProducts(aiResponse != null ? aiResponse.getProducts() : Collections.emptyList())

            .dominantColors(dominantColors)
            .colorTone(colorTone)
            .detectedStyle(detectedStyle)

            .createdAt(designRequest.getCreatedAt())
            .build();
}
}
