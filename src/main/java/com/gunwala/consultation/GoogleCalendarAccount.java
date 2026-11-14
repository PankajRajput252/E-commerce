package com.gunwala.consultation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * The Google account that hosts consultations (single row, id = 1). Connected once by an admin
 * from the admin panel; its refresh token lets the backend create Calendar events with Meet links.
 */
@Entity
@Table(name = "consultation_google_account")
@Getter
@Setter
@NoArgsConstructor
public class GoogleCalendarAccount {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id = SINGLETON_ID;

    @Column(nullable = false, length = 512)
    private String refreshToken;

    @Column(length = 200)
    private String accountEmail;

    @Column(nullable = false)
    private Instant connectedAt;
}
