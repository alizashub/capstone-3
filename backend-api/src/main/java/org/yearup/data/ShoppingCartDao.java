package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    // get the full shopping cart for a user
    // uses userid to find user
    // returns a fully populated shopping cart object including items and product details
    ShoppingCart getByUserId(int userId);

    // add a product to a user's shopping cart
    // if product not already in cart - inserts a new record with quantity = 1
    // if product already in cart - increases quantity by 1
    void addProduct(int userId, int productId);

    // updates the quantity of a specific product in the cart
    // sets quantity to the provided value
    void updateProductQuantity(int userId, int productId, int quantity);

    // removes all items from the user's shopping cart
    void clearCart(int userId);





}
