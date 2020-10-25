package com.weweibuy.bpms;

import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author durenhao
 * @date 2020/10/25 11:22
 **/
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, FlowableSecurityAutoConfiguration.class})
@EnableFeignClients
public class BpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpmsApplication.class, args);
    }

}
