package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    // get the full shopping cart for a user
    ShoppingCart getByUserId(int userId);


    void addProduct(int userId, int productId);

    // set quantity
    void updateProductQuantity(int userId, int productId, int quantity);

    // clear cart
    void clearCart(int userId);





}
