package org.yearup.data.mysql;

import org.apache.ibatis.jdbc.SQL;
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

    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public ShoppingCart getByUserId(int userId) {

        // create an empty shoppingcart obj
        // if user has no items, it returns empty not null
        ShoppingCart cart = new ShoppingCart();

        // get quantity from shopping_cart table
        // get full product details from products table
        // join makes sure cart item has valid product
        // filter by given userId

        String sql = """
                SELECT 
                    shopping_cart.quantity,
                    products.product_id,
                    products.name,
                    products.price,
                    products.category_id,
                    products.description,
                    products.subcategory,
                    products.stock,
                    products.featured,
                    products.image_url
                                FROM shopping_cart
                                JOIN products ON shopping_cart.product_id = products.product_id
                                WHERE shopping_cart.user_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // bind the userId value to ?
            preparedStatement.setInt(1, userId);

            // execute and get result set
            try (ResultSet row = preparedStatement.executeQuery()) {

                // loop through each row returned from the database
                while (row.next()) {
                    // build's a product obj from the current database row
                    Product product = new Product(
                            row.getInt("product_id"),
                            row.getString("name"),
                            row.getBigDecimal("price"),
                            row.getInt("category_id"),
                            row.getString("description"),
                            row.getString("subcategory"),
                            row.getInt("stock"),
                            row.getBoolean("featured"),
                            row.getString("image_url")
                    );

                    // create a shopping cart for this product
                    ShoppingCartItem item = new ShoppingCartItem();
                    // attach the product to the cart item
                    item.setProduct(product);
                    // set the quanity from the shopping cart table
                    item.setQuantity(row.getInt("quantity"));
                    // defaults to zero
                    item.setDiscountPercent(BigDecimal.ZERO);

                    // add item to shopping cart
                    cart.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load shoppin cart for the user" + userId + e);
        }
        // retun the fully build shopping cart be it empty or populated
        return cart;
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

            try (PreparedStatement updateStament = connection.prepareStatement(updateSql)) {
                // bind values for the UPDATE statment
                updateStament.setInt(1, userId);
                updateStament.setInt(2, productId);

                // execute the update and store how many rows got updated
                int rowsUpdated = updateStament.executeUpdate();

                // if no rows updated, the product was not in the cart
                if (rowsUpdated == 0) {

                    // insert a new cart record with quantity = 1
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

                        insertStatement.setInt(1, userId);
                        insertStatement.setInt(2, productId);
                        insertStatement.executeUpdate();
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add product to cart" , e);
        }
    }

    @Override
    public void updateProductQuantity(int userId, int productId, int quantity) {

        String sql = """
                UPDATE shopping_cart 
                SET quantity = ?
                WHERE user_id = ? AND product_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, productId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product quantity" , e);
        }
    }

    @Override
    public void clearCart(int userId) {

        // delets all shopping cart items that belong to specific user

        String sql = """
                DELETE FROM shopping_cart
                WHERE user_id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            preparedStatement.setInt(1, userId);
            // execute delete and remove all cart items from user

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear shopping cart" , e);
        }

    }


}
