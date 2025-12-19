package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController {
    private final ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @GetMapping
    public List<Product> search(
            // maps directly from the URL -- becomes null is not provided
            @RequestParam(name = "cat", required = false) Integer categoryId,
            // Reads ?minPrice= from the URL
            // If not provided, minPrice will be null
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice, @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            // Reads ?subCategory= from the URL
            // If not provided, subCategory will be null
            @RequestParam(name = "subCategory", required = false) String subCategory) {
        return productDao.search(categoryId, minPrice, maxPrice, subCategory);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable int id) {
        // this will return a product if it exists and be null if no product with id exists
        Product product = productDao.getById(id);

        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
        }
        return product;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Product addProduct(@RequestBody Product product) //  reads the JSON body and converts it into a product obj
    {
        // return a fully populated product obj
        return productDao.create(product);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(@PathVariable int id, @RequestBody Product product) // reads the JSON body and converts it into a product obj
    {
        // checks to see if body productid matches url productid
        if (product.getProductId() != 0 && product.getProductId() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ProductID in URL does not match productID in the body.");
        }

        boolean updated = productDao.update(id, product);

        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
        }
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable int id) {

        boolean deleted = productDao.delete(id);

        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
        }
    }
}
