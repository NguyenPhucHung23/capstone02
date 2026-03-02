package cap2.schema;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
public class User {

    @Id
    String id;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String password;

    @Builder.Default
    Role role = Role.USER;

    String passwordResetToken;
    LocalDateTime passwordResetExpires;
}
