package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {


    private final ProfileDao profileDao;
    // used to translate username to userid
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public Profile getProfile(Principal principal) {

        // get the authenticated user
        User user = getAuthenticatedUser(principal);

        // get user's profile using userid
        Profile profile = profileDao.getByUserId(user.getId());

        // if profile does not exist return 404 error
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profilenot found");
        }

        // return profile -- as JSON
        return profile;

    }

    @PutMapping
    public void updateProfile(@RequestBody Profile profile, Principal principal) {
        // get the authenticated user
        User user = getAuthenticatedUser(principal);

        // mapos userid on the profile obj
        profile.setUserId(user.getId());

        boolean updated = profileDao.update(profile);

        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }

    }

    private User getAuthenticatedUser(Principal principal) {

        // if principal is null spring did not attach a logged in user
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        // get the currently logged in username - from the login token
        String userName = principal.getName();

        System.out.println("JWT username = [" + userName + "]");

        // find database user that matches username - to get userid
        User user = userDao.getByUserName(userName);

        // if no user record exists, return a 404
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        // return the fully loaded user object - this is confirmation that the user is verified and in the database
        return user;
    }


}
