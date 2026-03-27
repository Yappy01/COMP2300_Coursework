package Service;

import DBHandling.UserRepository;
import Models.User;



public class UserService {
    public User searchByUsername(String keyword) {
        return UserRepository.getUser(keyword);
    }
}
