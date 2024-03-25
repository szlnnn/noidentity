package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.UserRoleAssignmentMinimalDataDto;
import hu.thesis.msc.noidentity.entity.Resource;
import hu.thesis.msc.noidentity.entity.Role;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserRoleAssignment;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.repository.ResourceRepository;
import hu.thesis.msc.noidentity.repository.RoleRepository;
import hu.thesis.msc.noidentity.repository.UserRoleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final ResourceRepository resourceRepository;

    private final UserAccountService userAccountService;

    private final UserRoleAssignmentRepository userRoleAssignmentRepository;

    public List<Role> getRolesUnderResource(Long resourceId) {
        return resourceRepository.findById(resourceId).map(roleRepository::findAllByResource)
                .orElseThrow(() -> new AppException("Cannot find provided resource: " + resourceId, HttpStatus.BAD_REQUEST));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(Role roleFromClient) {
        Optional<Resource> resourceOptional = resourceRepository.findById(roleFromClient.getResource().getId());

        if (resourceOptional.isEmpty()) {
            throw new AppException("Cannot find provided resource: " + roleFromClient.getResource().getName(), HttpStatus.BAD_REQUEST);
        }
        return roleRepository.save(roleFromClient);
    }

    public Role updateRole(Role roleFromClient) {
        return roleRepository.findById(roleFromClient.getId())
                .map(role -> {
                    role.setDescription(roleFromClient.getDescription());
                    role.setActive(roleFromClient.isActive());
                    role.setResourceRoleId(roleFromClient.getResourceRoleId());
                    role.setType(roleFromClient.getType());
                    role.setName(roleFromClient.getName());
                    return roleRepository.save(role);
                })
                .orElseThrow(() -> new AppException("Cannot find provided role: " + roleFromClient.getName(), HttpStatus.BAD_REQUEST));
    }

    public List<UserRoleAssignmentMinimalDataDto> getAssignedRolesDtosForUser(Long id) {
        UserAccount user = userAccountService.getUserByIdOrElseThrow(id);
        return userRoleAssignmentRepository.findByUser(user).stream().map(ura -> {
            UserRoleAssignmentMinimalDataDto dto = new UserRoleAssignmentMinimalDataDto();
            dto.setId(ura.getId());
            dto.setUserId(user.getId());
            dto.setRole(ura.getRole());
            dto.setAssignmentStatus(ura.getAssignmentStatus());
            dto.setCreatedTime(ura.getCreationTime());
            dto.setAssignedTime(ura.getAssignedTime());
            dto.setRevokedTime(ura.getRevokedTime());
            return dto;
        }).collect(Collectors.toList());
    }

}
