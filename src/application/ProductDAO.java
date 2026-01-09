package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static List<ProductSimple> getAllProducts() {

        List<ProductSimple> list = new ArrayList<>();

        String sql = """
            SELECT id_produit, nom, prix, quantite
            FROM produit
            ORDER BY nom
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            	list.add(new ProductSimple(
            	        rs.getInt("id_produit"),
            	        rs.getString("nom"),
            	        rs.getInt("quantite"),   // quantity
            	        rs.getDouble("prix")     // price
            	));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<ProductSimple> getLowStockProducts() {

        List<ProductSimple> list = new ArrayList<>();

        String sql = """
            SELECT id_produit, nom, prix, quantite
            FROM produit
            WHERE quantite < 5
            ORDER BY quantite ASC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            	list.add(new ProductSimple(
            	        rs.getInt("id_produit"),
            	        rs.getString("nom"),
            	        rs.getInt("quantite"),   // quantity
            	        rs.getDouble("prix")     // price
            	));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void deleteProduct(int productId) {

        String sql = "DELETE FROM produit WHERE id_produit = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void insertProduct(
            String name,
            double price,
            int quantity,
            int categoryId
    ) {

        String sql = """
            INSERT INTO produit (nom, prix, quantite, id_categorie)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, quantity);
            ps.setInt(4, categoryId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
