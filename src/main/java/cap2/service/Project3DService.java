package cap2.service;

import cap2.dto.request.CreateProject3DRequest;
import cap2.dto.request.SaveEditedProductsRequest;
import cap2.dto.response.Project3DResponse;
import cap2.exception.NotFoundException;
import cap2.repository.DesignRequestRepository;
import cap2.repository.Project3DRepository;
import cap2.schema.DesignRequest;
import cap2.schema.Project3D;
import cap2.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Project3DService {

    private final Project3DRepository project3DRepository;
    private final DesignRequestRepository designRequestRepository;

    public Project3DResponse createProject(CreateProject3DRequest request) {
        DesignRequest designRequest = designRequestRepository.findById(request.getDesignRequestId())
                .orElseThrow(() -> new NotFoundException(cap2.exception.ErrorCode.DESIGN_REQUEST_NOT_FOUND));

        SecurityUtils.checkPermission(designRequest.getUserId());

        Instant now = Instant.now();
        Project3D project = Project3D.builder()
                .designRequestId(designRequest.getId())
                .userId(designRequest.getUserId())
                .name(request.getName() != null && !request.getName().isBlank() ? request.getName() : defaultProjectName(designRequest))
                .sceneData(request.getSceneData() != null ? request.getSceneData() : designRequest.getLayout())
                .editedProducts(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Project3D saved = project3DRepository.save(project);
        return toResponse(saved);
    }

    public Project3DResponse saveEditedProducts(String projectId, SaveEditedProductsRequest request) {
        Project3D project = project3DRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(cap2.exception.ErrorCode.DESIGN_REQUEST_NOT_FOUND));

        SecurityUtils.checkPermission(project.getUserId());

        List<Project3D.EditedProduct> editedProducts = request.getEditedProducts() == null
                ? new ArrayList<>()
                : request.getEditedProducts().stream()
                .map(item -> Project3D.EditedProduct.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .category(item.getCategory())
                        .modelUrl(item.getModelUrl())
                        .price(item.getPrice())
                        .x(item.getX())
                        .y(item.getY())
                        .z(item.getZ())
                        .rotationX(item.getRotationX())
                        .rotationY(item.getRotationY())
                        .rotationZ(item.getRotationZ())
                        .scaleX(item.getScaleX())
                        .scaleY(item.getScaleY())
                        .scaleZ(item.getScaleZ())
                        .width(item.getWidth())
                        .depth(item.getDepth())
                        .height(item.getHeight())
                        .metadata(item.getMetadata())
                        .build())
                .toList();

        project.setEditedProducts(editedProducts);
        if (request.getSceneData() != null) {
            // Fixed: allow the client to persist the final scene snapshot after product edits.
            project.setSceneData(request.getSceneData());
        }
        project.setUpdatedAt(Instant.now());

        Project3D saved = project3DRepository.save(project);
        return toResponse(saved);
    }

    public List<Project3DResponse> getProjectsByCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        return project3DRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Project3DResponse getProjectById(String projectId) {
        Project3D project = project3DRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(cap2.exception.ErrorCode.DESIGN_REQUEST_NOT_FOUND));
        SecurityUtils.checkPermission(project.getUserId());
        return toResponse(project);
    }

    private String defaultProjectName(DesignRequest designRequest) {
        return "3D Project - " + Optional.ofNullable(designRequest.getRoomType()).orElse("Unknown Room");
    }

    private Project3DResponse toResponse(Project3D project) {
        List<Project3DResponse.EditedProductResponse> editedProducts = project.getEditedProducts() == null
                ? new ArrayList<>()
                : project.getEditedProducts().stream()
                .map(item -> Project3DResponse.EditedProductResponse.builder()
                        .productId(item.getProductId())
                        .name(item.getName())
                        .category(item.getCategory())
                        .modelUrl(item.getModelUrl())
                        .price(item.getPrice())
                        .x(item.getX())
                        .y(item.getY())
                        .z(item.getZ())
                        .rotationX(item.getRotationX())
                        .rotationY(item.getRotationY())
                        .rotationZ(item.getRotationZ())
                        .scaleX(item.getScaleX())
                        .scaleY(item.getScaleY())
                        .scaleZ(item.getScaleZ())
                        .width(item.getWidth())
                        .depth(item.getDepth())
                        .height(item.getHeight())
                        .metadata(item.getMetadata())
                        .build())
                .toList();

        return Project3DResponse.builder()
                .id(project.getId())
                .designRequestId(project.getDesignRequestId())
                .userId(project.getUserId())
                .name(project.getName())
                .sceneData(project.getSceneData())
                .editedProducts(editedProducts)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
