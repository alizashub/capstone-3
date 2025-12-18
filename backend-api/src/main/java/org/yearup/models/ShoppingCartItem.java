package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
// as a whole represents one product and its quantity and price
// ShoppingCart is a container that manages ShoppingCartItems.
public class ShoppingCartItem
{
    // the full product obj
    private Product product = null;
    // defaults to 1 because adding a product adds one unit
    private int quantity = 1;
    private BigDecimal discountPercent = BigDecimal.ZERO;


    public Product getProduct()
    {

        return product;
    }

    public void setProduct(Product product)
    {

        this.product = product;
    }

    public int getQuantity()
    {

        return quantity;
    }

    public void setQuantity(int quantity)
    {

        this.quantity = quantity;
    }

    public BigDecimal getDiscountPercent()
    {

        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent)
    {

        this.discountPercent = discountPercent;
    }

    // returns the productId of the product in this cart
    // used by shopping cart as the map key
    @JsonIgnore
    public int getProductId()
    {
        return
                this.product.getProductId();
    }

    // calculates the total price for this cart item -- the line only
    // ( price * quantity ) - discount
    public BigDecimal getLineTotal()
    {
        BigDecimal basePrice = product.getPrice();
        BigDecimal quantity = new BigDecimal(this.quantity);

        BigDecimal subTotal = basePrice.multiply(quantity);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);

        return subTotal.subtract(discountAmount);
    }
}
