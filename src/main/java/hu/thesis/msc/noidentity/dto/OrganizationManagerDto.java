package hu.thesis.msc.noidentity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationManagerDto {
    private Long id;
    private String name;
    private String company;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserAccount manager;

}
