package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// this tells spring the class should be created and managed by spring
@Component
// it implements the interface, spring will create one instance of this class and inject whenever categorydao is needed
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    public MySqlCategoryDao(DataSource dataSource) {

        super(dataSource);
    }

    @Override
    // gives all categories as java objects
    public List<Category> getAllCategories() {

        // the list starts empty
        // the array list that will hold all the category objects
        // returned even if no rows exist
        ArrayList<Category> categories = new ArrayList<>();
        // getting all columns under categories
        // the database will return raw rows not category objects
        String sql = "SELECT * FROM categories;";

        try (
                // using the getconnection() method from MySqlDaoBase - cant directly use datasource cause its private
                Connection connection = getConnection();
                // preparing and executing the statment
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                // esecuteQuery runs the SELECT
                // resultset now holds all the returned database rows
                ResultSet resultSet = preparedStatement.executeQuery())
                // try with reousrces automatially closes eveything
        {
            // loop through each returned row by the database
            while (resultSet.next()) {
                // converts the current row into category object
                // mapRow :
                // reads the values from the row > creates the cat obj ( container ) > puts the values inside the container
                Category category = mapRow(resultSet);
                // add the fully populated cat obj in the arraylist
                categories.add(category);
            }
        } catch (SQLException e) {
            // if something goes wrong with the database, log the error
            System.err.println("Error while running getAllCategories(): " + e.getMessage());
        }
        // retuns the list of cat objs
        // if no rows existed, the list will be empty not NULL
        return categories;

    }

    @Override
    public Category getByCategoryID(int categoryId) {
        // get category obj by cat id
        //  ? is the placeholder for the cat id
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (
                // using the getconnection() method from MySqlDaoBase - cant directly use datasource cause its private
                Connection connection = getConnection();
                // preparing and executing the statment
                PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            // replace the ? in the SQL with actual catId
            preparedStatement.setInt(1,categoryId);
            // executes the query
            // resultset may contain 0 or 1 row
            ResultSet resultSet = preparedStatement.executeQuery();
            // checks if row exists, is resultSet.next is true, it is positioned on that row
            if(resultSet.next())
            // converts currrent row into cat obj

            {
                return mapRow(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error while running getById(): " + e.getMessage());
        }
        // if no row was found then it returns null -- "no cat with this id exists"
        return null;
    }

    @Override
    public Category create(Category category) {
        // telling database to create a new row in the categories table
        // the pk will be automatically generated
        String sql = """
                INSERT INTO categories(name,description)
                VALUES(?,?)
                """;
        try (
                Connection connection = getConnection();
                // statment.returngenkeys does not automatically retun the id
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            // take the data out of the cat object
            // bind it to sql as data
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());

            // execute the insert and returns how many rows were changed
            // this is where database generates the cat id but java doesnt know the value yet
            int rowsInserted = preparedStatement.executeUpdate();

            // saefty check to make sure only one row was inserted
            if (rowsInserted != 1) {
                System.err.println("Something went wrong, expected 1 row");
            }

            // asking database what pk was generated
            // resultSet doe not contain the cat data just the gen keys
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            // resultset starts before the first row so must move it before reading
            // moves the cursor to the first generated key
            resultSet.next();
            // read the first column of the generated key results
            // now java knows knows the cat id value
            int category_id = resultSet.getInt(1);
            // closes the resultset -- cant close after return as return will exit the method and the resutlset will remain open -- so must close before the retun + we alreadt got the resultset value
            resultSet.close();

            // returns the populated cat obj
            return getByCategoryID(category_id);

        } catch (SQLException e) {
            System.err.println("Error creating a new category: " + e.getMessage());
        }
        // if creation failed, return null
        return null;
    }

    @Override
    public boolean update(int categoryId, Category category) {

        // update row whos catId matches the value
        String sql = """
                UPDATE categories 
                SET name = ? , description = ?
                WHERE category_id = ?
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            // take updated values out of the cat obj
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            // this tells the database which row to update
            preparedStatement.setInt(3, categoryId);

            // executes the update statement and returns how many rows were updated
            int rowsUpdated = preparedStatement.executeUpdate();

            // returns true only if exactly one row was updated and falase if catId did not exist
            return  rowsUpdated == 1;
        }
        catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int categoryId) {

        // delete the row where catId matches the value
        String sql = """
                DELETE FROM categories
                WHERE category_id = ?
                """;
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            // bind the catid to the placeholder data -- tell the database which row to delete
            preparedStatement.setInt(1, categoryId);

            // execute the delete and return how many rows were affected
            int rowsDeleted = preparedStatement.executeUpdate();

            // return true only if exactly one row was deleted and flase if catId did not exist
            return rowsDeleted == 1;
        }

        catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }

        return false;
    }

    private Category mapRow(ResultSet row) throws SQLException {
        // retuns a category obj for the current database row
        // reads value from database and stores it inside the java variable
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        // creates an empty category obj
        // copies the data from the database into the java obj
        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        // returns the populated cat obj, represents one row from the data
        return category;
    }

}
