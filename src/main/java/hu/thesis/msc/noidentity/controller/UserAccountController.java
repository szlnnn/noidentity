package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.dto.UserAccountDto;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserAccountController {

    private final UserAccountService userService;

    @Autowired
    public UserAccountController(UserAccountService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserAccountDto> getUsers() {
       return userService.getAllUsers();
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserAccount updateUserAccount(@RequestBody UserAccountDto userAccount) {
       return userService.updateUserAccount(userAccount);
    }
}
