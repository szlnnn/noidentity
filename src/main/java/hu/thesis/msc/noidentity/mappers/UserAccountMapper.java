package hu.thesis.msc.noidentity.mappers;

import hu.thesis.msc.noidentity.dto.SignUpDto;
import hu.thesis.msc.noidentity.dto.UserDto;
import hu.thesis.msc.noidentity.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserDto toUserDto(UserAccount user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    UserAccount signUpToUser(SignUpDto signUpDto);

}