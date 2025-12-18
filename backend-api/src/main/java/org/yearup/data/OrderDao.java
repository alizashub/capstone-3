package org.yearup.data;

import org.yearup.models.Order;

public interface OrderDao {

    // creates a new order in the database
    // returns the orderid
    int create(Order order);
}
