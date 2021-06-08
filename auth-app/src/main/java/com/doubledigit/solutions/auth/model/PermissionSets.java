package com.doubledigit.solutions.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Data
@Entity
@Table(name = "permission_sets")
@AllArgsConstructor
@NoArgsConstructor
public class PermissionSets {

    @Id
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
