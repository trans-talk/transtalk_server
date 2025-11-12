package com.wootech.transtalk.repository.chat;

import com.wootech.transtalk.entity.ChatContent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMongoRepository extends MongoRepository<ChatContent, String> {
}
