package com.inswave.whive.common.enums;

public enum MessageString {
    REQUIRED_USER_NAME("사용자 이름을 입력해 주세요"),
    REQUIRED_USER_ID("아이디를 입력해 주세요"),
    REQUIRED_USER_INITIAL_PASSWORD("초기 비밀번호를 입력해 주세요"),
    REQUIRED_USER_PASSWORD("비밀번호를 입력해주세요"),
    REQUIRED_USER_SECSSION("해당 계정은 탈퇴한 계정입니다."),
    REQUIRED_NEW_USER_PASSWORD("비밀번호를 입력해주세요"),
    REQUIRED_USER_ID_OR_PASSWORD("아이디나 비밀번호가 틀렸습니다."),
    REQUIRED_USER_OLD_PASSWORD("예전 비밀번호가 틀렸습니다."),
    USER_CONFLICT("이미 존재하는 ID 입니다.<br>다시 시도해 주세요."),
    USER_NOT_FOUND("ID 존재하지 않습니다.<br>다시 시도해 주세요."),
    SAME_ID_ERROR("같은 아이디가 존재합니다.<br>다른 아이디를 입력해 주세요."),
    ADMIN_USER_SAVE_ERROR("저장에러.<br>다시 시도해 주세요."),
    ADMIN_USER_DELETE_ERROR("삭제에러.<br>다시 시도해 주세요."),
    PASSWORD_IS_SAME("기존 비밀번호와 새 비밀번호가 일치합니다.<br>다시 시도해 주세요."),
    PASSWORD_VALIDATION_ERROR("기존 비밀번호가 맞지 않습니다.<br>다시 시도해 주세요."),
    PASSWORD_CHANGE_SUCCESS("비밀번호가 변경되었습니다.<br>다시 로그인 해주세요."),
    REQUIRED_BUILD_PROJECT_NAME("프로젝트이름을 입력해 주세요"),
    LICENSE_DOES_NOT_MATCH_PLATFORM("Platform does not match to license file. please reissue license."),
    LICENSE_DOES_NOT_MATCH_ANDROID_APPID("Real Android AppID does not match to license file. please reissue license."),
    LICENSE_DOES_NOT_MATCH_iOS_APPID("Real iOS AppID does not match to license file. please reissue license."),
    LICENSE_INVALID_TO_HIVE("Invalid hive license."),
    APP_ID_INVALID_TO_HIVE("등록되지 않은 APP ID 입니다. 사용자 정보에서 APP ID를 등록하신 후, 이용하실 수 있습니다."),
    BUILDER_QUEUE_IS_FULL("Builder 사용량이 초과 되었습니다. Builder 상태를 확인하시고 다시 수행해 주시기 바랍니다.");






    private final String message;

    MessageString(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
