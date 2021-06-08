package com.doubledigit.solutions.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "permission")
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(targetEntity = PermissionSets.class, mappedBy = "id",
            orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<PermissionSets> permissionSets;
}
