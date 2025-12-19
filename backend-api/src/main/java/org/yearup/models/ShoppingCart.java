package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart
        // represent one user's cart
{
    // this map holds all the items in the cart
    // key - productid and value - shoppingcartitem ( product + quantity + pricing )
    // for each productid there is ont shopping cart item -- no duplicate product in the cart -- quantity can change
    private Map<Integer, ShoppingCartItem> items = new HashMap<>();


    public Map<Integer, ShoppingCartItem> getItems() {

        return items;
    }

    public void setItems(Map<Integer, ShoppingCartItem> items) {

        this.items = items;
    }

    public boolean contains(int productId) {

        return items.containsKey(productId);
    }

    public void add(ShoppingCartItem item) {

        items.put(item.getProductId(), item);
    }

    public ShoppingCartItem get(int productId) {

        return items.get(productId);
    }

    @JsonProperty("total")
    public BigDecimal getTotal() {
        // starts with zero
        BigDecimal total = items.values()
                .stream()
                // converts each ShoppingCartItem into its line total
                .map(i -> i.getLineTotal())
                // add all the line total together
                .reduce(BigDecimal.ZERO, (lineTotal, subTotal) -> subTotal.add(lineTotal));
        // returns the final cart total
        return total;
    }

}
