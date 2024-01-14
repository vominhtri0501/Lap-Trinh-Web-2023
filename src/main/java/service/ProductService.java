package service;


import database.DbConnection;
import database.dao.ProductDAO;
import model.Product;
import model.StatusProduct;
import model.SubTypeProduct;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProductService {
    private static final String QUERY_PRODUCTS =
            "SELECT p.id_product, url_img_product, name_product, description_product, quantity_product, " +
                    "date_inserted, name_status_product, name_type_product, name_subtype, " +
                    "name_supplier, views, pp.current_price, pp.listed_price " +
                    "FROM products p JOIN price_product pp ON p.id_product = pp.id_product " +
                    "JOIN status_product sp ON p.id_status_product = sp.id_status_product " +
                    "JOIN subtype_product st ON p.id_subtype = st.id_subtype " +
                    "JOIN suppliers s ON p.id_supplier = s.id_supplier " +
                    "JOIN type_product tp ON st.id_type_product = tp.id_type_product\t";

    public static List<Product> queryProducts(String query, Object... params) {
        try (var ps = DbConnection.getInstance().getPreparedStatement(query)) {
            for (int i = 0; i < params.length; i++)
                ps.setObject(i + 1, params[i]);
            return getProducts(ps.executeQuery());
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    private static int queryInt(String query, Object... params) {
        try (var ps = DbConnection.getInstance().getPreparedStatement(query)) {
            for (int i = 0; i < params.length; i++)
                ps.setObject(i + 1, params[i]);
            var rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> queryMap(String query, Object... params) {
        Map<K, V> map = new HashMap<>();
        try (var ps = DbConnection.getInstance().getPreparedStatement(query)) {
            for (int i = 0; i < params.length; i++)
                ps.setObject(i + 1, params[i]);
            var rs = ps.executeQuery();
            while (rs.next()) {
                K k = (K) rs.getObject(1);
                V v = (V) rs.getObject(2);
                map.put(k, v);
            }
            return map;
        } catch (SQLException e) {
            return new HashMap<>();
        }
    }

    ////////////////////////////

    public static List<Product> getAllProducts() {
        DbConnection connectDB = DbConnection.getInstance();
        ProductDAO dao = new ProductDAO();
        try {
            return dao.getAllProducts(connectDB);
        } finally {
            connectDB.close();
        }

    }

    public static List<Object> getSubTypeAndStatusAndSupplierForProduct() {
        DbConnection connectDB = DbConnection.getInstance();
        ProductDAO dao = new ProductDAO();
        List<Object> result = new ArrayList<>();

        List<SubTypeProduct> subtypeProducts = dao.getSubTypeProducts(connectDB);
        List<StatusProduct> statusProducts = dao.getStatusProducts(connectDB);
        List<Supplier> suppliers = dao.getSuppliers(connectDB);

        result.add(subtypeProducts);
        result.add(statusProducts);
        result.add(suppliers);

        connectDB.close();
        return result;
    }
    }

