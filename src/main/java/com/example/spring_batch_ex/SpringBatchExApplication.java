package com.example.spring_batch_ex;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@SpringBootApplication
@EnableScheduling
public class SpringBatchExApplication {

    public static void main(String[] args) {


        SpringApplication.run(SpringBatchExApplication.class, args);

    }

}
