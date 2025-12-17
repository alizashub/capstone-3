package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    // give a userid and return the shoppingcart object for that user
    @Override
    // uses pk to retun object
    public ShoppingCart getByUserId(int userId) {

        // create an empty cart
        ShoppingCart cart = new ShoppingCart();

        // for the userid, give me every product in their cart, including details and quantity
        String sql = """
                SELECT 
                shopping_cart.quantity, 
                products.*
                FROM shopping_cart
                JOIN products ON shopping_cart.product_id = products.product_id
                WHERE shopping_cart.user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet row = preparedStatement.executeQuery()) {
                while (row.next()) {

                    Product product = new Product(row.getInt("product_id"),
                            row.getString("name"),
                            row.getBigDecimal("price"),
                            row.getInt("category_id"),
                            row.getString("description"),
                            row.getString("subcategory"),
                            row.getInt("stock"),
                            row.getBoolean("featured"),
                            row.getString("image_url")
                    );

                    // build shoppingcartitem
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(row.getInt("quantity"));
                    item.setDiscountPercent(BigDecimal.ZERO);

                    // add item to cart
                    cart.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load shoppin cart for the user" + userId + e);
        } return cart;
    }

    @Override
    public void addProduct(int userId, int productId) {

        // increse quantity if product already exists in cart
        String updateSql = """
                UPDATE shopping_cart
                SET quantity = quantity + 1 
                WHERE user_id = ? AND product_id = ?
                """;
        // insert product if it does not exist in cart
        String insertSql = """
                INSERT INTO shopping_cart ( user_id, product_id, quantity)
                VALUES(?,?,1)
                """;

        try (Connection connection = getConnection()) {

            try ( PreparedStatement updateStament = connection.prepareStatement(updateSql))
            {
                updateStament.setInt(1,userId);
                updateStament.setInt(2,productId);

                int rowsUpdated = updateStament.executeUpdate();

                if (rowsUpdated == 0)
                {
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql))
                    {
                        insertStatement.setInt(1,userId);
                        insertStatement.setInt(2,productId);

                        insertStatement.executeUpdate();
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException( "Failed to add product to cart" + e);
        }

    }

    @Override
    public void updateProductQuantity(int userId, int productId, int quantity) {
        // implement later
    }

    @Override
    public void clearCart(int userId) {
        // implment later
    }


}
