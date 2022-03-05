package com.dragonslair.bcintredux.config;


import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RollbarConfig {

    @Bean
    public Rollbar rollbar(@Value("${rollbar.key}") String key) {
        return new Rollbar(getRollbarConfigs(key));
    }

    private Config getRollbarConfigs(String accessToken) {
        // Reference ConfigBuilder.java for all the properties you can set for Rollbar
        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .build();
    }
}
