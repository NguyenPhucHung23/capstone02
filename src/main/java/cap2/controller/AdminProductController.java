package cap2.controller;

import cap2.dto.request.ProductRequest;
import cap2.dto.request.ProductSearchRequest;
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
        return ApiResponse.ok("Tạo/cập nhật sản phẩm thành công",
                productService.createOrUpdateProduct(request));
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
        return ApiResponse.ok("Lấy danh sách sản phẩm thành công",
                productService.getAllProducts(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        return ApiResponse.ok("Lấy sản phẩm thành công", productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.ok("Xóa sản phẩm thành công", null);
    }

    /**
     * Tìm kiếm & lọc sản phẩm (Admin – xem đầy đủ thông tin)
     * GET /admin/products/search?keyword=sofa&category=Sofa&minPrice=1000000&inStock=true
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<ProductResponse>> searchProducts(
            @ModelAttribute ProductSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Tìm kiếm sản phẩm thành công",
                productService.searchProductsAdmin(request, page, size));
    }
}
