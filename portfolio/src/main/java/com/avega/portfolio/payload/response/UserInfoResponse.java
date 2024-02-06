package com.avega.portfolio.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {

	private String id;
	private String username;
	private String email;
	private List<String> roles;

}
