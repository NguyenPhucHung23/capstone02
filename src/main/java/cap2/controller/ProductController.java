package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.PublicProductResponse;
import cap2.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        PageResponse<PublicProductResponse> response = productService.getAllProductsPublic(page, size);
        return ApiResponse.ok("Lấy danh sách sản phẩm thành công", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<PublicProductResponse> getProductById(@PathVariable String id) {
        PublicProductResponse response = productService.getProductByIdPublic(id);
        return ApiResponse.ok("Lấy sản phẩm thành công", response);
    }
}
