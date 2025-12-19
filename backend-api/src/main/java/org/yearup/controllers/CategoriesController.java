package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// tells spring that this class handles http requests and returns JSON
@RestController
@RequestMapping("categories")
// this will set this as the baseURL for every method in this controller
// every method path is relative to /categories
// http://localhost:8080/categories
@CrossOrigin
// add annotation to allow cross site origin requests ( frontend apps )
public class CategoriesController {
    // final so they never change after construction
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    // spring inject the DAO implementations
    // create an Autowired controller to inject the categoryDao and ProductDao
    // constructor injection
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {

        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    // handles get / categories - no path var and no request body
    @GetMapping
    public List<Category> getAll() {
        // find and return the categories list
        // the list is converted into JSON
        // HTTP status is set to 200
        return categoryDao.getAllCategories();
    }


    @GetMapping("/{id}")
    public Category getByCategoryId(@PathVariable int id) {
        // spring takes the id from the URL and converts it to data
        Category category = categoryDao.getByCategoryId(id);

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found.");
        }
        // cat is returned in JSON
        return category;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    // rest end point
    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        // Sspring takes the id from URL
        // looks for products for catId
        // returns list of products in JSON
        return productDao.listByCategoryId(categoryId);
    }


    @PostMapping
    // http status handling
    @ResponseStatus(HttpStatus.CREATED)
    // add annotation to ensure that only an ADMIN can call this function
    //spring security
    @PreAuthorize("hasRole('ADMIN')")
    // HTTP request contains body ( includes name and description )
    // spring reads the JSON body and creates a java obj
    public Category addCategory(@RequestBody Category category) {
        // inserts obj into DB, get gen id, gets row, returns full cat
        return categoryDao.create(category);
    }


    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("/{id}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    // HTTP request contains body ( includes cat id, name and description )
    // spring extracts the id
    // converts JSON into cat obj
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {

        // java check the ID from URL VS ID inside the request body
        if (id != category.getCategoryId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The categoryID in URL is different from categoryID in the body");
        }
        // call dao and store the result
        boolean updated = categoryDao.update(id, category);

        // if dao says nothing was updated, category does not exist
        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found.");
        }
    }

    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")

    public void deleteCategory(@PathVariable int id) {
        // call dao deleted method and store the result, if true cat is deleted, if false no cat existed
        boolean deleted = categoryDao.delete(id);

        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found.");
        }
    }
}
