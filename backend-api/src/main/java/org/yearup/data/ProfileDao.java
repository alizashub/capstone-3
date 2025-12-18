package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    //  creates a new profile when a user registers
    Profile create(Profile profile);

    // retrieves the profile for a specific user
    Profile getByUserId(int userId);

    // updates an exisiting profile
    boolean update(Profile profile);

}
