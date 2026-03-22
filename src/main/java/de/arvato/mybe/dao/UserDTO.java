package de.arvato.mybe.dao;

import lombok.Data;

@Data
public class UserDTO {
	private long id;
	private String firstName;
	private String lastName;
	private String email;
	private String racf;
	private String oid;

}
