package hu.thesis.msc.noidentity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AzureRoleObjectDto {

    private String name;
    private String uid;

}
