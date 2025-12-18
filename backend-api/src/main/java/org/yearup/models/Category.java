package org.yearup.models;

// this represents a product category in the store
public class Category
{
    // the pk in the database
    private int categoryId;
    // category name
    private String name;
    // category details
    private String description;

    // default constructor required for spring for obj creation
    public Category()
    {}

    // overloaded constructor used when creating a category with all the data from the database
    public Category(int categoryId, String name, String description)
    {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    public int getCategoryId()
            // returns the category id
    {
        return categoryId;
    }

    public void setCategoryId(int categoryId)
            // sets category id
    {

        this.categoryId = categoryId;
    }

    public String getName()
            // returns category name
    {

        return name;
    }

    public void setName(String name)
            // sets category name or updates it
    {

        this.name = name;
    }

    public String getDescription()
            // returns the category description
    {

        return description;
    }

    public void setDescription(String description)
            // updates or sets the cat description
    {

        this.description = description;
    }
}
