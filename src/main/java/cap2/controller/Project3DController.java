package cap2.controller;

import cap2.dto.request.CreateProject3DRequest;
import cap2.dto.request.SaveEditedProductsRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.Project3DResponse;
import cap2.service.Project3DService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects-3d")
@RequiredArgsConstructor
public class Project3DController {

    private final Project3DService project3DService;

    @PostMapping
    public ApiResponse<Project3DResponse> createProject(@RequestBody @Valid CreateProject3DRequest request) {
        Project3DResponse response = project3DService.createProject(request);
        return ApiResponse.ok("3D project saved successfully", response);
    }

    @PostMapping("/{id}/edited-products")
    public ApiResponse<Project3DResponse> saveEditedProducts(
            @PathVariable String id,
            @RequestBody @Valid SaveEditedProductsRequest request) {
        Project3DResponse response = project3DService.saveEditedProducts(id, request);
        return ApiResponse.ok("Edited products saved successfully", response);
    }

    @GetMapping("/my")
    public ApiResponse<List<Project3DResponse>> getMyProjects() {
        return ApiResponse.ok(project3DService.getProjectsByCurrentUser());
    }

    @GetMapping("/{id}")
    public ApiResponse<Project3DResponse> getProjectById(@PathVariable String id) {
        return ApiResponse.ok(project3DService.getProjectById(id));
    }
}
