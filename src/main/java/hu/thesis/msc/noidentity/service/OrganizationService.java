package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.OrganizationManagerDto;
import hu.thesis.msc.noidentity.dto.UserOrganizationAssignmentMinimalDataDto;
import hu.thesis.msc.noidentity.entity.Organization;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserOrganizationAssignment;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.OrganizationRepository;
import hu.thesis.msc.noidentity.repository.UserAccountRepository;
import hu.thesis.msc.noidentity.repository.UserOrganizationAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final UserAccountRepository accountRepository;

    private final UserOrganizationAssignmentRepository assignmentRepository;

    public List<OrganizationManagerDto> getAllOrganizations() {
         return organizationRepository.findAll().stream()
                 .map(org -> {
                     OrganizationManagerDto dto = new OrganizationManagerDto();
                     dto.setId(org.getId());
                     assignmentRepository.findByOrganizationAndAssignmentType(org, "manager")
                             .ifPresent(assignment -> dto.setManager(assignment.getUser()));
                     dto.setName(org.getName());
                     dto.setCompany(org.getCompany());
                     return dto;
                 }).collect(Collectors.toList());
    }

    public Organization createOrganization(OrganizationManagerDto organizationFromClient) {
        Optional<Organization> organizationOptional = organizationRepository.findByName(organizationFromClient.getName());

        if (organizationOptional.isPresent()) {
            throw new AppException("An organization with this name already exists: " + organizationFromClient.getName(), HttpStatus.BAD_REQUEST);
        }


        Organization toSaveOrg = new Organization();
        toSaveOrg.setName(organizationFromClient.getName());
        toSaveOrg.setCompany(organizationFromClient.getCompany());

        Organization savedOrg = organizationRepository.save(toSaveOrg);

        if (organizationFromClient.getManager() != null) {
            createUserOrganizationAssignment(new UserOrganizationAssignmentMinimalDataDto(organizationFromClient.getManager().getId(), savedOrg.getId(), "manager"));
        }

        return savedOrg;

    }

    public Organization updateOrganization(OrganizationManagerDto organizationFromClient) {
        Organization savedOrg = organizationRepository.findById(organizationFromClient.getId())
                .map(org -> {
                    org.setCompany(organizationFromClient.getCompany());
                    org.setName(organizationFromClient.getName());
                    return organizationRepository.save(org);
                })
                .orElseThrow(() -> new AppException("Cannot find provided organization: " + organizationFromClient.getName(), HttpStatus.BAD_REQUEST));
        if (organizationFromClient.getManager() != null) {
            createUserOrganizationAssignment(new UserOrganizationAssignmentMinimalDataDto(organizationFromClient.getManager().getId(), savedOrg.getId(), "manager"));
        }

        return savedOrg;

    }

    public UserOrganizationAssignment createUserOrganizationAssignment(UserOrganizationAssignmentMinimalDataDto dto) {
        Optional<UserAccount> user = accountRepository.findById(dto.getUserId());
        Optional<Organization> organization = organizationRepository.findById(dto.getOrganizationId());

        if (user.isEmpty() || organization.isEmpty()) {
            throw new AppException("Cannot find user or organization userId: " + dto.getUserId() + " orgid: "  + dto.getOrganizationId(), HttpStatus.BAD_REQUEST);
        }

        if ("manager".equals(dto.getAssignmentType())) {
        assignmentRepository.findByOrganizationAndAssignmentType(organization.get(), dto.getAssignmentType())
                .ifPresent(assignmentRepository::delete);
        } else if ("member".equals(dto.getAssignmentType())) {
            assignmentRepository.findByUserAndAssignmentType(user.get(), dto.getAssignmentType())
                    .ifPresent(assignmentRepository::delete);
        }


        UserOrganizationAssignment newAssignment = new UserOrganizationAssignment();
        newAssignment.setOrganization(organization.get());
        newAssignment.setUser(user.get());
        newAssignment.setAssignmentType(dto.getAssignmentType());

        return assignmentRepository.save(newAssignment);
    }

}
