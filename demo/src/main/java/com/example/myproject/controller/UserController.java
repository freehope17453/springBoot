package com.example.myproject.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.myproject.domain.User;
import com.example.myproject.domain.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
    
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping("/list")
    public String  listUser(Model model) {
		 List<User> userList = userRepository.findAll();
		 model.addAttribute("users", userList);
        return "/userlist";
    }
}
