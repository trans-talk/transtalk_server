package com.wootech.transtalk.dto.chat.mongo;

import com.wootech.transtalk.dto.chat.RecipientInfoRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MongoChatMessageListResponse {
    private List<MongoChatMessageResponse> chats;
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean isLast;
    private long totalElements;
    private RecipientInfoRequest recipientInfoRequest;

    public static MongoChatMessageListResponse from(Page<MongoChatMessageResponse> pages, RecipientInfoRequest recipient) {
        return new MongoChatMessageListResponse(
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