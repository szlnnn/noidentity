package hu.thesis.msc.noidentity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleAssignmentMinimalDataDto {


    private Long id;
    private Long userId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Role role;
    private String assignmentStatus;
    private Date createdTime;
    private Date assignedTime;
    private Date revokedTime;

}
