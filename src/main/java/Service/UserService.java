package Service;

import DBHandling.StiDatabase;
import DBHandling.UserRepository;
import Models.StiEntry;
import Models.User;

import java.util.List;

public class UserService {
    public User searchByUsername(String keyword) {
        return UserRepository.getUser(keyword);
    }
}
