package com.sliit.sms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Login/security identity (FR1 - User Authentication & Authorisation).
 * Kept separate from Employee so that not every login has to be an employee
 * (e.g. could extend to external auditors later) and so password/auth concerns
 * are not mixed with HR data.
 */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password; // BCrypt-hashed, never stored/logged in plain text, never sent to the client

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    // Back-reference only; Employee.user already carries this relationship for
    // the client, so this side is excluded from JSON to avoid an infinite
    // User <-> Employee serialization loop.
    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Employee employee; // null for pure admin/system accounts with no HR record
}
