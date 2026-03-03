package cap2.controller;

import cap2.dto.request.ProductRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ProductResponse;
import cap2.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminProductController {

    ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponse> createOrUpdateProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createOrUpdateProduct(request);
        return ApiResponse.ok("Tạo/cập nhật sản phẩm thành công", response);
    }

    @PostMapping("/batch")
    public ApiResponse<List<ProductResponse>> batchCreateOrUpdateProducts(
            @Valid @RequestBody List<ProductRequest> requests) {
        List<ProductResponse> responses = productService.batchCreateOrUpdateProducts(requests);
        return ApiResponse.ok("Import thành công: " + responses.size() + " sản phẩm", responses);
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductResponse> response = productService.getAllProducts(page, size);
        return ApiResponse.ok("Lấy danh sách sản phẩm thành công", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse response = productService.getProductById(id);
        return ApiResponse.ok("Lấy sản phẩm thành công", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.ok("Xóa sản phẩm thành công", null);
    }
}
