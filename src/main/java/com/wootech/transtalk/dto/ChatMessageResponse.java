package com.wootech.transtalk.dto;

import java.time.Instant;
import com.wootech.transtalk.enums.TranslationStatus;

public record ChatMessageResponse(Long chatId, String originalMessage, String translatedMessage,
                                  String senderEmail, Instant sendAt, int unReadCount, TranslationStatus status) {
}
