package hu.thesis.msc.noidentity.service;

import hu.thesis.msc.noidentity.entity.NoIdMUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    public List<NoIdMUser> getUsers() {
        return List.of(
                new NoIdMUser(1L,
                        "Csortan",
                        "Szilard",
                        "csoszi",
                        "csor@mail.hu")
        );
    }
}
