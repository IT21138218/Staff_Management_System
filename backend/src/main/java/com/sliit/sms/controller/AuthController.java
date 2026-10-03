package com.sliit.sms.controller;

import com.sliit.sms.config.JwtUtil;
import com.sliit.sms.dto.LoginForm;
import com.sliit.sms.dto.RegisterForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.EmployeeStatus;
import com.sliit.sms.entity.Role;
import com.sliit.sms.entity.User;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.repository.EmployeeRepository;
import com.sliit.sms.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * FR1 - User Authentication & Authorisation.
 * POST /api/auth/login exchanges username/password for a JWT; POST /api/auth/register
 * self-signs-up a new EMPLOYEE account. Both return the same shape, which the
 * React app attaches as "Authorization: Bearer <token>" on every subsequent call.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginForm form) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        User user = userRepository.findByUsername(form.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        return buildAuthResponse(user);
    }

    /**
     * Self-service signup. Always creates a plain EMPLOYEE (role is never taken
     * from the client) with a minimal linked Employee record - employeeCode
     * auto-generated, no department/salary yet - so every "my ..." page works
     * immediately. HR completes onboarding (department, salary, designation)
     * later from the Employees screen.
     */
    @PostMapping("/register")
    @Transactional
    public Map<String, Object> register(@Valid @RequestBody RegisterForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessRuleException("Password and confirmation do not match.");
        }
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new BusinessRuleException("Username already taken: " + form.getUsername());
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new BusinessRuleException("An account with this email already exists.");
        }

        User user = userRepository.save(User.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .email(form.getEmail())
                .role(Role.EMPLOYEE)
                .enabled(true)
                .build());

        Employee employee = Employee.builder()
                .employeeCode(generateEmployeeCode())
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .email(form.getEmail())
                .dateJoined(LocalDate.now())
                .designation("Unassigned")
                .basicSalary(0.0)
                .status(EmployeeStatus.ACTIVE)
                .user(user)
                .build();
        employeeRepository.save(employee);

        return buildAuthResponse(user);
    }

    private Map<String, Object> buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        Employee employee = employeeRepository.findByUser_Username(user.getUsername()).orElse(null);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("employeeId", employee != null ? employee.getId() : null);
        response.put("fullName", employee != null ? employee.getFullName() : user.getUsername());
        response.put("photoUrl", employee != null ? employee.getPhotoUrl() : null);
        return response;
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count() + 1;
        String candidate = String.format("EMP%04d", count);
        while (employeeRepository.findByEmployeeCode(candidate).isPresent()) {
            count++;
            candidate = String.format("EMP%04d", count);
        }
        return candidate;
    }
}
