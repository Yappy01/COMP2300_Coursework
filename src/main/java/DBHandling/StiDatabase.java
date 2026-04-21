package DBHandling;

import Models.StiEntry;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class StiDatabase {
    public Boolean addSti(StiEntry sti) {
        String sql = "INSERT INTO sti_information (name, symptoms, prevention, treatment, risklevel) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, sti.getName());
            stmt.setString(2, sti.getSymptoms());
            stmt.setString(3, sti.getPrevention());
            stmt.setString(4, sti.getTreatment());
            stmt.setInt(5, sti.getRiskLevel());

            stmt.executeUpdate();

            // Get generated ID
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                sti.setStiId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2️⃣ Read (By ID)
    public StiEntry findById(int id) {
        String sql = "SELECT * FROM sti_information WHERE stiid = ?";
        StiEntry sti = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sti = new StiEntry(
                        rs.getInt("stiid"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risklevel")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sti;
    }

    public StiEntry findByName(String name) {
        String sql = "SELECT * FROM sti_information WHERE name = ?";
        StiEntry sti = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sti = new StiEntry(
                        rs.getInt("stiid"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risklevel")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sti;
    }

    // 3️⃣ Update
    public Boolean update(StiEntry sti) {
        String sql = "UPDATE sti_information SET name=?, symptoms=?, prevention=?, treatment=?, risklevel=? WHERE stiid=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sti.getName());
            stmt.setString(2, sti.getSymptoms());
            stmt.setString(3, sti.getPrevention());
            stmt.setString(4, sti.getTreatment());
            stmt.setInt(5, sti.getRiskLevel());
            stmt.setInt(6, sti.getStiId());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4️⃣ Delete
    public Boolean delete(int id) {
        String sql = "DELETE FROM sti_information WHERE stiid=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5️⃣ List All
    public ArrayList<StiEntry> getAll() {
        ArrayList<StiEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM sti_information";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StiEntry sti = new StiEntry(
                        rs.getInt("stiid"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risklevel")
                );
                list.add(sti);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<StiEntry> searchBySymptom(String keyword) {
        ArrayList<StiEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM sti WHERE symptoms LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%"); // add wildcards
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StiEntry sti = new StiEntry(
                        rs.getInt("stiid"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risklevel")
                );
                list.add(sti);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public String getFunFact(Integer id) {
        String sql = "SELECT * FROM fun_fact WHERE id = ?";
        String funFact = "";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                funFact = rs.getString("funfact");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return funFact;
    }

}
