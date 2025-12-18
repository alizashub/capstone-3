package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
@RestController
@RequestMapping("/cart")
@CrossOrigin

// only loggedin users should have access to these actions
public class ShoppingCartController {

    // a shopping cart requires
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;


    // constructor injection
    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    // each method in this controller requires a Principal object as a parameter

    @GetMapping("")
    public ShoppingCart getCart(Principal principal) {

        User user = getAuthenticatedUser(principal);

        // asks the shoppincartdao to build cart of user based on user id from the authenticated user
        // create shopping cart item objects
        // adds them to the shopping cart
        // return a fully built cart - converts object to JSON
        ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());
        return cart;
    }


    @PostMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal) {

        // get the database user with the userid
        User user = getAuthenticatedUser(principal);

        try {
            // add the product to the users cart in the database ( update or insert based on if product exists )
            // for this user add this product to the cart
            shoppingCartDao.addProduct(user.getId(), productId);

            // reload and return the updated shopping cart
            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product to cart", e);
        }
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

    @PutMapping("/products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal)  {

        if (item.getQuantity() <= 0 )
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        // identify if user is authentic - ensure updating the correct users cart
        User user = getAuthenticatedUser(principal);

        try {
            // update the quanity for the specific product
            shoppingCartDao.updateProductQuantity(user.getId(), productId, item.getQuantity());
            // reload and return the updated shopping cart - shows new quantity and re-calcualted totals
            return shoppingCartDao.getByUserId(user.getId());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product quantity.", e);
        }
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

    @DeleteMapping
    public ShoppingCart clearCart(Principal principal) {

        // ensure clearing cart for the correct user
        User user = getAuthenticatedUser(principal);


        try {
            // clear all items for user in the database
            shoppingCartDao.clearCart(user.getId());
            // reload and return the now-empty shopping cart -- where total is 0
            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to clear shopping cart", e);
        }
    }

    private User getAuthenticatedUser(Principal principal) {

        // if principal is null spring did not attach a logged in user
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        // get the currently logged in username - from the login token
        String userName = principal.getName();

        // find database user that matches username - to get userid
        User user = userDao.getByUserName(userName);

        // if no user record exists, return a 404
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // return the fully loaded user object - this is confirmation that the user is verified and in the database
        return user;
    }

}
