package com.wootech.transtalk.dto.chatroom;

import com.wootech.transtalk.dto.chatroom.ChatRoomResponse;
import java.util.List;

public record ChatRoomListResponse(List<ChatRoomResponse> rooms,long totalElements) {
}
