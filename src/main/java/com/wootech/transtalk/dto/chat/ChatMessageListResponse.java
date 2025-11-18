package com.wootech.transtalk.dto.chat;

import java.util.List;
import org.springframework.data.domain.Page;

public record ChatMessageListResponse(List<ChatMessageResponse> chats,
                                      int pageNumber,
                                      int pageSize,
                                      boolean hasNext,
                                      boolean isLast,
                                      long totalElements,
                                      RecipientInfoRequest recipient) {
    public static ChatMessageListResponse from(Page<ChatMessageResponse> pages,RecipientInfoRequest recipient) {
        return new ChatMessageListResponse(
                pages.getContent(),
                pages.getNumber(),
                pages.getSize(),
                pages.hasNext(),
                pages.isLast(),
                pages.getTotalElements(),
                recipient
        );
    }

}
