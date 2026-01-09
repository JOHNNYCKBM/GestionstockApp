package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDAO {

    /* ===================== VENTE ===================== */

    public static boolean sellProduct(int productId, int quantity) {

        try (Connection con = DBConnection.getConnection()) {

            // Vérifier le stock
            String checkSql =
                "SELECT quantite FROM produit WHERE id_produit = ?";
            PreparedStatement checkPs =
                con.prepareStatement(checkSql);
            checkPs.setInt(1, productId);

            ResultSet rs = checkPs.executeQuery();
            if (!rs.next() || rs.getInt("quantite") < quantity) {
                return false; // stock insuffisant
            }

            // Diminuer le stock
            String updateSql =
                "UPDATE produit SET quantite = quantite - ? WHERE id_produit = ?";
            PreparedStatement updatePs =
                con.prepareStatement(updateSql);
            updatePs.setInt(1, quantity);
            updatePs.setInt(2, productId);
            updatePs.executeUpdate();

            // Enregistrer le mouvement
            insertMovement(productId, "VENTE", quantity);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== APPROVISIONNEMENT ===================== */

    public static boolean supplyProduct(int productId, int quantity) {

        if (quantity <= 0) return false;

        try (Connection con = DBConnection.getConnection()) {

            // Augmenter le stock
            String updateSql =
                "UPDATE produit SET quantite = quantite + ? WHERE id_produit = ?";
            PreparedStatement ps =
                con.prepareStatement(updateSql);
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();

            // Enregistrer le mouvement
            insertMovement(productId, "APPRO", quantity);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== HISTORIQUE ===================== */

    private static void insertMovement(
            int productId, String type, int quantity) {

        String sql = """
            INSERT INTO mouvement_stock
            (id_produit, type_mouvement, quantite)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setString(2, type);
            ps.setInt(3, quantity);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<String[]> getAllMovements() {

        List<String[]> list = new ArrayList<>();

        String sql = """
            SELECT m.id_mouvement,
                   p.nom AS produit,
                   m.type_mouvement,
                   m.quantite,
                   m.date_mouvement
            FROM mouvement_stock m
            JOIN produit p ON p.id_produit = m.id_produit
            ORDER BY m.date_mouvement DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("id_mouvement"),
                    rs.getString("produit"),
                    rs.getString("type_mouvement"),
                    rs.getString("quantite"),
                    rs.getString("date_mouvement")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    /* ===================== TRANSFERT ===================== */

    public static boolean transferProduct(
            int productId, int pointId, int quantity) {

        if (quantity <= 0) return false;

        try (Connection con = DBConnection.getConnection()) {

            // 1. Vérifier stock central
            String checkSql =
                "SELECT quantite FROM produit WHERE id_produit = ?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setInt(1, productId);

            ResultSet rs = checkPs.executeQuery();
            if (!rs.next() || rs.getInt("quantite") < quantity) {
                return false;
            }

            // 2. Diminuer stock central
            String updateCentral =
                "UPDATE produit SET quantite = quantite - ? WHERE id_produit = ?";
            PreparedStatement ps1 = con.prepareStatement(updateCentral);
            ps1.setInt(1, quantity);
            ps1.setInt(2, productId);
            ps1.executeUpdate();

            // 3. Ajouter / mettre à jour stock point de vente
            String upsert =
                """
                INSERT INTO stock_point_vente (id_point, id_produit, quantite)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE quantite = quantite + ?
                """;

            PreparedStatement ps2 = con.prepareStatement(upsert);
            ps2.setInt(1, pointId);
            ps2.setInt(2, productId);
            ps2.setInt(3, quantity);
            ps2.setInt(4, quantity);
            ps2.executeUpdate();

            // 4. Historique
            insertMovement(productId, "TRANSFERT", quantity);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
