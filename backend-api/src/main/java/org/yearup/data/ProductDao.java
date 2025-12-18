package org.yearup.data;

import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao {
    List<Product> search(
            // used Integer vs int -- Integer can be null
            // allows to search with whatever filters the user provides -- ignores if not all parameters met
            // returns a list cause search can match many products
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String subCategory);

    // catId REQUIRED this int not Integer
    // gets all products that belong to category
    List<Product> listByCategoryId(int categoryId);

    // gets 1 product based on productId
    // returns null if not found
    Product getById(int productId);

    Product create(Product product);

    // return boolean to indicate success
    boolean update(int productId, Product product);

    // return boolean to indicate success
    boolean delete(int productId);
}
