package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.ResponseFromResourceDto;
import hu.thesis.msc.noidentity.entity.ResourceAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
@RequiredArgsConstructor
@Service
public class SimulatedResourceService {


    public ResponseFromResourceDto create(ResourceAccount resourceAccount){
        return new ResponseFromResourceDto(UUID.randomUUID().toString(), resourceAccount.getExpectedAttributes());
    }

    public ResponseFromResourceDto update(ResourceAccount resourceAccount) {
        return new ResponseFromResourceDto(resourceAccount.getIdentifier(), resourceAccount.getExpectedAttributes());
    }

}
