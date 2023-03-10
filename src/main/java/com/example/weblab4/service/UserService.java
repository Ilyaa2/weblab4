package com.example.weblab4.service;


import com.example.weblab4.model.User;

import java.util.List;

public interface UserService {

    User register(User user);

    User findByUsername(String username);

    User findById(Long id);

    void delete(Long id);
}
