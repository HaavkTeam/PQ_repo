package org.pqproject.backend.service;

import org.pqproject.backend.pojo.Returnlogin;
import org.pqproject.backend.pojo.User;

public interface loginService {
    public Returnlogin validateUser(String email, String password, int role);

    public boolean registerUser(User user);

    public User getUserById(String userId);
}
