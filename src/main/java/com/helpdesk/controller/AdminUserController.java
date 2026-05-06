package com.helpdesk.controller;

import com.helpdesk.service.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserAccountService userAccountService;

    public AdminUserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public String users(@RequestParam(required = false) String q, @RequestParam(required = false) String role, Model model) {
        model.addAttribute("users", userAccountService.searchUsers(q, role));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedRole", role == null ? "" : role);
        return "admin-users";
    }

    @PostMapping
    public String createUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        userAccountService.createUser(username, password, role);
        return "redirect:/admin/users";
    }
}
