package com.apisak.userservice.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * โปรไฟล์ของผู้ใช้
 */
@Entity
@Table(name = "profiles")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("unused")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private String displayName;
    private String bio;
    private String avatarUrl;

    public Profile(Long userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
}