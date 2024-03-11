package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.entity.NoIdMUser;
import hu.thesis.msc.noidentity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class NoIdMUserController {

    private final UserService userService;

    @Autowired
    public NoIdMUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<NoIdMUser> getUsers() {
       return userService.getUsers();
    }
}
