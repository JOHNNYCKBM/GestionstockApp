package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public static List<String> getAllCategories() {

        List<String> list = new ArrayList<>();

        String sql = "SELECT nom FROM categorie ORDER BY nom";

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

    public static List<ProductSimple> getProductsByCategory(String categoryName) {

        List<ProductSimple> list = new ArrayList<>();

        String sql = """
            SELECT p.id_produit, p.nom, p.prix, p.quantite
            FROM produit p
            JOIN categorie c ON c.id_categorie = p.id_categorie
            WHERE c.nom = ?
            ORDER BY p.nom
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ProductSimple(
                        rs.getInt("id_produit"),
                        rs.getString("nom"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public static int getCategoryIdByName(String name) {

        String sql = "SELECT id_categorie FROM categorie WHERE nom = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_categorie");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // catégorie introuvable
    }
    public static void insertCategory(String name) {

        String sql = "INSERT INTO categorie (nom) VALUES (?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

