package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao {
    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String subCategory) {
        // create an empty list that will hold the results
        List<Product> products = new ArrayList<>();

        // SQL query with optional filters
        // each filter is written so it is ignored if the parameter is null or empty
        String sql = """
                SELECT *
                FROM products
                WHERE (? IS NULL OR category_id = ?)
                  AND (? IS NULL OR price >= ?)
                  AND (? IS NULL OR price <= ?)
                  AND (? = '' OR subcategory = ?)
                """;

        // strings behave diff from int and cant be ignored if null directly
        // if subcat is null -- convert to empty string
        subCategory = subCategory == null ? "" : subCategory;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);

            // category
            statement.setObject(1, categoryId); // for IS NULL check
            statement.setObject(2, categoryId); // for the catId comparison

            // min price
            // If maxPrice is NULL → filter is ignored
            statement.setObject(3, minPrice); // for IS NULL check
            statement.setObject(4, minPrice); // for the price comparison

            // max price
            statement.setObject(5, maxPrice);
            statement.setObject(6, maxPrice);

            // subcategory
            // Empty string ("") means "ignore this filter"
            statement.setString(7, subCategory);
            statement.setString(8, subCategory);

            //execute query
            // database runs and returns matching rows

            // nested try-with-resources to close result set
            try (ResultSet row = statement.executeQuery()) {

                // loops through each retured row
                while (row.next()) {
                    // converts one database row into one product object
                    Product product = mapRow(row);

                    // add product to the list
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching products", e);
        }
        // returns the product list -- list can be empty but never null
        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId) {
        // create a list that will hold products for this category
        // starts with an empty list
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products " + " WHERE category_id = ? ";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);

            // loop through each row returned by the database
            try (ResultSet row = statement.executeQuery()) {

                while (row.next()) {
                    // convert database row into product object
                    Product product = mapRow(row);
                    // add product obj to the result list
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving products for category " + categoryId, e);
        }

        return products;
    }

    @Override
    public Product getById(int productId) {
        // get one product by pk
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, productId);

            // database returns 0 or 1 row
            // with .executeQuery make sure to use try with resources to close resultset explicity
            try(ResultSet row = statement.executeQuery()) {

                // checks if row exits, if true the cursor moves to that row
                if (row.next()) {
                    // converts database row into product obj
                    return mapRow(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving product with id " + productId, e);
        }
        // no row found -- product does not exist
        return null;
    }

    @Override
    public Product create(Product product) {
        // list all columns except pk -- generated automatically
        String sql = """
                INSERT INTO products(name, price, category_id, description, subcategory, image_url, stock, featured)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // take data out of the product obj and bind it into SQL statement as DATA
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getSubCategory());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());

            // execute returns - how many rows inserted
            int rowsInserted = statement.executeUpdate();

            // exactly one row should be inserted per product obj
            if (rowsInserted != 1) {
                throw new RuntimeException("Error inserting product.");
            }
            // ask database for generated keys
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                // Retrieve the auto-incremented ID
                int orderId = generatedKeys.getInt(1);

                //  get the fully populated product from the database
                return getById(orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating product: " + product.getName(), e);
        }
        return null;
    }

    @Override
    public boolean update(int productId, Product product) {
        // update row where product_id matches the value

        String sql = """
                UPDATE products
                        SET name = ?,
                            price = ?,
                            category_id = ?,
                            description = ?,
                            subcategory = ?,
                            image_url = ?,
                            stock = ?,
                            featured = ?
                        WHERE product_id = ?
                """;


        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // these are the new values that will overwrite the old ones
            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getPrice());
            preparedStatement.setInt(3, product.getCategoryId());
            preparedStatement.setString(4, product.getDescription());
            preparedStatement.setString(5, product.getSubCategory());
            preparedStatement.setString(6, product.getImageUrl());
            preparedStatement.setInt(7, product.getStock());
            preparedStatement.setBoolean(8, product.isFeatured());

            // this tells the database which row to update
            preparedStatement.setInt(9, productId);

            // returns how mnay rows were updated
            int rowsUpdated = preparedStatement.executeUpdate();

            // returns true is product existed and was updated and flase if there was no product with this ID
            return rowsUpdated == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating product with id " + productId, e);
        }
    }

    @Override
    public boolean delete(int productId) {
        // remove the row whose productId matches this value

        String sql = """
                DELETE FROM products
                WHERE product_id = ?
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // tell database which row to delete
            preparedStatement.setInt(1, productId);

            // returns how mnay rows were deleted
            int rowsDeleted = preparedStatement.executeUpdate();

            // returns true if product existed and 1 row was deleted and flase is product did not exist
            return rowsDeleted == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product with id " + productId, e);
        }
    }

    protected static Product mapRow(ResultSet row) throws SQLException {
        // create an empty product object -- just like a container -- with no data
        Product product = new Product();

        // read values from the database row
        // copy them into the product obj

        product.setProductId(row.getInt("product_id"));
        product.setName(row.getString("name"));
        product.setPrice(row.getBigDecimal("price"));
        product.setCategoryId(row.getInt("category_id"));
        product.setDescription(row.getString("description"));
        product.setSubCategory(row.getString("subcategory"));
        product.setStock(row.getInt("stock"));
        product.setFeatured(row.getBoolean("featured"));
        product.setImageUrl(row.getString("image_url"));

        // retun the full populated product obj
        // this product represents one database row
        return product;
    }
}
