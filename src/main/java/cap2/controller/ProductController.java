package cap2.controller;

import cap2.dto.request.ProductSearchRequest;
import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.PublicProductResponse;
import cap2.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @GetMapping
    public ApiResponse<PageResponse<PublicProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Lấy danh sách sản phẩm thành công",
                productService.getAllProductsPublic(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<PublicProductResponse> getProductById(@PathVariable String id) {
        return ApiResponse.ok("Lấy sản phẩm thành công", productService.getProductByIdPublic(id));
    }

    /**
     * Tìm kiếm & lọc sản phẩm
     * GET /products/search?keyword=sofa&category=Sofa&minPrice=1000000&maxPrice=20000000&inStock=true&sortBy=price&sortDir=asc&page=0&size=10
     */
    @GetMapping("/search")
    public ApiResponse<PageResponse<PublicProductResponse>> searchProducts(
            @ModelAttribute ProductSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Tìm kiếm sản phẩm thành công",
                productService.searchProductsPublic(request, page, size));
    }
}
