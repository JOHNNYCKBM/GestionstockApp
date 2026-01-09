package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PointVenteDAO {

    /* ===================== LISTE DES POINTS ===================== */

    public static List<String> getAllPoints() {

        List<String> list = new ArrayList<>();

        String sql = "SELECT nom FROM point_vente";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("nom"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ===================== ID PAR NOM ===================== */

    public static int getIdByName(String name) {

        String sql = "SELECT id_point FROM point_vente WHERE nom = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_point");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // non trouvé
    }
    public static void insertPoint(String name) {

        String sql = "INSERT INTO point_vente (nom) VALUES (?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
