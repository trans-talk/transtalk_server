package com.wootech.transtalk.dto;

import com.wootech.transtalk.enums.TranslationStatus;

public record ChatMessageResponse(String originalMessage, TranslationStatus status, String translatedMessage, String senderEmail) {
}
