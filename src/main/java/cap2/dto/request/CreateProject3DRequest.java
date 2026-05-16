package cap2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProject3DRequest {

    @NotBlank(message = "Design request id is required")
    private String designRequestId;

    private String name;

    // Fixed: allow the client to save the generated layout snapshot as a 3D project.
    private Map<String, Object> sceneData;
}
