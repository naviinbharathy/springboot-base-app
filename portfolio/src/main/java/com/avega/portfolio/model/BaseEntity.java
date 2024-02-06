package com.avega.portfolio.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {

	@Column(name = "user_created")
	private String userCreated;

	@Column(name = "user_modified")
	private String userModified;

	@CreationTimestamp
	@Column(name = "date_created")
	private LocalDateTime dateCreated;

	@UpdateTimestamp
	@Column(name = "date_modified")
	private LocalDateTime dateModified;

}
