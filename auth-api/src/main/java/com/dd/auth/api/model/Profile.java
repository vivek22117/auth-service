package com.dd.auth.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NamedQuery(name = "Profile.findByFirstName", query = "FROM Profile WHERE name = ?1")
@Table(name = "profile")
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long profileId;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "address")
    private String address;

    @Column(name = "email", unique = true, nullable = false)
    @Email
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "created")
    private Instant created;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "profile")
    private Login login;

    @OneToMany(targetEntity = PermissionSets.class,
            mappedBy = "id",  orphanRemoval = false,
            fetch = FetchType.LAZY)
    private Set<PermissionSets> permissionSets;
}