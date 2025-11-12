package com.wootech.transtalk.dto.chatroom;

import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public record ChatRoomListResponse(List<ChatRoomResponse> rooms,
                                   int pageNumber,
                                   int pageSize,
                                   boolean hasNext,
                                   boolean isLast,
                                   long totalElements) {
    public static ChatRoomListResponse from(Page<ChatRoomResponse> pages) {
        return new ChatRoomListResponse(
                pages.getContent(),
                pages.getNumber(),
                pages.getSize(),
                pages.hasNext(),
                pages.isLast(),
                pages.getTotalElements()
        );
    }
}
