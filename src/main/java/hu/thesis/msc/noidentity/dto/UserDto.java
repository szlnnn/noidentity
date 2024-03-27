package hu.thesis.msc.noidentity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.Organization;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String login;
    private String email;
    private String token;
    private Role role;
    private Date startDate;
    private Date endDate;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Organization organization;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserAccount manager;
}
