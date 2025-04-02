package com.musinsa.core;

import com.musinsa.core.config.JpaConfig;
import com.musinsa.core.config.QueryDslConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({QueryDslConfig.class, JpaConfig.class})
public class CoreTestConfiguration {


}
