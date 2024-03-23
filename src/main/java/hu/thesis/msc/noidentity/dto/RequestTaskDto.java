package hu.thesis.msc.noidentity.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.Role;
import hu.thesis.msc.noidentity.entity.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestTaskDto {


    private Long id;

    private String status;

    /**
     * -manager
     * -applicationOwner
     */
    private String type;

    private Date creationTime;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserAccount targetUser;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Role role;

    private String operation;


}
