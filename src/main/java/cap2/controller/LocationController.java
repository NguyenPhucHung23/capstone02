package cap2.controller;

import cap2.dto.response.ApiResponse;
import cap2.dto.response.ProvinceResponse;
import cap2.dto.response.WardResponse;
import cap2.service.LocationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationController {

    LocationService locationService;

    /**
     * GET /locations/provinces - Lấy tất cả tỉnh/thành phố
     */
    @GetMapping("/provinces")
    public ApiResponse<List<ProvinceResponse>> getAllProvinces() {
        return ApiResponse.ok("Lấy danh sách tỉnh/thành phố thành công", locationService.getAllProvinces());
    }

    /**
     * GET /locations/provinces/cities - Chỉ thành phố trực thuộc trung ương
     */
    @GetMapping("/provinces/cities")
    public ApiResponse<List<ProvinceResponse>> getCities() {
        return ApiResponse.ok("Lấy danh sách thành phố thành công", locationService.getCities());
    }

    /**
     * GET /locations/provinces/provinces-only - Chỉ tỉnh
     */
    @GetMapping("/provinces/provinces-only")
    public ApiResponse<List<ProvinceResponse>> getProvincesOnly() {
        return ApiResponse.ok("Lấy danh sách tỉnh thành công", locationService.getProvinces());
    }

    /**
     * GET /locations/provinces/{id} - Chi tiết tỉnh/thành theo ID
     */
    @GetMapping("/provinces/{id}")
    public ApiResponse<ProvinceResponse> getProvinceById(@PathVariable String id) {
        return ApiResponse.ok("Lấy thông tin tỉnh/thành phố thành công", locationService.getProvinceById(id));
    }

    /**
     * GET /locations/provinces/{id}/wards - Phường/xã theo provinceId
     */
    @GetMapping("/provinces/{id}/wards")
    public ApiResponse<List<WardResponse>> getWardsByProvinceId(@PathVariable String id) {
        return ApiResponse.ok("Lấy danh sách phường/xã thành công", locationService.getWardsByProvinceId(id));
    }

    /**
     * GET /locations/wards?provinceName=Đà Nẵng - Phường/xã theo tên tỉnh/thành
     */
    @GetMapping("/wards")
    public ApiResponse<List<WardResponse>> getWardsByProvinceName(@RequestParam String provinceName) {
        return ApiResponse.ok("Lấy danh sách phường/xã thành công", locationService.getWardsByProvinceName(provinceName));
    }
}
