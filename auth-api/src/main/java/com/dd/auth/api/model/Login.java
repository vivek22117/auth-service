package com.dd.auth.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "login")
@AllArgsConstructor
@NoArgsConstructor
public class Login {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "passphrase")
    private String passphrase;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
