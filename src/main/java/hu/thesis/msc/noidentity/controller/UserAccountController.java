package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserAccountController {

//    private final UserAccountService userService;
//
//    @Autowired
//    public UserAccountController(UserAccountService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public List<UserAccount> getUsers() {
//       return userService.getUserAccounts();
//    }
//
//    @PostMapping
//    public void registerNewUserAccount(@RequestBody UserAccount userAccount) {
//        userService.addNewUserAccount(userAccount);
//    }
//
//    @DeleteMapping(path = "{userAccountId}")
//    public void deleteUserAccount(@PathVariable("userAccountId") Long id) {
//        userService.deleteUserAccount(id);
//    }
//
//    @PutMapping()
//    public UserAccount updateUserAccount(@RequestBody UserAccount userAccount) {
//       return userService.updateUserAccount(userAccount);
//    }
}
