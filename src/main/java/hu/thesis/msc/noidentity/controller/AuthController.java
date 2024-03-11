package hu.thesis.msc.noidentity.controller;

import hu.thesis.msc.noidentity.config.UserAuthenticationProvider;
import hu.thesis.msc.noidentity.dto.CredentialsDto;
import hu.thesis.msc.noidentity.dto.SignUpDto;
import hu.thesis.msc.noidentity.dto.UserDto;
import hu.thesis.msc.noidentity.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserAccountService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = userService.register(user);
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
     //   return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
        return ResponseEntity.ok(createdUser);
    }

}