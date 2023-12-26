package database.dao;

import database.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductDAO {

    public ProductDAO() {
    }

    public boolean insertProduct(DbConnection connectDB, Product p, String nameAdmin) {
        String sql = "insert into products (name_product,description_product,url_img_product,quantity_product" + ",id_subtype,id_status_product,id_supplier,nameAdmin) " + "values(?,?,?,?,?,?,?,?)";
        PreparedStatement preStatement = connectDB.getPreparedStatement(sql);
        try {
            preStatement.setString(1, p.getName());
            preStatement.setString(2, p.getDesc());
            preStatement.setString(3, p.getImgPath());
            preStatement.setInt(4, p.getQuantity());
            preStatement.setInt(5, p.getType_product());
            preStatement.setInt(6, p.getStatus_product());
            preStatement.setInt(7, p.getSupplier());
            preStatement.setString(8, nameAdmin);
            int rowInserted = preStatement.executeUpdate();
            if (rowInserted > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public int getIdProduct(DbConnection connectDB, Product p) {
        String sql = "SELECT id_product FROM products" + " WHERE name_product=? AND description_product= ? AND url_img_product= ? AND quantity_product= ?" + " AND id_subtype=? AND id_status_product= ? AND id_supplier= ?";
        PreparedStatement preStatement = connectDB.getPreparedStatement(sql);
        try {
            preStatement.setString(1, p.getName());
            preStatement.setString(2, p.getDesc());
            preStatement.setString(3, p.getImgPath());
            preStatement.setInt(4, p.getQuantity());
            preStatement.setInt(5, p.getType_product());
            preStatement.setInt(6, p.getStatus_product());
            preStatement.setInt(7, p.getSupplier());
            ResultSet rs = preStatement.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Product> getAllProducts(DbConnection connectDB) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT P.id_product,P.name_product,P.url_img_product,P.quantity_product,P.id_subtype,SP.id_status_product,SP.name_status_product,PP.listed_price,PP.current_price " + "FROM products P " + "JOIN price_product PP ON P.id_product=PP.id_product " + "JOIN status_product SP ON P.id_status_product = SP.id_status_product " + "WHERE PP.date = (SELECT MAX(date) " + "FROM price_product PP2 " + "WHERE PP2.id_product = PP.id_product)";
        Statement statement = connectDB.getStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                StatusProduct statusP = new StatusProduct(rs.getInt("id_status_product"), rs.getString("name_status_product"));
                SubTypeProduct subtypeP = new SubTypeProduct(rs.getInt("id_subtype"));
                Product product = new Product(rs.getInt("id_product"), rs.getString("url_img_product"), rs.getString("name_product"), rs.getInt("quantity_product"), rs.getInt("listed_price"), rs.getInt("current_price"), subtypeP, statusP);
                list.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public boolean deleteProductByIdOnTable_price_product(DbConnection connectDB, int id) throws SQLException {
        String sql = "DELETE FROM price_product WHERE id_product=?";
        PreparedStatement preState = connectDB.getPreparedStatement(sql);
        preState.setInt(1, id);
        int row = preState.executeUpdate();
        if (row >= 0) return true;
        return false;
    }

    public boolean deleteProductByIdOnTable_sold_product(DbConnection connectDB, int id) throws SQLException {
        String sql = "DELETE FROM sold_product WHERE id_product=?";
        PreparedStatement preState = connectDB.getPreparedStatement(sql);
        preState.setInt(1, id);
        int row = preState.executeUpdate();
        if (row >= 0) return true;
        return false;
    }

    public boolean deleteProductByIdOnTable_products(DbConnection connectDB, int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id_product=?";
        PreparedStatement preState = connectDB.getPreparedStatement(sql);
        preState.setInt(1, id);
        int row = preState.executeUpdate();
        if (row >= 0) return true;
        return false;
    }

    public void updateQuantity(int productId, int quantity) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try (var ps = DbConnection.getInstance().getPreparedStatement(
                    "UPDATE products SET quantity_product = ? WHERE id_product = ?"
            )) {
                ps.setInt(1, quantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
