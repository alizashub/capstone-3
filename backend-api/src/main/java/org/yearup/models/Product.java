package org.yearup.models;

import java.math.BigDecimal;

// represents one product

public class Product {
    // primary key
    private int productId;
    private String name;
    // bigdecimal used instead of double to avoid rounding errors
    private BigDecimal price;
    // foreign key linking product to category
    private int categoryId;
    private String description;
    // optional grouping inside category
    private String subCategory;
    private int stock;
    private boolean isFeatured;
    private String imageUrl;


    // default constructor required by spring
    public Product() {
    }

    // used when creating product object from database rows
    public Product(int productId, String name, BigDecimal price, int categoryId, String description, String subCategory, int stock, boolean isFeatured, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.description = description;
        this.subCategory = subCategory;
        this.stock = stock;
        this.isFeatured = isFeatured;
        this.imageUrl = imageUrl;
    }

    public int getProductId() {

        return productId;
    }

    public void setProductId(int productId) {

        this.productId = productId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public BigDecimal getPrice() {

        return price;
    }

    public void setPrice(BigDecimal price) {

        this.price = price;
    }

    public int getCategoryId() {

        return categoryId;
    }

    public void setCategoryId(int categoryId) {

        this.categoryId = categoryId;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getSubCategory() {

        return subCategory;
    }

    public void setSubCategory(String subCategory) {

        this.subCategory = subCategory;
    }

    public int getStock() {

        return stock;
    }

    public void setStock(int stock) {

        this.stock = stock;
    }

    public boolean isFeatured() {

        return isFeatured;
    }

    public void setFeatured(boolean featured) {

        isFeatured = featured;
    }

    public String getImageUrl() {

        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {

        this.imageUrl = imageUrl;
    }
}
