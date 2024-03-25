package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.dto.UserRoleAssignmentMinimalDataDto;
import hu.thesis.msc.noidentity.entity.Role;
import hu.thesis.msc.noidentity.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    public List<Role> getRoles(@PathVariable Long id) {
       return roleService.getRolesUnderResource(id);
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public Role createRole(@RequestBody Role role) {
       return roleService.createRole(role);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Role updateRole(@RequestBody Role role) {
            return roleService.updateRole(role);
    }

    @GetMapping("/user/{id}")
    public List<UserRoleAssignmentMinimalDataDto> getRolesOfUser(@PathVariable Long id) {
        return roleService.getAssignedRolesDtosForUser(id);
    }
}
