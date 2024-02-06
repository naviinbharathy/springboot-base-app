package com.avega.portfolio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseEntity {

	@Column(name = "user_created")
	private String userCreated;

	@Column(name = "user_modified")
	private String userModified;

	@Column(name = "date_created")
	private LocalDateTime dateCreated;

	@Column(name = "date_modified")
	private LocalDateTime dateModified;

}
