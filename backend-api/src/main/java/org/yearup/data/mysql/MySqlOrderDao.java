package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    // returns order_id
    @Override
    public int create(Order order) {

        // inserts 1 order
        String sql = """
                INSERT INTO orders
                (user_id, date, address, city, state, zip, shipping_amount)
                VALUES (?,?,?,?,?,?,?)
                """;

        try (Connection connection = getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            // binding parameters
            preparedStatement.setInt(1, order.getUserId());
            // converts local date time obj into SQL timestamp and then stores it into the data column of the order row
            preparedStatement.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            preparedStatement.setString(3, order.getAddress());
            preparedStatement.setString(4, order.getCity());
            preparedStatement.setString(5, order.getState());
            preparedStatement.setString(6, order.getZip());
            preparedStatement.setBigDecimal(7, order.getShippingAmount());

            // executes the insert
            preparedStatement.executeUpdate();

            // gets the generated order id key
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new RuntimeException("Failed to get generated orderId");

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating order", e);
        }

    }
}
