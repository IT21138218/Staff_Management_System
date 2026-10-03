package com.sliit.sms.controller;

import com.sliit.sms.dto.ChangePasswordForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.User;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.repository.UserRepository;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minor functions: "Change password and update profile details" -
 * viewing your own profile and changing your own password. Login/logout
 * itself is handled by AuthController + the client discarding its JWT.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final EmployeeService employeeService;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Map<String, Object> view() {
        User user = securityUtil.getCurrentUser();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", user);
        response.put("employee", safeFindEmployee(user.getUsername()));
        return response;
    }

    @PostMapping("/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordForm form) {
        User user = securityUtil.getCurrentUser();

        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            throw new BusinessRuleException("Current password is incorrect.");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BusinessRuleException("New password and confirmation do not match.");
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }

    @PostMapping(value = "/photo", consumes = "multipart/form-data")
    public Employee uploadPhoto(@RequestParam("file") MultipartFile file) {
        User user = securityUtil.getCurrentUser();
        Employee employee = employeeService.findByUsername(user.getUsername());
        return employeeService.updatePhoto(employee.getId(), file);
    }

    private Employee safeFindEmployee(String username) {
        try {
            return employeeService.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
}
