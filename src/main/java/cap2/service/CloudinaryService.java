package cap2.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload image to Cloudinary
     * @param file MultipartFile from request
     * @param folder Folder path on Cloudinary (e.g., "design-requests")
     * @return secure_url of uploaded image
     */
    public String uploadImage(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                log.warn("Empty file provided for upload");
                return null;
            }

            // Generate public_id to avoid conflicts
            String publicId = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "public_id", publicId,
                            "resource_type", "image",
                            "quality", "auto",
                            "fetch_format", "auto"
                    )
            );

            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded successfully to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("IO error while uploading image to Cloudinary: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
            return null;
        }
    }
}
