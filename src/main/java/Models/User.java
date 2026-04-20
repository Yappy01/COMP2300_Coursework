package Models;

public class User {
    private Integer userId;
    private String name;
    private String password;
    private String email;
    private String answer;
    private String role;

    public User(String name, String password, String email, String answer, String role) {
        this.userId = null;
        this.name = name;
        this.password = password;
        this.email = email;
        this.answer = answer;
        this.role = role;
    }

    public User(Integer userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() { return name;}

    public String getPassword() { return password;}

    public String getEmail() { return email;}
    public String getAnswer() { return answer;}
    public Integer getUserId() { return userId;}

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.valueOf(userId);
    }
}
