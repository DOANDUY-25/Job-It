package vn.duy.jobIT.domain.dto;

import lombok.Getter;
import lombok.Setter;
import vn.duy.jobIT.domain.Role;
import vn.duy.jobIT.util.constant.GenderEnum;
import vn.duy.jobIT.domain.Company;


@Getter
@Setter
public class UpdateUserDTO {
    private Long id;

    private String name;

    private int age;

    private String address;

    private GenderEnum gender;

    private Company company;

    private Role role;
}
