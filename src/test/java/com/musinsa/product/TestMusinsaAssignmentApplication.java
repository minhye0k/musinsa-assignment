package com.musinsa.product;

import org.springframework.boot.SpringApplication;

public class TestMusinsaAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.from(MusinsaAssignmentApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
