package hu.thesis.msc.noidentity.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.BaseGraphServiceClient;
import com.microsoft.graph.directoryobjects.getbyids.GetByIdsPostRequestBody;
import com.microsoft.graph.directoryobjects.getbyids.GetByIdsPostResponse;
import com.microsoft.graph.directoryobjects.item.getmemberobjects.GetMemberObjectsPostRequestBody;
import com.microsoft.graph.directoryobjects.item.getmemberobjects.GetMemberObjectsPostResponse;
import com.microsoft.graph.models.AssignedLicense;
import com.microsoft.graph.models.DirectoryRoleCollectionResponse;
import com.microsoft.graph.models.Entity;
import com.microsoft.graph.models.LicenseDetailsCollectionResponse;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.ReferenceCreate;
import com.microsoft.graph.models.SubscribedSkuCollectionResponse;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.assignlicense.AssignLicensePostRequestBody;
import hu.thesis.msc.noidentity.dto.AzureRoleObjectDto;
import hu.thesis.msc.noidentity.dto.ResponseFromResourceDto;
import hu.thesis.msc.noidentity.entity.AzureResourceConfig;
import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.ResourceAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AzureResourceService {

    public GraphServiceClient getGraphServiceClient(AzureResourceConfig azureResourceConfig) {
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(azureResourceConfig.getApplicationId())
                .tenantId(azureResourceConfig.getTenantId())
                .clientSecret(azureResourceConfig.getSecret())
                .build();
        return new GraphServiceClient(credential, azureResourceConfig.getScope());
    }

    public List<AzureRoleObjectDto> getDirectoryRoles(Resource resource) {
        GraphServiceClient graphClient = getGraphServiceClient(resource.getAzureConfig());

        DirectoryRoleCollectionResponse result = graphClient.directoryRoles().get();

        if (result == null || result.getValue() == null) {
            return Collections.emptyList();
        }
        return  result.getValue().stream()
                .map(role ->  {
                    AzureRoleObjectDto directoryRole = new AzureRoleObjectDto();
                    directoryRole.setUid(String.valueOf(role.getId()));
                    directoryRole.setName(role.getDisplayName());
                    return directoryRole;
                }).collect(Collectors.toList());
    }

    public List<AzureRoleObjectDto> getLicences(Resource resource) {
        GraphServiceClient graphClient = getGraphServiceClient(resource.getAzureConfig());

        SubscribedSkuCollectionResponse result = graphClient.subscribedSkus().get();

        if (result == null || result.getValue() == null) {
            return Collections.emptyList();
        }
        return  result.getValue().stream()
                .map(subscribedSku ->  {
                    AzureRoleObjectDto licence = new AzureRoleObjectDto();
                    licence.setUid(String.valueOf(subscribedSku.getSkuId()));
                    licence.setName(subscribedSku.getSkuPartNumber());
                    return licence;
                }).collect(Collectors.toList());
    }

    public ResponseFromResourceDto create(ResourceAccount resourceAccount) {
        ResponseFromResourceDto dto = new ResponseFromResourceDto();
        GraphServiceClient graphClient = getGraphServiceClient(resourceAccount.getResource().getAzureConfig());
        User azureUser = buildAzureUser(resourceAccount, true);
        User createdUser = graphClient.users().post(azureUser);
        if (createdUser != null) {
            modifyLicenceAssignmentForUserToCreate(getCollectionAttributeFromResourceAccount(resourceAccount, "licences"), createdUser.getId(), false, graphClient);
            addUserToDirectoryRoles(getCollectionAttributeFromResourceAccount(resourceAccount, "roles"), createdUser.getId(), graphClient);
            buildResponse(dto, createdUser, graphClient);
        }
        return dto;
    }


    public void buildResponse(ResponseFromResourceDto dto, User azureUser, GraphServiceClient graphServiceClient) {
        dto.setUid(azureUser.getId());
        Map<String, Object> attributesOnResource = new HashMap<>();
        attributesOnResource.put("displayName", azureUser.getDisplayName());
        attributesOnResource.put("email", azureUser.getUserPrincipalName());
        attributesOnResource.put("accountId", azureUser.getMailNickname());
        attributesOnResource.put("company", azureUser.getCompanyName());
        attributesOnResource.put("department", azureUser.getDepartment());
        if (azureUser.getId() != null) {
            attributesOnResource.put("licences", getLicencesOfUser(azureUser.getId(), graphServiceClient));
            attributesOnResource.put("roles", getDirectoryRolesForUser(azureUser.getId(), graphServiceClient));
        }
        dto.setAttributesOnResource(attributesOnResource);
    }

    private List<String> getCollectionAttributeFromResourceAccount(ResourceAccount resourceAccount, String attributeName) {
        return (List<String>) resourceAccount.getExpectedAttributes().get(attributeName);
    }


    public ResponseFromResourceDto update(ResourceAccount resourceAccount) {
        ResponseFromResourceDto dto = new ResponseFromResourceDto();
        GraphServiceClient graphClient = getGraphServiceClient(resourceAccount.getResource().getAzureConfig());
        User azureUser = buildAzureUser(resourceAccount, false);
        azureUser.setId(resourceAccount.getIdentifier());
        handleLicenceAssignments(resourceAccount, graphClient);
        handleDirectoryRoleAssignments(resourceAccount, graphClient);
        try {
            graphClient.users().byUserId(resourceAccount.getIdentifier()).patch(azureUser);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating Azure user with id " + resourceAccount.getIdentifier(), e);
        }
        buildResponse(dto, azureUser, graphClient);
        return dto;
    }

    private User buildAzureUser(ResourceAccount resourceAccount, boolean isNew) {
        User azureUser = new User();
        Map<String, Object> expectedAttributes = resourceAccount.getExpectedAttributes();
        azureUser.setDisplayName(String.valueOf(expectedAttributes.get("displayName")));
        azureUser.setUserPrincipalName(String.valueOf(expectedAttributes.get("accountId")) + "@devlft.onmicrosoft.com");
        azureUser.setMailNickname(String.valueOf(expectedAttributes.get("accountId")));
        azureUser.setAccountEnabled(true);
        azureUser.setCompanyName(String.valueOf(expectedAttributes.get("company")));
        azureUser.setDepartment(String.valueOf(expectedAttributes.get("department")));
        if (isNew) {
            //must be set for each user
            azureUser.setUsageLocation("HU");
            PasswordProfile passwordProfile = new PasswordProfile();
            passwordProfile.setForceChangePasswordNextSignIn(false);
            passwordProfile.setPassword("Password123");
            azureUser.setPasswordProfile(passwordProfile);
        }
        return azureUser;
    }


    private void handleLicenceAssignments(ResourceAccount resourceAccount, GraphServiceClient graphClient) {
        List<String> licencesFromIdm = (List<String>) resourceAccount.getExpectedAttributes().get("licences");
        List<String> licencesFromAzure = getLicencesOfUser(resourceAccount.getIdentifier(), graphClient);
        List<String> licencesToAdd = getDiffFromLists(licencesFromIdm, licencesFromAzure);
        List<String> licencesToRemove = getDiffFromLists(licencesFromAzure, licencesFromIdm);
        modifyLicenceAssignmentForUserToCreate(licencesToAdd, resourceAccount.getIdentifier(), false, graphClient);
        modifyLicenceAssignmentForUserToCreate(licencesToRemove, resourceAccount.getIdentifier(), true, graphClient);
    }

    private void handleDirectoryRoleAssignments(ResourceAccount resourceAccount, GraphServiceClient graphClient) {
        List<String> rolesFromIdm = (List<String>) resourceAccount.getExpectedAttributes().get("roles");
        List<String> rolesFromAzure = getDirectoryRolesForUser(resourceAccount.getIdentifier(), graphClient);
        List<String> rolesToAdd = getDiffFromLists(rolesFromIdm, rolesFromAzure);
        List<String> rolesToRemove = getDiffFromLists(rolesFromAzure, rolesFromIdm);
        addUserToDirectoryRoles(rolesToAdd, resourceAccount.getIdentifier(), graphClient);
        removeUserFromDirectoryRoles(rolesToRemove, resourceAccount.getIdentifier(), graphClient);
    }


    private List<String> getDiffFromLists(List<String> listA, List<String> listB) {
        List<String> copyList = new ArrayList<>(listA);
        copyList.removeAll(listB);
        return copyList;
    }


    public void modifyLicenceAssignmentForUserToCreate(Collection<String> licenceIds, String userId, boolean remove, GraphServiceClient graphClient) {
        if (!licenceIds.isEmpty()) {
            licenceIds.forEach(licence -> modifyLicenceAssignment(licence, userId, remove, graphClient));
        }
    }

    public void modifyLicenceAssignment(String licenceId, String userId, boolean remove, BaseGraphServiceClient graphClient) {
        LinkedList<AssignedLicense> addLicensesList = new LinkedList<>();
        LinkedList<UUID> removeLicensesList = new LinkedList<>();
        AssignLicensePostRequestBody requestBody = new AssignLicensePostRequestBody();
        if (remove) {
            removeLicensesList.add(UUID.fromString(licenceId));
        } else {
            AssignedLicense addLicenses = new AssignedLicense();
            LinkedList<UUID> disabledPlansList = new LinkedList<>();
            addLicenses.setDisabledPlans(disabledPlansList);
            addLicenses.setSkuId(UUID.fromString(licenceId));
            addLicensesList.add(addLicenses);
        }
        requestBody.setAddLicenses(addLicensesList);
        requestBody.setRemoveLicenses(removeLicensesList);

        graphClient.users().byUserId(userId).assignLicense().post(requestBody);
    }


    private void addUserToDirectoryRoles(List<String> directoryRoleIds, String userId, GraphServiceClient graphServiceClient) {
        if (!directoryRoleIds.isEmpty()) {
            directoryRoleIds.forEach(roleId -> addUserToDirectoryRole(userId, roleId, graphServiceClient));
        }
    }

    private void removeUserFromDirectoryRoles(List<String> directoryRoleIds, String userId, GraphServiceClient graphServiceClient) {
        if (!directoryRoleIds.isEmpty()) {
            directoryRoleIds.forEach(roleId -> removeUserFromDirectoryRole(userId, roleId, graphServiceClient));
        }
    }

    private void addUserToDirectoryRole(String userId, String roleId, GraphServiceClient graphClient) {
        ReferenceCreate referenceCreate = new ReferenceCreate();
        referenceCreate.setOdataId("https://graph.microsoft.com/v1.0/directoryObjects/" + userId);
        graphClient.directoryRoles().byDirectoryRoleId(roleId).members().ref().post(referenceCreate);
    }

    private void removeUserFromDirectoryRole(String userId, String roleId, GraphServiceClient graphClient) {
        graphClient.directoryRoles().byDirectoryRoleId(roleId).members().byDirectoryObjectId(userId).ref().delete();
    }

    private List<String> getLicencesOfUser(String uid, GraphServiceClient graphClient) {
        LicenseDetailsCollectionResponse licenseDetails = graphClient.users().byUserId(uid).licenseDetails().get();

        if (licenseDetails == null || licenseDetails.getValue() == null) {
            return Collections.emptyList();
        }
        return licenseDetails.getValue().stream().
                map(details -> String.valueOf(details.getSkuId()))
                .collect(Collectors.toList());
    }

    private List<String> getDirectoryRolesForUser(String uid, GraphServiceClient graphClient) {
        GetMemberObjectsPostRequestBody getMemberObjectsPostRequestBody = new GetMemberObjectsPostRequestBody();
        getMemberObjectsPostRequestBody.setSecurityEnabledOnly(true);
        GetMemberObjectsPostResponse memberObjects = graphClient.directoryObjects().byDirectoryObjectId(uid).getMemberObjects().post(getMemberObjectsPostRequestBody);
        if (memberObjects == null || memberObjects.getValue() == null) {
            return Collections.emptyList();
        }
        return getDirectoryRolesFromAssignedObjects(memberObjects.getValue(), graphClient);
    }

    private List<String> getDirectoryRolesFromAssignedObjects(List<String> objectIds, GraphServiceClient graphClient) {
        if (objectIds.isEmpty()) {
            return Collections.emptyList();
        }
        GetByIdsPostRequestBody getByIdsPostRequestBody = new GetByIdsPostRequestBody();
        LinkedList<String> ids = new LinkedList<>(objectIds);
        getByIdsPostRequestBody.setIds(ids);
        LinkedList<String> types = new LinkedList<>();
        types.add("directoryRole");
        getByIdsPostRequestBody.setTypes(types);
        GetByIdsPostResponse result = graphClient.directoryObjects().getByIds().post(getByIdsPostRequestBody);
        if (result == null || result.getValue() == null) {
            return Collections.emptyList();
        }
        return result.getValue().stream().map(Entity::getId).collect(Collectors.toList());

    }


}
