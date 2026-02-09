package vn.duy.jobIT.domain.res.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.duy.jobIT.util.constant.GenderEnum;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatedUserResponse {
    private Long id;

    private String name;

    private int age;

    private String phoneNumber;

    private String email;

    private String address;

    private GenderEnum gender;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdDate;

    private CompanyUser company;

    private RoleUser role;
}
