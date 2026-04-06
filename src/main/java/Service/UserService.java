package Service;

import DBHandling.UserRepository;
import Models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class UserService {
    private final UserRepository userRepository = new UserRepository();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    public User searchByUsername(String keyword) {
        return UserRepository.getUser(keyword);
    }
}
