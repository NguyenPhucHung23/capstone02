package cap2.service;

import cap2.dto.request.DesignCreateRequest;
import cap2.dto.response.DesignResponse;
import cap2.repository.DesignRequestRepository;
import cap2.schema.DesignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesignRequestService {

    private final DesignRequestRepository designRequestRepository;
    private final Path root = Paths.get("uploads/design-requests");

    public DesignResponse createDesignRequest(DesignCreateRequest request, List<MultipartFile> files) throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                Files.copy(file.getInputStream(), this.root.resolve(filename));
                imageUrls.add("/uploads/design-requests/" + filename);
            }
        }

        DesignRequest.Dimensions dimensions = DesignRequest.Dimensions.builder()
                .width(request.getDimensions().getWidth())
                .length(request.getDimensions().getLength())
                .height(request.getDimensions().getHeight())
                .build();

        DesignRequest designRequest = DesignRequest.builder()
                .roomType(request.getRoomType())
                .dimensions(dimensions)
                .style(request.getStyle())
                .furnitureDensity(request.getFurnitureDensity())
                .gender(request.getGender())
                .imageUrls(imageUrls)
                .build();

        DesignRequest savedDesignRequest = designRequestRepository.save(designRequest);

        DesignResponse.DimensionsResponse dimensionsResponse = DesignResponse.DimensionsResponse.builder()
                .width(savedDesignRequest.getDimensions().getWidth())
                .length(savedDesignRequest.getDimensions().getLength())
                .height(savedDesignRequest.getDimensions().getHeight())
                .build();

        return DesignResponse.builder()
                .id(savedDesignRequest.getId())
                .roomType(savedDesignRequest.getRoomType())
                .dimensions(dimensionsResponse)
                .style(savedDesignRequest.getStyle())
                .furnitureDensity(savedDesignRequest.getFurnitureDensity())
                .gender(savedDesignRequest.getGender())
                .imageUrls(savedDesignRequest.getImageUrls())
                .build();
    }
}
