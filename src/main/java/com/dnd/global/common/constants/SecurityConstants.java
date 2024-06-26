package com.dnd.global.common.constants;

public final class SecurityConstants {

	public static final String TOKEN_ROLE_NAME = "role";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String ACCESS_TOKEN_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
	public static final String REGISTER_REQUIRED_HEADER = "Registration-Required";
	public static final String KAKAO_ISSUER = "https://kauth.kakao.com";
	public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

	private SecurityConstants() {}
}