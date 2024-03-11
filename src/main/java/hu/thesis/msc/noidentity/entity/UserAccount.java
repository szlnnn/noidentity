package hu.thesis.msc.noidentity.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class NoIdMUser {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String firstName;
    private String lastName;
    private String accountId;
    private String email;

    public NoIdMUser(Long id, String firstName, String lastName, String accountId, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountId = accountId;
        this.email = email;
    }

    public NoIdMUser(String firstName, String lastName, String accountId, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountId = accountId;
        this.email = email;
    }

    public NoIdMUser() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;


    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", accountId='" + accountId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
