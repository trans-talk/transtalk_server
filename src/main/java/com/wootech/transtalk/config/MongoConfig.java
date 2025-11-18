package com.wootech.transtalk.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
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
