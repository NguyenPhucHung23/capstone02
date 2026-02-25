package cap2.schema;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "profiles")
public class Profile {

    @Id
    private String id;

    private String userId;

    private String fullName;
    private String phone;
    private String address;
    private String gender;
}