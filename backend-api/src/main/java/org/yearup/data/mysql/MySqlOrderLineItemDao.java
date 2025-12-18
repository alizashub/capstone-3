package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {

    public MySqlOrderLineItemDao(DataSource dataSource) {

        super(dataSource);
    }

    // inserts a single order line item into the database
    @Override
    public void create(OrderLineItem item) {

        String sql = """
                INSERT INTO order_line_items
                (order_id,product_id, sales_price, quantity, discount)
                VALUES (?,?,?,?,?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setInt(1,item.getOrderId());
            preparedStatement.setInt(2,item.getProductId());
            preparedStatement.setBigDecimal(3,item.getSalesPrice());
            preparedStatement.setInt(4,item.getQuantity());
            preparedStatement.setBigDecimal(5,item.getDiscount());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating order line item." , e);
        }

    }
}
