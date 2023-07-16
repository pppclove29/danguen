package com.example.danguen.domain.user.entity;

public enum Role {
	USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), ANONYMOUS("ROLE_ANONYMOUS");

	private final String role;

	Role(String role) {
		this.role = role;
	}

	public String toString() {
		return this.role;
	}
}
