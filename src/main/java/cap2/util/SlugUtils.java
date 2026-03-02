package cap2.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    private SlugUtils() {
        // Private constructor
    }

    /**
     * Tạo slug từ string
     * Ví dụ: "Ghế Sofa Đẹp" -> "ghe-sofa-dep"
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);

        // Remove Vietnamese diacritics
        normalized = normalized
                .replaceAll("[đĐ]", "d")
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");

        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Tạo unique slug bằng cách thêm suffix
     */
    public static String toUniqueSlug(String input, String suffix) {
        String baseSlug = toSlug(input);
        if (suffix == null || suffix.isBlank()) {
            return baseSlug;
        }
        return baseSlug + "-" + suffix;
    }
}
