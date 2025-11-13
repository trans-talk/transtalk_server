package com.wootech.transtalk.enums;

import static com.wootech.transtalk.exception.ErrorMessages.NOT_SUPPORTED_LANGUAGE_CODE_ERROR;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wootech.transtalk.exception.custom.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.Arrays;

public enum TranslateLanguage {
    KOREAN("ko"),
    SPANISH("es"),
    JAPANESE("ja"),
    ENGLISH("en"),
    CHINESE("zh");
    private final String code;

    TranslateLanguage(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public static TranslateLanguage from(String code) {
        return Arrays.stream(values())
                .filter(lang -> lang.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(NOT_SUPPORTED_LANGUAGE_CODE_ERROR, HttpStatusCode.valueOf(400)));
    }
}
