package com.avega.portfolio.user.model;

import org.hibernate.annotations.UuidGenerator;

import com.avega.portfolio.enums.ERole;
import com.avega.portfolio.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

	@Id
	@UuidGenerator
	@Column(name = "role_id")
	private String roleId;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;
}
