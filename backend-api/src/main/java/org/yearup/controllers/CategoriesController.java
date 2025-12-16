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

@RestController
@RequestMapping("categories")
// this will set this as the baseURL for every method in this controller
// http://localhost:8080/categories
@CrossOrigin
// add annotation to allow cross site origin requests
public class CategoriesController {
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    // constructor injection
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {

        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }
    // add the appropriate annotation for a get action


    @GetMapping
    public List<Category> getAll() {
        // find and return all categories
        return categoryDao.getAllCategories();
    }


    // add the appropriate annotation for a get action
    @GetMapping("/{id}")
    public Category getByCategoryId(@PathVariable int id) {
        // get the category by id
        Category category = categoryDao.getByCategoryID(id);

        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category" + id + "not found.");
        }
        return category;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
     @GetMapping("{categoryId}/products")
        public List<Product> getProductsById(@PathVariable int categoryId) {
        return productDao.listByCategoryId(categoryId);
     }
//    }

    // add annotation to call this method for a POST action
    @PostMapping
    @ResponseStatus (value =  HttpStatus.CREATED)
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        // insert the category
        return categoryDao.create(category);
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("/{id}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {

        if (id != category.getCategoryId()) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "The categoryID is different from categoryID in the body");
        }
        // update the category by id
        categoryDao.update(id, category);
    }

    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable int id) {
        // delete the category by id
        categoryDao.delete(id);
    }
}
