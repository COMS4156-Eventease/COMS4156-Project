package com.eventease.eventease_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    private Boolean accessibilityMode;
    private Boolean rsvpNotifications;
    private Boolean smsEnabled;
    private Boolean emailEnabled;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }


    public enum Role {
        ELDERLY, CAREGIVER, ORGANIZER, PLANNER
    }
}
