package cap2.service;

import cap2.dto.response.ProvinceResponse;
import cap2.dto.response.WardResponse;
import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import cap2.repository.ProvinceRepository;
import cap2.repository.WardRepository;
import cap2.schema.Province;
import cap2.schema.Ward;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocationService {

    ProvinceRepository provinceRepository;
    WardRepository wardRepository;

    // ===== Province =====

    /**
     * Lấy tất cả tỉnh/thành phố, sắp xếp theo tên
     */
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::mapToProvinceResponse)
                .toList();
    }

    /**
     * Lấy danh sách chỉ thành phố trực thuộc trung ương
     */
    public List<ProvinceResponse> getCities() {
        return provinceRepository.findByType(Province.Type.CITY)
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::mapToProvinceResponse)
                .toList();
    }

    /**
     * Lấy danh sách chỉ tỉnh
     */
    public List<ProvinceResponse> getProvinces() {
        return provinceRepository.findByType(Province.Type.PROVINCE)
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::mapToProvinceResponse)
                .toList();
    }

    /**
     * Lấy chi tiết một tỉnh/thành theo ID
     */
    public ProvinceResponse getProvinceById(String id) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND));
        return mapToProvinceResponse(province);
    }

    // ===== Ward =====

    /**
     * Lấy danh sách phường/xã theo cityId
     */
    public List<WardResponse> getWardsByProvinceId(String cityId) {
        if (!provinceRepository.existsById(cityId)) {
            throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
        }
        return wardRepository.findByCityId(cityId)
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::mapToWardResponse)
                .toList();
    }

    /**
     * Lấy danh sách phường/xã theo tên tỉnh/thành phố
     */
    public List<WardResponse> getWardsByProvinceName(String provinceName) {
        Province province = provinceRepository.findByName(provinceName)
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND));
        return wardRepository.findByCityId(province.getId())
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::mapToWardResponse)
                .toList();
    }

    // ===== Mappers =====

    private ProvinceResponse mapToProvinceResponse(Province province) {
        return ProvinceResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .type(province.getType().name())
                .typeDisplay(province.getType() == Province.Type.CITY ? "Thành phố" : "Tỉnh")
                .build();
    }

    private WardResponse mapToWardResponse(Ward ward) {
        return WardResponse.builder()
                .id(ward.getId())
                .name(ward.getName())
                .cityId(ward.getCityId())
                .build();
    }
}
