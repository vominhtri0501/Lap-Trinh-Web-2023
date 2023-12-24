package service;


import database.dao.ProductDAO;

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


}
