package org.yearup.data;

import org.yearup.models.Category;

import java.util.List;

public interface CategoryDao
{
    // reteives all the cat from the database
    List<Category> getAllCategories();
    // retuns category by its cat id
    Category getByCategoryID(int categoryId);
    // creates a new category in the database
    Category create(Category category);
    // updates an existing category by cat id
    boolean update(int categoryId, Category category);
    // deletes category by ID
    boolean delete(int categoryId);
}
