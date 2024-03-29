package hu.thesis.msc.noidentity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccountDto {


    private Long id;
    private String firstName;
    private String lastName;
    private String login;
    private Date startDate;
    private Date endDate;
    private String email;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Organization organization;
    private String role;

}