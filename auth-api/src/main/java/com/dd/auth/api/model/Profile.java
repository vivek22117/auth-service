package com.dd.auth.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NamedQuery(name = "Profile.findByFirstName", query = "FROM Profile WHERE name = ?1")
@Table(name = "profile")
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "address")
    private String address;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "birthday", columnDefinition = "DATE")
    private LocalDate bday;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "profile")
    private Login login;

    @OneToMany(targetEntity = PermissionSets.class,
            mappedBy = "id",  orphanRemoval = false,
            fetch = FetchType.LAZY)
    private Set<PermissionSets> permissionSets;
}
