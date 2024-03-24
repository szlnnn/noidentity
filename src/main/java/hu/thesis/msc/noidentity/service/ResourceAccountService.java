package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.entity.Organization;
import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.ResourceAccount;
import hu.thesis.msc.noidentity.entity.ResourceAccountOperationRequest;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserRoleAssignment;
import hu.thesis.msc.noidentity.repository.ResourceAccountRepository;
import hu.thesis.msc.noidentity.repository.UserRoleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ResourceAccountService {

    private final OrganizationService organizationService;

    private final UserRoleAssignmentRepository userRoleAssignmentRepository;

    private final ResourceAccountRepository resourceAccountRepository;

    public void updateResourceAccount(ResourceAccount resourceAccount) {
        if (resourceAccount.getIdentifier() != null) {
            resourceAccount.setOperation("U");
        }
        resourceAccount.setLastUpdateTime(new Date());
        buildAttributes(resourceAccount, resourceAccount.getUser(), resourceAccount.getResource());
        resourceAccountRepository.save(resourceAccount);
    }

    public void createResourceAccount(ResourceAccountOperationRequest operationRequest) {
        ResourceAccount resourceAccount = new ResourceAccount();
        resourceAccount.setResource(operationRequest.getResource());
        resourceAccount.setUser(operationRequest.getUser());
        resourceAccount.setLastUpdateTime(new Date());
        resourceAccount.setOperation("C");
        buildAttributes(resourceAccount, operationRequest.getUser(), operationRequest.getResource());
        resourceAccountRepository.save(resourceAccount);
    }


    private void buildAttributes(ResourceAccount resourceAccount, UserAccount userAccount, Resource resource) {
        Map<String, Object> initialExpectedAttributes = buildDefaultAttributesFromUser(userAccount);
        addRoleAttributesToExpectedAttributes(initialExpectedAttributes, userAccount, resource);
        resourceAccount.setExpectedAttributes(initialExpectedAttributes);
    }


    private Map<String, Object> buildDefaultAttributesFromUser(UserAccount userAccount) {
        Map<String, Object> expectedAttributesWithDefaultAttributes = new HashMap<>();
        expectedAttributesWithDefaultAttributes.put("accountId", userAccount.getLogin());
        expectedAttributesWithDefaultAttributes.put("email", userAccount.getEmail());
        expectedAttributesWithDefaultAttributes.put("displayName", getFullNameForUser(userAccount));
        Organization organization = organizationService.getOrganizationOfUser(userAccount);
        if (organization != null) {
            expectedAttributesWithDefaultAttributes.put("department", organization.getName());
            expectedAttributesWithDefaultAttributes.put("company", organization.getCompany());
        }

        return expectedAttributesWithDefaultAttributes;

    }

    private void addRoleAttributesToExpectedAttributes(Map<String, Object> expectedAttribute, UserAccount user, Resource resource) {
        List<String> licenceUIDs = new ArrayList<>();
        List<String> appRoleIds = new ArrayList<>();

        userRoleAssignmentRepository
                .findByUserAndAssignmentStatusIn(user, getUserRoleStatuses())
                .stream()
                .filter(ura -> ura.getRole().getResource().getId().equals(resource.getId()))
                .forEach(ura -> {
                    if ("Licence".equals(ura.getRole().getType())) {
                        licenceUIDs.add(ura.getRole().getResourceRoleId());
                    } else {
                        appRoleIds.add(ura.getRole().getResourceRoleId());
                    }
                });

        expectedAttribute.put("licences", licenceUIDs);
        expectedAttribute.put("roles", appRoleIds);
    }


    private List<String> getUserRoleStatuses() {
        return List.of("A", "PA");
    }

    private String getFullNameForUser(UserAccount userAccount) {
        return userAccount.getFirstName() + " " + userAccount.getLastName();
    }

}
