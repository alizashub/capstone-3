package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {
    public MySqlProfileDao(DataSource dataSource) {

        super(dataSource);
    }

    @Override
    public Profile create(Profile profile) {
        // inserts a new profile row into the profiles table

        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            // maps profile object fields
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            // executes update and returns the number of rows updated
            ps.executeUpdate();

            // returns the same profile object that was inserted
            return profile;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile getByUserId(int userId) {
        // retrieve a single profile by user_id
        String sql = """
                SELECT 
                user_id, 
                first_name, 
                last_name, 
                phone, 
                email, 
                address,
                city, 
                state, 
                zip
                FROM profiles
                WHERE user_id = ?
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // create a new profile object
                Profile profile = new Profile();
                // map each column from the result set to the profile obj
                profile.setUserId(resultSet.getInt("user_id"));
                profile.setFirstName(resultSet.getString("first_name"));
                profile.setLastName(resultSet.getString("last_name"));
                profile.setPhone(resultSet.getString("phone"));
                profile.setEmail(resultSet.getString("email"));
                profile.setAddress(resultSet.getString("address"));
                profile.setCity(resultSet.getString("city"));
                profile.setState(resultSet.getString("state"));
                profile.setZip(resultSet.getString("zip"));

                // return the populated profile object
                return profile;
            }
            // if no row was found returns null
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve profile for userId: " + userId + e);
        }
    }

    @Override
    public boolean update(Profile profile) {

        // updates an exisiting profile
        // users can only update their own profile thus only one row is ever updated
        String sql = """
                UPDATE profiles 
                SET 
                first_name = ?,
                last_name = ?,
                phone = ?,
                email = ?,
                address = ?,
                city = ?,
                state = ?,
                zip = ?
                WHERE user_id = ?
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, profile.getFirstName());
            preparedStatement.setString(2, profile.getLastName());
            preparedStatement.setString(3, profile.getPhone());
            preparedStatement.setString(4, profile.getEmail());
            preparedStatement.setString(5, profile.getAddress());
            preparedStatement.setString(6, profile.getCity());
            preparedStatement.setString(7, profile.getState());
            preparedStatement.setString(8, profile.getZip());

            // identifies which row to update - where clause
            preparedStatement.setInt(9, profile.getUserId());

            // updates and gets how many rows where updated
            int rowsUpdated = preparedStatement.executeUpdate();

            // if 1 row is updated , returns true
            return rowsUpdated == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update profile for userId: " + profile.getUserId(), e);
        }
    }

}
