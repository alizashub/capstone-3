package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
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

    @GetMapping
    public ShoppingCart getCart(Principal principal) {
        // get the currently logged in username
        String userName = principal.getName();
        // find database user by userId
        User user = userDao.getByUserName(userName);
        // return's the user's shopping cart
        return shoppingCartDao.getByUserId(user.getId());
    }


    @PostMapping("/products/{productId}")
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal) {
        try {
            // get logged in username
            String username = principal.getName();
            // converts username to userid
            User user = userDao.getByUserName(username);
            // add the product to the cart
            shoppingCartDao.addProduct(user.getId(), productId);
            // returns the updated shopping cart
            return shoppingCartDao.getByUserId(user.getId());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add product");
        }
    }

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

    @PutMapping("/products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal) {
        try {
            // get logged in username
            String username = principal.getName();
            // converts username to userid
            User user = userDao.getByUserName(username);
            // update the quanity for the specific product
            shoppingCartDao.updateProductQuantity(user.getId(), productId, item.getQuantity());
            // return the updated shopping cart
            return shoppingCartDao.getByUserId(user.getId());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product quantity.", e);
        }
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart

    @DeleteMapping
    public ShoppingCart clearCart(Principal principal) {
        try {
            // get logged-in username
            String username = principal.getName();
            // convert username to userid
            User user = userDao.getByUserName(username);
            // removes all cart items for the user
            shoppingCartDao.clearCart(user.getId());
            // return the now-empty shopping cart
            return shoppingCartDao.getByUserId(user.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to clear shopping cart");
        }
    }
}
