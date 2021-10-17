package com.test.noughtcrosses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class NoughtCrossesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoughtCrossesApplication.class, args);
    }

}
