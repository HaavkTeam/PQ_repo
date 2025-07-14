package org.pqproject.backend.service;

import org.pqproject.backend.pojo.User;

public interface loginService {
    public boolean validateUser(String email, String password);

    public boolean registerUser(User user);
}
