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

    // returns the category id
    public int getCategoryId()

    {
        return categoryId;
    }
    // sets category id
    public void setCategoryId(int categoryId)

    {

        this.categoryId = categoryId;
    }
    // returns category name
    public String getName()

    {

        return name;
    }
    // sets category name or updates it
    public void setName(String name)

    {

        this.name = name;
    }
    // returns the category description
    public String getDescription()

    {

        return description;
    }
    // updates or sets the cat description
    public void setDescription(String description)

    {

        this.description = description;
    }
}
