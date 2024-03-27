package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.dto.CredentialsDto;
import hu.thesis.msc.noidentity.dto.SignUpDto;
import hu.thesis.msc.noidentity.dto.UserAccountDto;
import hu.thesis.msc.noidentity.dto.UserDto;
import hu.thesis.msc.noidentity.dto.UserOrganizationAssignmentMinimalDataDto;
import hu.thesis.msc.noidentity.entity.UserAccount;
import hu.thesis.msc.noidentity.entity.UserOrganizationAssignment;
import hu.thesis.msc.noidentity.enums.Role;
import hu.thesis.msc.noidentity.exceptions.AppException;
import hu.thesis.msc.noidentity.mappers.UserAccountMapper;
import hu.thesis.msc.noidentity.repository.UserAccountRepository;
import hu.thesis.msc.noidentity.repository.UserOrganizationAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserAccountMapper userMapper;

    private final OrganizationService organizationService;

    private final UserOrganizationAssignmentRepository assignmentRepository;

    private final UserOrganizationAssignmentRepository userOrganizationAssignmentRepository;

    public UserDto login(CredentialsDto credentialsDto) {
        UserAccount user = userRepository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            UserDto dto = userMapper.toUserDto(user);
            dto.setOrganization(organizationService.getOrganizationOfUser(user));
            dto.setManager(getManagerForUser(user));
            dto.setStartDate(user.getStartDate());
            dto.setEndDate(user.getEndDate());
            dto.setEmail(user.getEmail());
            return dto;
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

        if (userDto.getAccountDto().getOrganization() != null) {
            organizationService.createUserOrganizationAssignment(new UserOrganizationAssignmentMinimalDataDto(
                    savedUser.getId(), userDto.getAccountDto().getOrganization().getId(), "member"));
        }

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

    public List<UserAccountDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userAccount -> {
                    UserAccountDto dto = userMapper.toUserAccountDto(userAccount);
                    assignmentRepository.findByUserAndAssignmentType(userAccount, "member")
                            .ifPresent(assignemnt -> dto.setOrganization(assignemnt.getOrganization()));
                    dto.setRole(userAccount.getRole().getAuthority());
                    return dto;
                }).collect(Collectors.toList());
    }


    public UserAccount updateUserAccount(UserAccountDto userDataFromClient) {
        UserAccount currentAccount = getUserOrElseThrow(userDataFromClient.getLogin());
        currentAccount.setEmail(userDataFromClient.getEmail());
        currentAccount.setFirstName(userDataFromClient.getFirstName());
        currentAccount.setLastName(userDataFromClient.getLastName());
        currentAccount.setRole(userDataFromClient.getRole().equals("USER") ? Role.USER : Role.ADMIN);
        currentAccount.setStartDate(userDataFromClient.getStartDate());
        currentAccount.setEndDate(userDataFromClient.getEndDate());
        userRepository.save(currentAccount);

        if (userDataFromClient.getOrganization() != null) {
            organizationService.createUserOrganizationAssignment(new UserOrganizationAssignmentMinimalDataDto(
                    currentAccount.getId(), userDataFromClient.getOrganization().getId(), "member"));
        }
        return currentAccount;

    }

    public UserAccount getUserOrElseThrow(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Cannot find user with login: " + login, HttpStatus.BAD_REQUEST));
    }

    public UserAccount getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("Cannot find user with id: " + id, HttpStatus.BAD_REQUEST));
    }

    public UserAccount getAdminUser() {
        return userRepository.findByLogin("noadmin").orElse(null);
    }

    public UserAccount getManagerForUser(UserAccount user) {
        return userOrganizationAssignmentRepository.findByUserAndAssignmentType(user, "member")
                .map(UserOrganizationAssignment::getOrganization)
                .flatMap(organization -> userOrganizationAssignmentRepository.findByOrganizationAndAssignmentType(organization, "manager"))
                .map(UserOrganizationAssignment::getUser)
                .orElse(getAdminUser());
    }
}
