package org.pqproject.backend.controller;

import org.pqproject.backend.pojo.Returnlogin;
import org.pqproject.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.pqproject.backend.service.loginService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class loginController {
    @Autowired
    private loginService loginService;

    @RequestMapping("/login")
    public Returnlogin login(@RequestParam(name = "email") String email,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "role") int role) {
        return loginService.validateUser(email, password, role);
    }

    @RequestMapping("/register")
    public String register(@RequestBody User user) {
        if(loginService.registerUser(user)) {
            return "注册成功"; // Return a success message or redirect to a different page
        } else {
            return "注册失败，邮箱已存在"; // Return an error message or redirect to the registration page
        }
    }

    @RequestMapping("/test")
    public String test() {
        return "测试成功"; // Return a success message for testing purposes
    }
}
