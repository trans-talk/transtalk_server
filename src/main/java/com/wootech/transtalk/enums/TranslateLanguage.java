package com.wootech.transtalk.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.wootech.transtalk.exception.custom.BadRequestException;
import org.springframework.http.HttpStatusCode;

import java.util.Arrays;

import static com.wootech.transtalk.exception.ErrorMessages.NOT_SUPPORTED_LANGUAGE_CODE_ERROR;

public enum TranslateLanguage {
    KOREAN("ko"),
    SPANISH("es"),
    JAPANESE("ja"),
    ENGLISH("en-us"),
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
