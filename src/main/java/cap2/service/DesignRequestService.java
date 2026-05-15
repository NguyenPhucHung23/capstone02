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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesignRequestService {

    private final DesignRequestRepository designRequestRepository;
    private final RestTemplate restTemplate;
    private final CloudinaryService cloudinaryService;
    private final AiLayoutService aiLayoutService;

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

        // Upload image to Cloudinary if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(image, "design-requests");
            if (imageUrl != null) {
                designRequest.setImageUrl(imageUrl);
                log.info("Image uploaded to Cloudinary: {}", imageUrl);
            } else {
                log.warn("Failed to upload image to Cloudinary, continuing without image");
            }
        }

        // Call AI service
        AiRecommendResponse aiResponse = callAiService(designRequest, image);

        if (aiResponse != null && aiResponse.getAnalysis() != null) {
            designRequest.setReasoning(formatReasoning(aiResponse.getAnalysis().getReasoning()));
            
            // Store detailed reasoning for granular display
            if (aiResponse.getAnalysis().getReasoning() != null) {
                Map<String, String> reasoningDetails = new HashMap<>();
                var reasoning = aiResponse.getAnalysis().getReasoning();
                reasoningDetails.put("styleJustification", reasoning.getStyleJustification() != null ? reasoning.getStyleJustification() : "");
                reasoningDetails.put("colorJustification", reasoning.getColorJustification() != null ? reasoning.getColorJustification() : "");
                reasoningDetails.put("densityJustification", reasoning.getDensityJustification() != null ? reasoning.getDensityJustification() : "");
                reasoningDetails.put("userProfileNote", reasoning.getUserProfileNote() != null ? reasoning.getUserProfileNote() : "");
                designRequest.setReasoningDetails(reasoningDetails);
            }
        }

        if (aiResponse != null) {
            designRequest.setWarning(aiResponse.getWarning());
            designRequest.setDensityApplied(aiResponse.getDensityApplied());
        }

        List<AiProductResponse> aiProducts = safeProducts(aiResponse);

        List<String> recommendedProductIds = aiProducts.stream()
                .map(AiProductResponse::getId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());

        designRequest.setRecommendedProductIds(recommendedProductIds);

        List<DesignRequest.ProductSnapshot> snapshots = aiProducts.stream()
                .map(this::toProductSnapshot)
                .collect(Collectors.toList());

        designRequest.setRecommendedProductSnapshots(snapshots);

        // Gọi FastAPI layout 
        Map<String, Object> layout = callAiLayoutService(designRequest, aiResponse);
        designRequest.setLayout(layout);

        DesignRequest savedDesignRequest = designRequestRepository.save(designRequest);
        log.info("Design request {} created for user {}", savedDesignRequest.getId(), userId);


        return convertToDesignResponse(savedDesignRequest, aiResponse);
    }

    private List<AiProductResponse> safeProducts(AiRecommendResponse aiResponse) {
        if (aiResponse == null || aiResponse.getProducts() == null) {
            return Collections.emptyList();
        }
        return aiResponse.getProducts();
    }

    private AiRecommendResponse callAiService(DesignRequest designRequest, MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        double areaM2 = designRequest.getDimensions().getWidth() * designRequest.getDimensions().getLength();
        body.add("room_type", designRequest.getRoomType());
        body.add("style", designRequest.getStyle());
        body.add("width", designRequest.getDimensions().getWidth());
        body.add("length", designRequest.getDimensions().getLength());
        body.add("height", designRequest.getDimensions().getHeight());
        body.add("area_m2", areaM2);
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

    private String formatReasoning(AiRecommendResponse.AnalysisReasoning reasoning) {
        if (reasoning == null) {
            return null;
        }

        return Stream.of(
            reasoning.getStyleJustification(),
            reasoning.getColorJustification(),
            reasoning.getDensityJustification(),
            reasoning.getUserProfileNote()
        )
        .filter(value -> value != null && !value.isBlank())
        .collect(Collectors.joining("\n"));
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
    Map<String, String> reasoningDetails = new HashMap<>();

    if (aiResponse != null &&
        aiResponse.getAnalysis() != null &&
        aiResponse.getAnalysis().getImageAnalysis() != null) {

        dominantColors = aiResponse.getAnalysis().getImageAnalysis().getDominantColors();
        colorTone = aiResponse.getAnalysis().getImageAnalysis().getColorTone();
        detectedStyle = aiResponse.getAnalysis().getImageAnalysis().getDetectedStyle();
    }

    if (aiResponse != null &&
        aiResponse.getAnalysis() != null &&
        aiResponse.getAnalysis().getReasoning() != null) {
        var reasoning = aiResponse.getAnalysis().getReasoning();
        reasoningDetails.put("styleJustification", reasoning.getStyleJustification() != null ? reasoning.getStyleJustification() : "");
        reasoningDetails.put("colorJustification", reasoning.getColorJustification() != null ? reasoning.getColorJustification() : "");
        reasoningDetails.put("densityJustification", reasoning.getDensityJustification() != null ? reasoning.getDensityJustification() : "");
        reasoningDetails.put("userProfileNote", reasoning.getUserProfileNote() != null ? reasoning.getUserProfileNote() : "");
    }
    
    // Also store reasoning details in DesignRequest for persistence
    if (!reasoningDetails.isEmpty() && designRequest.getReasoningDetails() == null) {
        designRequest.setReasoningDetails(reasoningDetails);
    }

        List<AiProductResponse> recommendedProducts = aiResponse != null
            ? aiResponse.getProducts()
            : fromProductSnapshots(designRequest.getRecommendedProductSnapshots());

        return DesignResponse.builder()
            .id(designRequest.getId())
            .roomType(designRequest.getRoomType())
            .dimensions(dimensionsResponse)
            .style(designRequest.getStyle())
            .furnitureDensity(designRequest.getFurnitureDensity())
            .gender(designRequest.getGender())
            .imageUrl(designRequest.getImageUrl())
            .reasoning(designRequest.getReasoning())
            .reasoningDetails(designRequest.getReasoningDetails() != null ? designRequest.getReasoningDetails() : reasoningDetails)
            .recommendedProducts(recommendedProducts)
            .dominantColors(dominantColors)
            .colorTone(colorTone)
            .detectedStyle(detectedStyle)
            .warning(designRequest.getWarning())
            .densityApplied(designRequest.getDensityApplied())
            .layout(designRequest.getLayout())
            
            .createdAt(designRequest.getCreatedAt())
            .build();
}

    private DesignRequest.ProductSnapshot toProductSnapshot(AiProductResponse product) {
        return DesignRequest.ProductSnapshot.builder()
                .productId(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .reasoning(product.getReasoning())
                .rankingScore(null)
                .styleScore(product.getStyleScore())
                .colorScore(product.getColorScore())
                .styles(product.getStyles())
                .colors(product.getColors())
                .modelUrl(product.getModelUrl())
                .dimensions(toProductDimensionsSnapshot(product.getDimensions()))
                .build();
    }

    private DesignRequest.ProductDimensionsSnapshot toProductDimensionsSnapshot(
            AiProductResponse.AiProductDimensions dimensions
    ) {
        if (dimensions == null) {
            return null;
        }

        return DesignRequest.ProductDimensionsSnapshot.builder()
                .width(dimensions.getWidth())
                .depth(dimensions.getDepth())
                .height(dimensions.getHeight())
                .build();
    }

    private AiProductResponse.AiProductDimensions fromProductDimensionsSnapshot(
            DesignRequest.ProductDimensionsSnapshot dimensions
    ) {
        if (dimensions == null) {
            return null;
        }

        AiProductResponse.AiProductDimensions response = new AiProductResponse.AiProductDimensions();
        response.setWidth(dimensions.getWidth());
        response.setDepth(dimensions.getDepth());
        response.setHeight(dimensions.getHeight());
        return response;
    }

    private List<AiProductResponse> fromProductSnapshots(List<DesignRequest.ProductSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return Collections.emptyList();
        }

        return snapshots.stream().map(snapshot -> {
            AiProductResponse product = new AiProductResponse();
            product.setId(snapshot.getProductId());
            product.setName(snapshot.getName());
            product.setCategory(snapshot.getCategory());
            product.setPrice(snapshot.getPrice());
            product.setImageUrl(snapshot.getImageUrl());
            product.setReasoning(snapshot.getReasoning());
            product.setStyles(snapshot.getStyles());
            product.setColors(snapshot.getColors());
            product.setModelUrl(snapshot.getModelUrl());
            product.setRankingScore(snapshot.getRankingScore());
            product.setStyleScore(snapshot.getStyleScore());
            product.setColorScore(snapshot.getColorScore());
            product.setDimensions(fromProductDimensionsSnapshot(snapshot.getDimensions()));
            return product;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> callAiLayoutService(DesignRequest designRequest, AiRecommendResponse aiResponse) {
        if (safeProducts(aiResponse).isEmpty()) {
        return Collections.emptyMap();
    }

    Map<String, Object> payload = buildAiLayoutPayload(designRequest, aiResponse);

    try {
        return aiLayoutService.generateLayout(payload);
    } catch (Exception e) {
        log.error("Error calling AI layout service: {}", e.getMessage(), e);
        return Collections.emptyMap();
    }
}

    private Map<String, Object> buildAiLayoutPayload(DesignRequest designRequest, AiRecommendResponse aiResponse) {
        Map<String, Object> payload = new LinkedHashMap<>();

        Map<String, Object> room = new LinkedHashMap<>();
        room.put("widthM", designRequest.getDimensions().getWidth());
        room.put("lengthM", designRequest.getDimensions().getLength());
        room.put("heightM", designRequest.getDimensions().getHeight());
        room.put("type", normalizeRoomType(designRequest.getRoomType()));
        room.put("style", designRequest.getStyle());

        List<AiProductResponse> products = safeProducts(aiResponse);

        Map<String, Object> recommendation = new LinkedHashMap<>();
        recommendation.put("analysis", aiResponse.getAnalysis());
        recommendation.put("products", products);

        payload.put("room", room);
        payload.put("recommendation", recommendation);
        payload.put("furnitureDensity", designRequest.getFurnitureDensity());
        int layoutTopK = Math.min(products.size(), 6);
        payload.put("topK", Math.max(layoutTopK, 1));
        payload.put("minScore", 0.2);
        payload.put("modelUrlById", buildModelUrlById(products));
        return payload;
    }

    private String normalizeRoomType(String roomType) {
        if (roomType == null) {
            return "living_room";
        }

        return switch (roomType.trim().toLowerCase()) {
            case "living room", "phòng khách", "phong khach", "living_room" -> "living_room";
            case "bedroom", "phòng ngủ", "phong ngu" -> "bedroom";
            default -> roomType.trim().toLowerCase().replace(" ", "_");
        };
    }

    private Map<String, String> buildModelUrlById(List<AiProductResponse> products) {
        Map<String, String> modelUrlById = new LinkedHashMap<>();

        if (products == null) {
            return modelUrlById;
        }

        for (AiProductResponse product : products) {
            String modelUrl = product.getModelUrl();

            if (product.getId() != null && modelUrl != null && !modelUrl.isBlank()) {
                modelUrlById.put(product.getId(), modelUrl);
            }
        }

        return modelUrlById;
    }

}
