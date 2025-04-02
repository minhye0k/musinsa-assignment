package com.musinsa.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.musinsa.core"})
@EntityScan(basePackages = {"com.musinsa.core"})
@EnableJpaAuditing
public class JpaConfig {
}
