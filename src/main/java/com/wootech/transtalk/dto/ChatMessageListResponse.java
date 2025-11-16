package com.wootech.transtalk.dto;

import com.wootech.transtalk.dto.chatroom.ChatRoomListResponse;
import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public record ChatMessageListResponse(List<ChatMessageResponse> chats,
                                      int pageNumber,
                                      int pageSize,
                                      boolean hasNext,
                                      boolean isLast,
                                      long totalElements) {
    public static ChatMessageListResponse from(Page<ChatMessageResponse> pages) {
        return new ChatMessageListResponse(
                pages.getContent(),
                pages.getNumber(),
                pages.getSize(),
                pages.hasNext(),
                pages.isLast(),
                pages.getTotalElements()
        );
    }

}
