package com.sliit.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * FR7 - Shift & Roster Scheduling: a reusable shift template (e.g. Morning 08:00-16:00).
 */
@Entity
@Table(name = "shifts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g. "Morning", "Evening", "Night"

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;
}
