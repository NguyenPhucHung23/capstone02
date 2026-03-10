package cap2.config;

import cap2.repository.ProvinceRepository;
import cap2.repository.WardRepository;
import cap2.schema.Province;
import cap2.schema.Ward;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seed dữ liệu 34 đơn vị hành chính cấp tỉnh (2025):
 * - 6 thành phố trực thuộc TW: Hà Nội, Hải Phòng, Huế, Đà Nẵng, Cần Thơ, TPHCM
 * - 28 tỉnh theo danh sách chính thức
 * Tổng phường/xã ~1200 (chỉ lấy phổ biến)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationDataSeeder implements ApplicationRunner {

    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    @Override
    @SuppressWarnings("NullableProblems")
    public void run(ApplicationArguments args) {
        if (provinceRepository.count() > 0) {
            log.info("Location data already seeded, skipping.");
            return;
        }
        log.info("Seeding location data...");
        seedLocations();
        log.info("Location data seeded successfully.");
    }

    private void seedLocations() {
        List<Object[]> data = buildSeedData();
        for (Object[] entry : data) {
            String name = (String) entry[0];
            Province.Type type = (Province.Type) entry[1];
            @SuppressWarnings("unchecked")
            List<String> wardNames = (List<String>) entry[2];

            Province province = provinceRepository.save(
                    Province.builder().name(name).type(type).build()
            );
            List<Ward> wards = wardNames.stream()
                    .map(wn -> Ward.builder().name(wn).cityId(province.getId()).build())
                    .toList();
            wardRepository.saveAll(wards);
        }
    }

    private List<Object[]> buildSeedData() {
        List<Object[]> list = new ArrayList<>();

        // =============================================
        // 6 THÀNH PHỐ TRỰC THUỘC TRUNG ƯƠNG
        // =============================================

        list.add(new Object[]{"Hà Nội", Province.Type.CITY, List.of(
                "Phường Hoàn Kiếm", "Phường Hàng Bạc", "Phường Hàng Bồ",
                "Phường Hàng Buồm", "Phường Hàng Đào", "Phường Hàng Gai",
                "Phường Cửa Đông", "Phường Đống Đa", "Phường Văn Miếu",
                "Phường Khâm Thiên", "Phường Phương Liên", "Phường Nam Đồng",
                "Phường Ba Đình", "Phường Điện Biên", "Phường Trúc Bạch",
                "Phường Nguyễn Trung Trực", "Phường Kim Mã", "Phường Cống Vị",
                "Phường Liễu Giai", "Phường Vĩnh Phúc",
                "Phường Hai Bà Trưng", "Phường Bách Khoa", "Phường Bạch Đằng",
                "Phường Tây Hồ", "Phường Quảng An", "Phường Nhật Tân",
                "Phường Cầu Giấy", "Phường Nghĩa Đô", "Phường Dịch Vọng",
                "Phường Thanh Xuân", "Phường Nhân Chính", "Phường Hạ Đình",
                "Phường Hoàng Mai", "Phường Tương Mai", "Phường Lĩnh Nam",
                "Phường Hà Đông", "Phường Mỗ Lao", "Phường Văn Quán",
                "Xã Đông Anh", "Xã Gia Lâm", "Xã Sóc Sơn",
                "Xã Hoài Đức", "Xã Thanh Trì", "Xã Từ Liêm"
        )});

        list.add(new Object[]{"Hồ Chí Minh", Province.Type.CITY, List.of(
                "Phường Bến Nghé", "Phường Bến Thành", "Phường Nguyễn Thái Bình",
                "Phường Cầu Kho", "Phường Cầu Ông Lãnh", "Phường Đa Kao",
                "Phường Tân Định", "Phường Phạm Ngũ Lão", "Phường Võ Thị Sáu",
                "Phường Bình Thạnh", "Phường Phước Long B", "Phường Hiệp Bình Chánh",
                "Phường Tân Phú", "Phường Bình Hưng Hòa", "Phường Tân Sơn Nhì",
                "Phường Thới An", "Phường An Lạc", "Phường Tân Tạo",
                "Phường Bình Trị Đông", "Phường Linh Trung",
                "Phường Hiệp Phú", "Phường Long Thạnh Mỹ", "Phường Tăng Nhơn Phú A",
                "Phường Phú Hữu", "Phường Thảo Điền", "Phường An Phú",
                "Phường Bình An", "Phường Bình Khánh", "Phường An Khánh",
                "Phường Thủ Thiêm",
                "Xã Bình Chánh", "Xã Hóc Môn", "Xã Củ Chi",
                "Xã Nhà Bè", "Xã Cần Giờ", "Xã Vĩnh Lộc A",
                "Xã Vĩnh Lộc B", "Xã Bình Lợi"
        )});

        list.add(new Object[]{"Hải Phòng", Province.Type.CITY, List.of(
                "Phường Máy Chai", "Phường Máy Tơ", "Phường Vạn Mỹ",
                "Phường Cầu Tre", "Phường Lạc Viên", "Phường Gia Viên",
                "Phường Đông Khê", "Phường Cầu Đất", "Phường Lê Lợi",
                "Phường An Biên", "Phường Đằng Giang", "Phường Thượng Lý",
                "Phường Đằng Lâm", "Phường Tràng Cát", "Phường Anh Dũng",
                "Phường Hải Thành", "Phường Đông Hải 1", "Phường Đông Hải 2",
                "Phường Nam Hải", "Phường Tân Thành",
                "Xã Tân Liên", "Xã Kiến Thiết", "Xã Thủy Triều",
                "Xã Hoàng Động", "Xã Đại Hà"
        )});

        list.add(new Object[]{"Đà Nẵng", Province.Type.CITY, List.of(
                "Phường Hải Châu 1", "Phường Hải Châu 2", "Phường Thạch Thang",
                "Phường Nam Dương", "Phường Phước Ninh", "Phường Hòa Thuận Đông",
                "Phường Hòa Thuận Tây", "Phường Bình Hiên", "Phường Bình Thuận",
                "Phường Hòa Cường Bắc", "Phường Hòa Cường Nam",
                "Phường Thanh Bình", "Phường Thuận Phước", "Phường Thọ Quang",
                "Phường Mân Thái", "Phường An Hải Bắc", "Phường Phước Mỹ",
                "Phường An Hải Tây", "Phường An Hải Đông",
                "Phường Mỹ An", "Phường Khuê Mỹ", "Phường Hòa Quý",
                "Phường Hòa Hải", "Phường Liên Chiểu", "Phường Hòa Minh",
                "Phường Hòa Khánh Bắc", "Phường Hòa Khánh Nam",
                "Xã Hòa Bắc", "Xã Hòa Liên", "Xã Hòa Sơn",
                "Xã Hòa Phú", "Xã Hòa Phong", "Xã Hòa Châu",
                "Xã Hòa Tiến", "Xã Hòa Nhơn"
        )});

        list.add(new Object[]{"Huế", Province.Type.CITY, List.of(
                "Phường An Cựu", "Phường An Đông", "Phường An Hòa",
                "Phường An Tây", "Phường Đúc", "Phường Hương Long",
                "Phường Kim Long", "Phường Phú Bình", "Phường Phú Cát",
                "Phường Phú Hậu", "Phường Phú Hòa", "Phường Phú Hội",
                "Phường Phú Nhuận", "Phường Phú Thuận", "Phường Phước Vĩnh",
                "Phường Tây Lộc", "Phường Thuận Hòa", "Phường Thuận Lộc",
                "Phường Thuận Thành", "Phường Trường An", "Phường Vỹ Dạ",
                "Phường Xuân Phú",
                "Xã Thủy Xuân", "Xã Thủy Biều", "Xã Hương Vinh",
                "Xã Phú Dương", "Xã Phú Mậu", "Xã Phú Thượng"
        )});

        list.add(new Object[]{"Cần Thơ", Province.Type.CITY, List.of(
                "Phường An Hòa", "Phường An Khánh", "Phường An Nghiệp",
                "Phường Cái Khế", "Phường Hưng Lợi", "Phường Tân An",
                "Phường Thới Bình", "Phường Xuân Khánh", "Phường Châu Văn Liêm",
                "Phường Thới Hòa", "Phường Thới Long", "Phường Trường Lạc",
                "Phường Hưng Phú", "Phường Hưng Thạnh", "Phường Long Hòa",
                "Phường Long Tuyền", "Phường Lê Bình", "Phường Hưng Lợi",
                "Phường An Bình", "Phường Bùi Hữu Nghĩa",
                "Xã Trung Kiên", "Xã Trung Nhứt", "Xã Thới Đông",
                "Xã Thới Lai", "Xã Giai Xuân"
        )});

        // =============================================
        // 28 TỈNH (theo danh sách chính thức 2025)
        // =============================================

        list.add(new Object[]{"An Giang", Province.Type.PROVINCE, List.of(
                "Phường Mỹ Bình", "Phường Mỹ Long", "Phường Đông Xuyên",
                "Phường Mỹ Xuyên", "Phường Bình Đức", "Phường Bình Khánh",
                "Phường Mỹ Phước", "Phường Mỹ Quý", "Phường Mỹ Thới",
                "Phường Núi Sam", "Phường Vĩnh Mỹ",
                "Xã Mỹ Hòa Hưng", "Xã Vĩnh Trạch", "Xã Bình Hòa",
                "Xã Phú Hòa", "Xã Vĩnh Châu"
        )});

        list.add(new Object[]{"Bắc Ninh", Province.Type.PROVINCE, List.of(
                "Phường Đáp Cầu", "Phường Kinh Bắc", "Phường Ninh Xá",
                "Phường Suối Hoa", "Phường Tiền An", "Phường Vệ An",
                "Phường Vũ Ninh", "Phường Thị Cầu", "Phường Hòa Long",
                "Phường Vạn An",
                "Xã Khúc Xuyên", "Xã Nam Sơn", "Xã Phong Khê",
                "Xã Kim Chân", "Xã Tam Giang"
        )});

        list.add(new Object[]{"Cà Mau", Province.Type.PROVINCE, List.of(
                "Phường 1", "Phường 2", "Phường 4", "Phường 5",
                "Phường 6", "Phường 7", "Phường 8", "Phường 9",
                "Xã An Xuyên", "Xã Định Bình", "Xã Hòa Thành",
                "Xã Hòa Tân", "Xã Lý Văn Lâm", "Xã Tắc Vân"
        )});

        list.add(new Object[]{"Cao Bằng", Province.Type.PROVINCE, List.of(
                "Phường Đề Thám", "Phường Hợp Giang", "Phường Ngọc Xuân",
                "Phường Sông Bằng", "Phường Sông Hiến", "Phường Tân Giang",
                "Xã Chu Trinh", "Xã Duyệt Trung", "Xã Hưng Đạo",
                "Xã Vĩnh Quang"
        )});

        list.add(new Object[]{"Đắk Lắk", Province.Type.PROVINCE, List.of(
                "Phường An Lạc", "Phường Ea Tam", "Phường Khánh Xuân",
                "Phường Tân An", "Phường Tân Hòa", "Phường Tân Lập",
                "Phường Tân Lợi", "Phường Tân Thành", "Phường Tân Tiến",
                "Phường Thắng Lợi", "Phường Thành Công", "Phường Thành Nhất",
                "Xã Cư Êbur", "Xã Ea Tu", "Xã Hòa Khánh",
                "Xã Hòa Phú", "Xã Hòa Thắng"
        )});

        list.add(new Object[]{"Điện Biên", Province.Type.PROVINCE, List.of(
                "Phường Him Lam", "Phường Mường Thanh", "Phường Nam Thanh",
                "Phường Noong Bua", "Phường Tân Thanh", "Phường Thanh Bình",
                "Phường Thanh Trường",
                "Xã Thanh Minh", "Xã Nà Tấu", "Xã Tà Lèng",
                "Xã Hua Thanh"
        )});

        list.add(new Object[]{"Đồng Nai", Province.Type.PROVINCE, List.of(
                "Phường Hố Nai", "Phường Long Bình", "Phường Tam Hòa",
                "Phường Tân Biên", "Phường Tân Hiệp", "Phường Tân Hòa",
                "Phường Tân Mai", "Phường Tân Phong", "Phường Tân Tiến",
                "Phường Trung Dũng", "Phường Quyết Thắng", "Phường Long Bình Tân",
                "Xã Hiệp Hòa", "Xã Hóa An", "Xã Long Hưng",
                "Xã Phước Tân", "Xã Tam Phước"
        )});

        list.add(new Object[]{"Đồng Tháp", Province.Type.PROVINCE, List.of(
                "Phường 1", "Phường 2", "Phường 3", "Phường 4",
                "Phường 6", "Phường 11",
                "Xã Tân Quy Tây", "Xã Tân Khánh Đông",
                "Xã Tân Quy Đông", "Xã Tân Phú Đông"
        )});

        list.add(new Object[]{"Gia Lai", Province.Type.PROVINCE, List.of(
                "Phường Diên Hồng", "Phường Đống Đa", "Phường Hoa Lư",
                "Phường Hội Phú", "Phường Hội Thương", "Phường Ia Kring",
                "Phường Phù Đổng", "Phường Tây Sơn", "Phường Thắng Lợi",
                "Phường Thống Nhất", "Phường Trà Bá", "Phường Yên Đổ",
                "Xã An Phú", "Xã Biển Hồ", "Xã Chư Á",
                "Xã Diên Phú", "Xã Gào"
        )});

        list.add(new Object[]{"Hà Tĩnh", Province.Type.PROVINCE, List.of(
                "Phường Bắc Hà", "Phường Nam Hà", "Phường Nguyễn Du",
                "Phường Tân Giang", "Phường Thạch Linh", "Phường Thạch Quý",
                "Phường Trần Phú",
                "Xã Thạch Hạ", "Xã Đồng Môn", "Xã Thạch Hưng",
                "Xã Thạch Môn", "Xã Thạch Châu"
        )});

        list.add(new Object[]{"Hưng Yên", Province.Type.PROVINCE, List.of(
                "Phường An Tảo", "Phường Hiến Nam", "Phường Hồng Châu",
                "Phường Lam Sơn", "Phường Lê Lợi", "Phường Minh Khai",
                "Phường Quang Trung",
                "Xã Bảo Khê", "Xã Hùng Cường", "Xã Liên Phương",
                "Xã Phú Cường", "Xã Trung Nghĩa"
        )});

        list.add(new Object[]{"Khánh Hòa", Province.Type.PROVINCE, List.of(
                "Phường Lộc Thọ", "Phường Phương Sài", "Phường Phương Sơn",
                "Phường Phước Hải", "Phường Phước Hòa", "Phường Phước Long",
                "Phường Phước Tân", "Phường Phước Tiến", "Phường Tân Lập",
                "Phường Vạn Thắng", "Phường Vạn Thạnh", "Phường Vĩnh Hải",
                "Phường Vĩnh Hòa", "Phường Vĩnh Nguyên", "Phường Vĩnh Phước",
                "Phường Vĩnh Thọ", "Phường Vĩnh Trường",
                "Xã Vĩnh Hiệp", "Xã Vĩnh Lương", "Xã Vĩnh Ngọc",
                "Xã Vĩnh Phương", "Xã Phước Đồng"
        )});

        list.add(new Object[]{"Lai Châu", Province.Type.PROVINCE, List.of(
                "Phường Đoàn Kết", "Phường Quyết Thắng", "Phường Tân Phong",
                "Phường Quyết Tiến",
                "Xã San Thàng", "Xã Nậm Loỏng", "Xã Sùng Phài",
                "Xã Thèn Sin", "Xã Giang Ma"
        )});

        list.add(new Object[]{"Lạng Sơn", Province.Type.PROVINCE, List.of(
                "Phường Chi Lăng", "Phường Đông Kinh", "Phường Hoàng Văn Thụ",
                "Phường Tam Thanh", "Phường Vĩnh Trại",
                "Xã Hoàng Đồng", "Xã Mai Pha", "Xã Quảng Lạc",
                "Xã Yên Trạch"
        )});

        list.add(new Object[]{"Lào Cai", Province.Type.PROVINCE, List.of(
                "Phường Bắc Lệnh", "Phường Bình Minh", "Phường Cốc Lếu",
                "Phường Duyên Hải", "Phường Kim Tân", "Phường Lào Cai",
                "Phường Nam Cường", "Phường Phố Mới", "Phường Pom Hán",
                "Phường Xuân Tăng",
                "Xã Đồng Tuyển", "Xã Cam Đường", "Xã Tả Phời",
                "Xã Hợp Thành", "Xã Vạn Hòa"
        )});

        list.add(new Object[]{"Lâm Đồng", Province.Type.PROVINCE, List.of(
                "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
                "Phường 6", "Phường 7", "Phường 8", "Phường 9", "Phường 10",
                "Phường 11", "Phường 12",
                "Xã Tà Nung", "Xã Trạm Hành", "Xã Xuân Thọ",
                "Xã Xuân Trường"
        )});

        list.add(new Object[]{"Nghệ An", Province.Type.PROVINCE, List.of(
                "Phường Bến Thủy", "Phường Cửa Nam", "Phường Đội Cung",
                "Phường Đông Vĩnh", "Phường Hà Huy Tập", "Phường Hưng Bình",
                "Phường Hưng Dũng", "Phường Hưng Phúc", "Phường Lê Lợi",
                "Phường Lê Mao", "Phường Quán Bàu", "Phường Quang Tiến",
                "Phường Trung Đô", "Phường Trường Thi", "Phường Vinh Tân",
                "Xã Nghi Kim", "Xã Nghi Liên", "Xã Nghi Phú",
                "Xã Nghi Ân", "Xã Nghi Đức"
        )});

        list.add(new Object[]{"Ninh Bình", Province.Type.PROVINCE, List.of(
                "Phường Bích Đào", "Phường Đông Thành", "Phường Nam Bình",
                "Phường Nam Thành", "Phường Ninh Khánh", "Phường Ninh Phong",
                "Phường Ninh Sơn", "Phường Phúc Thành", "Phường Tân Thành",
                "Phường Thanh Bình",
                "Xã Ninh Nhất", "Xã Ninh Tiến", "Xã Ninh Phúc"
        )});

        list.add(new Object[]{"Phú Thọ", Province.Type.PROVINCE, List.of(
                "Phường Gia Cẩm", "Phường Dữu Lâu", "Phường Nông Trang",
                "Phường Tân Dân", "Phường Thanh Miếu", "Phường Tiên Cát",
                "Phường Vân Cơ", "Phường Vân Phú",
                "Xã Chu Hóa", "Xã Phượng Lâu", "Xã Sông Lô",
                "Xã Thanh Đình", "Xã Thụy Vân"
        )});

        list.add(new Object[]{"Quảng Ngãi", Province.Type.PROVINCE, List.of(
                "Phường Chánh Lộ", "Phường Lê Hồng Phong", "Phường Nghĩa Chánh",
                "Phường Nghĩa Lộ", "Phường Nghĩa Thương", "Phường Nguyễn Nghiêm",
                "Phường Quảng Phú", "Phường Trần Hưng Đạo", "Phường Trần Phú",
                "Xã Nghĩa An", "Xã Nghĩa Dũng", "Xã Nghĩa Hà",
                "Xã Nghĩa Phú", "Xã Tịnh Ấn Đông"
        )});

        list.add(new Object[]{"Quảng Ninh", Province.Type.PROVINCE, List.of(
                "Phường Bạch Đằng", "Phường Cao Xanh", "Phường Giếng Đáy",
                "Phường Hà Khẩu", "Phường Hà Khánh", "Phường Hà Lầm",
                "Phường Hà Phong", "Phường Hà Trung", "Phường Hà Tu",
                "Phường Hồng Gai", "Phường Hồng Hà", "Phường Hồng Hải",
                "Phường Tuần Châu", "Phường Trần Hưng Đạo", "Phường Việt Hưng",
                "Xã Đại Yên", "Xã Hoàng Tân", "Xã Tân Bình"
        )});

        list.add(new Object[]{"Quảng Trị", Province.Type.PROVINCE, List.of(
                "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5",
                "Xã Hải Lệ", "Xã Triệu Ái", "Xã Triệu Trung",
                "Xã Triệu Phước"
        )});

        list.add(new Object[]{"Sơn La", Province.Type.PROVINCE, List.of(
                "Phường Chiềng An", "Phường Chiềng Cơi", "Phường Chiềng Lề",
                "Phường Chiềng Sinh", "Phường Quyết Tâm", "Phường Quyết Thắng",
                "Phường Tô Hiệu",
                "Xã Chiềng Đen", "Xã Chiềng Ngần", "Xã Chiềng Xôm",
                "Xã Hua La", "Xã Mường Chanh"
        )});

        list.add(new Object[]{"Tây Ninh", Province.Type.PROVINCE, List.of(
                "Phường 1", "Phường 2", "Phường 3", "Phường 4",
                "Phường Hiệp Ninh", "Phường Ninh Sơn", "Phường Ninh Thạnh",
                "Xã Bình Minh", "Xã Tân Bình", "Xã Thạnh Tân"
        )});

        list.add(new Object[]{"Thái Nguyên", Province.Type.PROVINCE, List.of(
                "Phường Chùa Hang", "Phường Đồng Bẩm", "Phường Đồng Quang",
                "Phường Gia Sàng", "Phường Hoàng Văn Thụ", "Phường Hương Sơn",
                "Phường Phan Đình Phùng", "Phường Phú Xá", "Phường Quán Triều",
                "Phường Tân Long", "Phường Tân Thịnh", "Phường Thịnh Đán",
                "Phường Trung Thành", "Phường Túc Duyên",
                "Xã Cao Ngạn", "Xã Linh Sơn", "Xã Phúc Hà",
                "Xã Phúc Trìu", "Xã Quyết Thắng", "Xã Tân Cương"
        )});

        list.add(new Object[]{"Thanh Hóa", Province.Type.PROVINCE, List.of(
                "Phường Ba Đình", "Phường Điện Biên", "Phường Đông Cương",
                "Phường Đông Hương", "Phường Đông Sơn", "Phường Đông Thọ",
                "Phường Hàm Rồng", "Phường Lam Sơn", "Phường Nam Ngạn",
                "Phường Ngọc Trạo", "Phường Phú Sơn", "Phường Quảng Hưng",
                "Phường Quảng Phú", "Phường Quảng Thắng", "Phường Quảng Thành",
                "Phường Tào Xuyên", "Phường Thiệu Dương",
                "Xã Đông Hải", "Xã Đông Lĩnh", "Xã Đông Tân",
                "Xã Quảng Đông", "Xã Thiệu Vân"
        )});

        list.add(new Object[]{"Tuyên Quang", Province.Type.PROVINCE, List.of(
                "Phường An Tường", "Phường Minh Xuân", "Phường Nông Tiến",
                "Phường Phan Thiết", "Phường Tân Hà", "Phường Tân Quang",
                "Phường Ỷ La",
                "Xã An Khang", "Xã Đội Cấn", "Xã Kim Phú",
                "Xã Lưỡng Vượng", "Xã Thái Long"
        )});

        list.add(new Object[]{"Vĩnh Long", Province.Type.PROVINCE, List.of(
                "Phường 1", "Phường 2", "Phường 3", "Phường 4",
                "Phường 5", "Phường 8", "Phường 9",
                "Xã Mỹ Hòa", "Xã Tân Hội", "Xã Tân Ngãi",
                "Xã Tân Hòa", "Xã Trung Hiếu"
        )});

        return list;
    }
}
