package Models;

public class User {
    private String name;
    private String password;
    private String email;
    private String answer;

    public User(String name, String password, String email,String answer) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.answer = answer;
    }

    public String getName() { return name;}

    public String getPassword() { return password;}

    public String getEmail() { return email;}
    public String getAnswer() { return answer;}


}
