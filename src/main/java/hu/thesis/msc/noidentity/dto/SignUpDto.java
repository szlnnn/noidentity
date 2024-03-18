package hu.thesis.msc.noidentity.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {


    private UserAccountDto accountDto;

    public String getFirstName() {
        return this.accountDto.getFirstName();
    }
    public   String getLastName() {
        return this.accountDto.getLastName();
    }
    public  String getEmail() {
        return this.accountDto.getEmail();
    }
    public   String getLogin() {
        return this.accountDto.getLogin();
    }

    public Date getStartDate() {
        return this.accountDto.getStartDate();
    }

    public Date getEndDate() {
        return this.accountDto.getEndDate();
    }

    public String getRole() {return this.accountDto.getRole();}

    @NotEmpty
    private char[] password;


}