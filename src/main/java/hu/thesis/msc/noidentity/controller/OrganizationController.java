package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.dto.OrganizationManagerDto;
import hu.thesis.msc.noidentity.dto.UserOrganizationAssignmentMinimalDataDto;
import hu.thesis.msc.noidentity.entity.Organization;
import hu.thesis.msc.noidentity.entity.UserOrganizationAssignment;
import hu.thesis.msc.noidentity.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@RequestMapping(path = "api/v1/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping()
    public List<OrganizationManagerDto> getOrganizations() {
       return organizationService.getAllOrganizations();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Organization createOrganization(@RequestBody OrganizationManagerDto organizationManagerDto) {
       return organizationService.createOrganization(organizationManagerDto);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Organization updateOrganization(@RequestBody OrganizationManagerDto organizationManagerDto) {
            return organizationService.updateOrganization(organizationManagerDto);
    }

    @PostMapping(path="/assignment")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserOrganizationAssignment> createAssignment(@RequestBody UserOrganizationAssignmentMinimalDataDto dto) {
        return ResponseEntity.ok(organizationService.createUserOrganizationAssignment(dto));
    }


}
