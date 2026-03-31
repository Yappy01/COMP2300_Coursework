package DBHandling;
import Models.StiEntry;
import Models.User; //import the model of user
import utils.DBConnection; //import the dbconnector
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserRepository {
    //add new user, hashed password
    public boolean register_user(User user) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO users (name,email,password,registration_answer) VALUES (?, ?, ?, ?)";

        //generate the hash
        String hash_password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement insert_user_stmt = conn.prepareStatement(query)) {
            insert_user_stmt.setString(1, user.getName().trim());
            insert_user_stmt.setString(2, user.getEmail());
            insert_user_stmt.setString(3, hash_password);
            insert_user_stmt.setString(4, user.getAnswer());

            insert_user_stmt.executeUpdate();

            System.out.println("Registration Successful");
//            conn.close();
            return true;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("User not registered");
        return false;
    }

    //check for hashed password, for secure login
    public int secureLogin(String name, String password) throws SQLException, ClassNotFoundException {
        String query = "SELECT \"userId\", password FROM users WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement login_stmt = conn.prepareStatement(query)) {
            login_stmt.setString(1, name.trim());

            try (ResultSet rs = login_stmt.executeQuery()) {
                if (rs.next()) {
                    String stored_password = rs.getString("password");


                    if (BCrypt.checkpw(password, stored_password)) {
//                        System.out.println("Logged in successfully");
                        return rs.getInt("\"userId\"");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Invalid username or password");
        return -1;

    }

    //check for a specific user based on the username and email
    public boolean check_user(String username, String email, String answer) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM users WHERE name = ? and email = ? and registration_answer = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement search_user_stmt = conn.prepareStatement(query)) {
            System.out.println("Looking for user with name and email");

            search_user_stmt.setString(1, username.trim());
            search_user_stmt.setString(2, email);
            search_user_stmt.setString(3, answer);


            try (ResultSet rs = search_user_stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("User found");
//                    conn.close();
                    return true;

                }else{
                    System.out.println("User not found");
//                    conn.close();
                    return false;
                }
            }catch(SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    //change password in database
    public boolean change_password(String username, String password) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET password = ? WHERE name = ?";
        String hash_password = BCrypt.hashpw(password, BCrypt.gensalt(12));

        try (Connection conn = DBConnection.getConnection();

             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, hash_password);
            update_user_stmt.setString(2, username.trim());
            update_user_stmt.executeUpdate();

            System.out.println("Password changed successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Password not changed");
        return false;
    }

    //change note in database
    public boolean change_notetoself(String username, String note) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET notetoself = ? WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, note.trim());
            update_user_stmt.setString(2, username.trim());
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //fetch note based on username
    public String fetch_notetoself(String name) throws SQLException, ClassNotFoundException {
        String query = "SELECT notetoself FROM users WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement search_user_stmt = conn.prepareStatement(query)) {
            search_user_stmt.setString(1, name.trim());

            try (ResultSet rs = search_user_stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("notetoself");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //check if the username already exist
    public boolean checkUserExist(String user) {
        String query = "SELECT count(1) FROM users WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement count_stmt = conn.prepareStatement(query)) {

            count_stmt.setString(1, user);
            ResultSet rs = count_stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getUser(String username) {
        String query = "SELECT * FROM users WHERE name = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement count_stmt = conn.prepareStatement(query)) {

            count_stmt.setString(1, username);
            ResultSet rs = count_stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt("userId"),
                        rs.getString("name"),
                        rs.getString("email")
                );

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserName(Integer userId) {
        String query = "SELECT name FROM users WHERE \"userId\" = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement count_stmt = conn.prepareStatement(query)) {

            count_stmt.setInt(1, userId);
            ResultSet rs = count_stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    //check if the email already exists in the database
    public boolean checkEmailExist(String email) {
        String query = "SELECT count(1) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement count_stmt = conn.prepareStatement(query)) {

            count_stmt.setString(1, email);
            ResultSet rs = count_stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean change_phonenumber(Integer userid, String phone_number) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET phone_number = ? WHERE \"userId\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, phone_number.trim());
            update_user_stmt.setInt(2,userid);
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean change_date_of_birth(Integer userid, String date_of_birth) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET date_of_birth = ? WHERE \"userId\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, date_of_birth.trim());
            update_user_stmt.setInt(2, userid);
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean change_allergies(Integer userid, String allergies) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET allergies = ? WHERE \"userId\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, allergies.trim());
            update_user_stmt.setInt(2, userid);
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean change_cd(Integer userid,  String chronic_disease) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET chronic_disease = ? WHERE \"userId\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, chronic_disease.trim());
            update_user_stmt.setInt(2, userid);
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean change_blood_type(Integer userid, String blood_type) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET blood_type = ? WHERE \"userId\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, blood_type.trim());
            update_user_stmt.setInt(2, userid);
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean change_injuries_illness(Integer userid,String injuries_illness) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET injuries_illness = ? WHERE \"userId\" = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement update_user_stmt = conn.prepareStatement(query)) {
            update_user_stmt.setString(1, injuries_illness.trim());
            update_user_stmt.setInt(2,userid);
            update_user_stmt.executeUpdate();
            System.out.println("Note updated in the database successfully");
            return true;

        }catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
