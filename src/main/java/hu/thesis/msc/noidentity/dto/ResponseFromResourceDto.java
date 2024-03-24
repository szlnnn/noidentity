package hu.thesis.msc.noidentity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseFromResourceDto {

    private String uid;

    private Map<String, Object> attributesOnResource;

}
