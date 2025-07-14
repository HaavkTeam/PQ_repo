package org.pqproject.backend.service;

import org.pqproject.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.pqproject.backend.mapper.userMapper;

@Service
public class loginServiceImpl implements loginService {

    @Autowired
    private userMapper userMapper;

    /**
     * Validates user credentials.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return true if the credentials are valid, false otherwise.
     */
    public boolean validateUser(String email, String password) {
        User user = userMapper.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    /**
     * Registers a new user.
     *
     * @param user The user to register.
     * @return true if the registration is successful, false if the email already exists.
     */
    public boolean registerUser(User user) {
        if (userMapper.getUserByEmail(user.getEmail()) == null) {
            userMapper.insertUser(user);
            return true;
        }
        return false;
    }
}
