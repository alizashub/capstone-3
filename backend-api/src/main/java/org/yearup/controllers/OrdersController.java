package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.OrderLineItemDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController {

    private final OrderDao orderDao;
    private final OrderLineItemDao orderLineItemDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public OrdersController(OrderDao orderDao, OrderLineItemDao orderLineItemDao, ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public void checkout(Principal principal) {

        User user = getLoggedInUser(principal);
        ShoppingCart cart = getUserCart(user);
        Order order = new Order();

        order.setUserId(user.getId());
        order.setDate(LocalDateTime.now());
        order.setAddress("");
        order.setCity("");
        order.setState("");
        order.setZip("");
        order.setShippingAmount(cart.getTotal());

        int orderId = orderDao.create(order);

        for (ShoppingCartItem cartItem : cart.getItems().values()) {
            OrderLineItem lineItem = new OrderLineItem();

            lineItem.setOrderId(orderId);
            lineItem.setProductId(cartItem.getProductId());
            lineItem.setSalesPrice(cartItem.getProduct().getPrice());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setDiscount(cartItem.getDiscountPercent());

            orderLineItemDao.create(lineItem);
        }

        shoppingCartDao.clearCart(user.getId());
    }


    private User getLoggedInUser(Principal principal) {

        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User does not exist");
        }
        return user;
    }

    private ShoppingCart getUserCart(User user) {
        ShoppingCart cart = shoppingCartDao.getByUserId(user.getId());

        if ( cart == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping cart is empty");
        }
        return cart;
    }


}
