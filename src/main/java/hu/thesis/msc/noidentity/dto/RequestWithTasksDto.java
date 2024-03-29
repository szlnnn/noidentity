package hu.thesis.msc.noidentity.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hu.thesis.msc.noidentity.entity.Request;
import hu.thesis.msc.noidentity.entity.RequestTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestWithTasksDto {

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Request request;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RequestTask managerTask;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RequestTask applicationOwnerTask;

}
