package hu.thesis.msc.noidentity.controller;


import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.createResource(resource));
    }


    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Resource> getResources() {
        return resourceService.getAllResources();
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public Resource updateUserAccount(@RequestBody Resource resource) {
        return resourceService.updateResource(resource);
    }

}
