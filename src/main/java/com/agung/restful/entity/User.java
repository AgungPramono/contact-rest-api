package com.agung.restful.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;
    private String password;
    private String name;

    private String token;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_expired_at")
    private Long tokenExpiredAt;

    @Column(name = "refresh_token_expired_at")
    private Long refreshTokenExpiredAt;

    @OneToMany(mappedBy = "user")
    private List<Contact> contacts;
}
