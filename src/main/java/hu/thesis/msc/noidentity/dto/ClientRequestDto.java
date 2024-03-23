package hu.thesis.msc.noidentity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.Role;
import hu.thesis.msc.noidentity.entity.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRequestDto {

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserAccount requester;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserAccount targetUser;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ArrayList<Role> rolesToRequest;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ArrayList<Role> rolesToRevoke;


}
