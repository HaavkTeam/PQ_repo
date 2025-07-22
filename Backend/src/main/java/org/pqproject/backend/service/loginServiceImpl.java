package org.pqproject.backend.service;

import org.pqproject.backend.pojo.Returnlogin;
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
    public Returnlogin validateUser(String email, String password, int role) {
        User user = userMapper.getUserByEmail(email);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                if (user.getRole() != role) {
                    Returnlogin returnLogin = new Returnlogin();
                    returnLogin.setUser(user);
                    returnLogin.setIsOK(0); // Token can be used for session management
                    returnLogin.setMessage("角色不匹配");
                    return returnLogin; // 返回角色不匹配的消息
                }
                Returnlogin returnLogin = new Returnlogin();
                returnLogin.setUser(user);
                returnLogin.setIsOK(1); // Token can be used for session management
                returnLogin.setMessage("登录成功");
                return returnLogin; // 返回登录成功的用户信息
            } else {
                Returnlogin returnLogin = new Returnlogin();
                returnLogin.setUser(user);
                returnLogin.setIsOK(0); // Token can be used for session management
                returnLogin.setMessage("密码错误");
                return returnLogin; // 返回密码错误的消息
            }
        }
        else {
            Returnlogin returnLogin = new Returnlogin();
            returnLogin.setIsOK(0); // Token can be used for session management
            returnLogin.setMessage("用户不存在");
            return returnLogin; // 返回用户不存在的消息
        }
    }

    /**
     * Registers a new user.
     *
     * @param user The user to register.
     * @return true if the registration is successful, false if the email already exists.
     */
    public boolean registerUser(User user) {
        if (userMapper.getUserByEmail(user.getEmail()) == null) {
            // Generate a unique user ID
            String userId = getUppercaseLetterAndNumber(8); // Generate a random 8-character ID
            user.setUserId(userId); // Set the generated user ID
            userMapper.insertUser(user);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object if found, null otherwise.
     */
    public User getUserById(String userId) {
        return userMapper.getUserById(userId);
    } // Return the user with the specified ID

    public  String getUppercaseLetterAndNumber(Integer length) {
        String uid = "";
        for (Integer i = 0; i < length; i++) {
            int index = (int) Math.round(Math.random() * 1);
            String randChar = "";
            switch (index) {
                case 0:
                    //大写字符
                    randChar = String.valueOf((char) Math.round(Math.random() * 25 + 97));
                    break;
                default:
                    //数字
                    randChar = String.valueOf(Math.round(Math.random() * 9));
                    break;
            }
            uid = uid.concat(randChar);
        }
        return uid;
    }
}
