package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories() {

        // the array list that will hold the category objects
        ArrayList<Category> categories = new ArrayList<>();
        // getting all column under categories
        String sql = "SELECT * FROM categories;";

        try (
                // using the getconnection() method from MySqlDaoBase - cant directly use datasource cause its private
                Connection connection = getConnection();
                // preparing and executing the statment
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery())
        {
            while (resultSet.next()) {
                // converts row into category object
                Category category = mapRow(resultSet);
                // stores it inside the array list
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error while running getAllCategories(): " + e.getMessage());
        }
        return categories;

    }

    @Override
    public Category getByCategoryID(int categoryId) {
        // get category by id
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (
                // using the getconnection() method from MySqlDaoBase - cant directly use datasource cause its private
                Connection connection = getConnection();
                // preparing and executing the statment
                PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setInt(1,categoryId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                return mapRow(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error while running getById(): " + e.getMessage());
        }
        // if no category found return null
        return null;
    }


    @Override
    public Category create(Category category) {
        // create a new category
        String sql = """
                INSERT INTO categories(name,description)
                VALUES(?,?)
                """;
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted != 1) {
                System.err.println("Something went wrong, expected 1 row");
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int category_id = resultSet.getInt(1);

            resultSet.close();

            return getByCategoryID(category_id);

        } catch (SQLException e) {
            System.err.println("Error creating a new category: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(int categoryId, Category category) {
        // update category

        String sql = """
                UPDATE categories 
                SET name = ? , description = ?
                WHERE category_id = ?
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            preparedStatement.setInt(3, categoryId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
    }

    @Override
    public void delete(int categoryId) {
        // delete category
        String sql = """
                DELETE FROM categories
                WHERE category_id = ?
                """;
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, categoryId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
