package com.wootech.transtalk.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.wootech.transtalk.entity.MongoChat;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.database}")
    private String database;


    @Bean
    @NonNull
    @Override
    public MongoClient mongoClient() {
        return super.mongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(uri));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupMongoIndexes() {
        IndexOperations indexOps = mongoTemplate().indexOps(MongoChat.class);
        // TTL: 365일
        Duration ttlDuration = Duration.ofDays(365);
        // createdAt 기준
        IndexDefinition ttlIndex = new Index().on("createdAt", Sort.Direction.ASC)
                .expire(ttlDuration);
        try {
            indexOps.createIndex(ttlIndex);
            log.info("[MongoConfig] MongoDB TTL index for ChatMessageDocument.createdAt created successfully.");
        } catch (Exception e) {
            log.error("[MongoConfig] Error creating MongoDB TTL index: " + e.getMessage());
        }
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(uri))
                .writeConcern(WriteConcern.MAJORITY)
                .applyToSocketSettings(socketSetting -> socketSetting
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES)
                )
                .applyToConnectionPoolSettings(pool -> pool
                        .maxSize(100)
                        .maxSize(5)
                        .maxConnectionIdleTime(10, TimeUnit.SECONDS)
                )
                .applyToSslSettings(ssl -> ssl.enabled(true))
                .retryWrites(true)
                .timeout(1, TimeUnit.MINUTES)
                .build();
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

}
