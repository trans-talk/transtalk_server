package com.wootech.transtalk.dto;

import java.time.Instant;

public record ChatMessageResponse(Long chatId, String originalMessage, String translatedMessage,
                                  String senderEmail, Instant sendAt, int unReadCount) {
}
