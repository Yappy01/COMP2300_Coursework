package DBHandling;

import Models.StiEntry;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class StiDatabase {
    public static void save(StiEntry sti) {
        String sql = "INSERT INTO sti_information (name, symptoms, prevention, treatment, risk_level) VALUES (?, ?, ?, ?, ?)";
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2️⃣ Read (By ID)
    public static StiEntry findById(int id) {
        String sql = "SELECT * FROM sti_information WHERE sti_id = ?";
        StiEntry sti = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sti = new StiEntry(
                        rs.getInt("sti_id"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risk_level")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sti;
    }

    // 3️⃣ Update
    public static void update(StiEntry sti) {
        String sql = "UPDATE sti_information SET name=?, symptoms=?, prevention=?, treatment=?, risk_level=? WHERE sti_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sti.getName());
            stmt.setString(2, sti.getSymptoms());
            stmt.setString(3, sti.getPrevention());
            stmt.setString(4, sti.getTreatment());
            stmt.setInt(5, sti.getRiskLevel());
            stmt.setInt(6, sti.getStiId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4️⃣ Delete
    public static void delete(int id) {
        String sql = "DELETE FROM sti_information WHERE sti_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5️⃣ List All / Search
    public static ArrayList<StiEntry> getAll() {
        ArrayList<StiEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM sti_information";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StiEntry sti = new StiEntry(
                        rs.getInt("sti_id"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risk_level")
                );
                list.add(sti);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<StiEntry> searchBySymptom(String keyword) {
        ArrayList<StiEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM sti WHERE symptoms LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%"); // add wildcards
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                StiEntry sti = new StiEntry(
                        rs.getInt("sti_id"),
                        rs.getString("name"),
                        rs.getString("symptoms"),
                        rs.getString("prevention"),
                        rs.getString("treatment"),
                        rs.getInt("risk_level")
                );
                list.add(sti);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
