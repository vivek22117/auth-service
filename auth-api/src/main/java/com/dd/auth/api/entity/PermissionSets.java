package com.dd.auth.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "permissionsets")
@AllArgsConstructor
@NoArgsConstructor
public class PermissionSets {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long permission_id;

    private Long id;

    @Column(name = "login_id")
    private Long loginId;

    @Column(name = "perm_id")
    private Long permId;

    @Column(name = "role_id")
    private Long roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Login login;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perm_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Role role;

}
