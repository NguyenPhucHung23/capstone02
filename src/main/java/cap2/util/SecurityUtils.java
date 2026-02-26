package cap2.util;

import cap2.exception.AppException;
import cap2.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class cho việc kiểm tra quyền truy cập
 * Dùng chung cho tất cả các Service
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Private constructor - không cho tạo instance
    }

    /**
     * Lấy userId hiện tại từ token
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return authentication.getName();
    }

    /**
     * Kiểm tra user hiện tại có phải ADMIN không
     */
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    /**
     * Kiểm tra quyền: chỉ cho phép xem/sửa/xóa của chính mình hoặc ADMIN
     * Throw exception nếu không có quyền
     */
    public static void checkPermission(String targetUserId) {
        String currentUserId = getCurrentUserId();
        if (!currentUserId.equals(targetUserId) && !isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * Kiểm tra quyền ADMIN
     * Throw exception nếu không phải ADMIN
     */
    public static void checkAdminRole() {
        if (!isAdmin()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * Kiểm tra xem user hiện tại có phải là chủ sở hữu không
     */
    public static boolean isOwner(String targetUserId) {
        return getCurrentUserId().equals(targetUserId);
    }
}
