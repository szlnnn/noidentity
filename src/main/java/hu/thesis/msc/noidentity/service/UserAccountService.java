package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.CredentialsDto;
import hu.thesis.msc.noidentity.dto.SignUpDto;
import hu.thesis.msc.noidentity.dto.UserAccountDto;
import hu.thesis.msc.noidentity.dto.UserDto;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.enums.Role;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.mappers.UserAccountMapper;
import hu.thesis.msc.noidentity.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserAccountMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        UserAccount user = userRepository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<UserAccount> optionalUser = userRepository.findByLogin(userDto.getAccountDto().getLogin());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        UserAccount user = userMapper.userAccountDtoToUserAccount(userDto.getAccountDto());
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
        setRole(userDto.getRole(), user);
        UserAccount savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    private static void setRole(String roleFromClient, UserAccount user) {
        if ((Role.USER.getAuthority().equals(roleFromClient.toUpperCase()))) {
            user.setRole(Role.USER);
        } else {
            user.setRole(Role.ADMIN);
        }
    }

    public UserDto findByLogin(String login) {
        UserAccount user = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public List<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }


    public UserAccount updateUserAccount(UserAccount userDataFromClient) {
        Optional<UserAccount> optionalUser = userRepository.findByLogin(userDataFromClient.getLogin());
        if (optionalUser.isEmpty()) {
            throw new AppException("Cannot find user with login: " + userDataFromClient.getLogin(), HttpStatus.BAD_REQUEST);
        }
        UserAccount currentAccount = optionalUser.get();
        currentAccount.setEmail(userDataFromClient.getEmail());
        currentAccount.setFirstName(userDataFromClient.getFirstName());
        currentAccount.setLastName(userDataFromClient.getLastName());
        currentAccount.setRole(userDataFromClient.getRole());
        currentAccount.setStartDate(userDataFromClient.getStartDate());
        currentAccount.setEndDate(userDataFromClient.getEndDate());
        userRepository.save(currentAccount);
        return currentAccount;

    }
}