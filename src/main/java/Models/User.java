package Models;

public class User {
    private Integer userId;
    private String name;
    private String password;
    private String email;
    private String answer;

    public User(String name, String password, String email,String answer) {
        this.userId = null;
        this.name = name;
        this.password = password;
        this.email = email;
        this.answer = answer;
    }

    public User(Integer userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public String getName() { return name;}

    public String getPassword() { return password;}

    public String getEmail() { return email;}
    public String getAnswer() { return answer;}
    public Integer getUserId() { return userId;}


}
