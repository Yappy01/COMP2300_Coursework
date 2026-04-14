package Service;

import DBHandling.UserRepository;
import Models.User;
import javafx.concurrent.Task;
import utils.General;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class UserService {
    private final UserRepository userRepository = new UserRepository();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    public User searchByUsername(String keyword) {
        return userRepository.getUser(keyword);
    }

    public void getUserName(Integer userId, Consumer<String> onSucceeded, Consumer<Throwable> onFailed) {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return userRepository.getUserName(userId);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void change_notetoselfAsync(String username, String note, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userRepository.change_notetoself(username, note);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void fetch_notetoselfAsync(String name, Consumer<String> onSucceeded, Consumer<Throwable> onFailed) {
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return userRepository.fetch_notetoself(name);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void secureLoginAsync(String name, String password, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userRepository.secureLogin(name, password);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public boolean checkUserExist(String user) {
        return userRepository.checkUserExist(user);
    }

    public boolean checkEmailExist(String email) {
        return userRepository.checkEmailExist(email);
    }

    public void getAllUserAsync(Consumer<ArrayList<User>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<ArrayList<User>> task = new Task<ArrayList<User>>() {
            @Override
            protected ArrayList<User> call() throws Exception {
                return userRepository.getAllUser();
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void register_userAsync(User user, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userRepository.register_user(user);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void check_userAsync(String username, String email, String answer, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userRepository.check_user(username,email, answer);
            }
        };
        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public boolean change_password(String username, String password) {
        try {
            return userRepository.change_password(username, password);
        } catch (SQLException|ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void getUserFullProfileAsync(int userId, Consumer<Map<String, String>> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Map<String, String>> task = new Task<Map<String, String>>() {
            @Override
            protected Map<String, String> call() throws Exception {
                return userRepository.getUserFullProfile(userId);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void change_personalInformationAsync(Integer userid, String phone_number, String date_of_birth, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userRepository.change_personalInformation(userid, phone_number, date_of_birth);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }

    public void change_anamnesisAsync(Integer userid, String allergies,String chronic_disease, String bloodtype, String injuriesillness, Consumer<Boolean> onSucceeded, Consumer<Throwable> onFailed) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userRepository.change_anamnesis(userid, allergies, chronic_disease, bloodtype, injuriesillness);
            }
        };

        General.setTask(task, onSucceeded, onFailed, executor);
    }
}
