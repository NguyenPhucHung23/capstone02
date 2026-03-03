package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.dto.response.PageResponse;
import cap2.dto.response.ProductResponse;
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
}
