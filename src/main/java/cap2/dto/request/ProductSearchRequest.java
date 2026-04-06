package cap2.dto.request;

import lombok.Data;

@Data
public class ProductSearchRequest {

    /** Từ khóa tìm kiếm theo tên sản phẩm */
    private String query;

    /** Danh mục (Sofa, Ghế, Bàn, Giường, ...) */
    private String category;

    /** Giá thấp nhất */
    private Double minPrice;

    /** Giá cao nhất */
    private Double maxPrice;

    /** Lọc theo tình trạng tồn kho */
    private Boolean inStock;

    /** Sắp xếp theo: price | name | soldCount | createdAt (mặc định: createdAt) */
    private String sortBy = "createdAt";

    /** Thứ tự sắp xếp: asc | desc (mặc định: desc) */
    private String sortDir = "desc";
}
