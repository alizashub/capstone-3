package org.yearup.data;

import org.yearup.models.OrderLineItem;

public interface OrderLineItemDao {

    // inserts a single order line item into database
    void create(OrderLineItem item);
}
