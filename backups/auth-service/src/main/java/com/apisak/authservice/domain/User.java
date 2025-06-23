package com.apisak.authservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue private UUID id;
    @Column(unique=true, nullable=false) private String username;
    private String passwordHash;
    @Column(unique=true) private String email;
    private boolean enabled = false;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();
}