package com.dnd.global.error.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "Sample Error Message"),

	// Common
	METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "요청 한 값 타입이 잘못되어 binding에 실패하였습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP method 입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요"),

	// Auth
	KAKAO_RESPONSE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "카카오에 대한 응답값이 존재하지 않습니다."),
	CLAIMS_IS_NULL(HttpStatus.UNAUTHORIZED, "Claim이 null 값입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "리프레시 토큰이 만료되었습니다."),

	// Image
	IMAGE_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지 키를 찾을 수 없습니다."),
	IMAGE_FILE_EXTENSION_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지 파일 형식을 찾을 수 없습니다."),

	// Member
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

	// Event
	EVENT_UPLOAD_STATUS_IS_NOT_PENDING(HttpStatus.BAD_REQUEST, "축제 이미지 업로드 상태가 PENDING이 아닙니다."),
	EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 축제입니다."),
	EVENT_USER_MISMATCH(HttpStatus.BAD_REQUEST, "축제를 생성한 유저와 로그인한 계정이 일치하지 않습니다."),

	EVENT_UPLOAD_STATUS_IS_NOT_NONE(HttpStatus.BAD_REQUEST, "축제 이미지 업로드 상태가 NONE이 아닙니다."),

	// Bookmark
	BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 북마크입니다."),
	BOOKMARK_USER_MISMATCH(HttpStatus.BAD_REQUEST, "북마크를 생성한 유저와 로그인한 계정이 일치하지 않습니다."),

	;
	private final HttpStatus status;
	private final String message;
}
