package com.weweibuy.bpms;

import com.weweibuy.framework.rocketmq.annotation.EnableRocketProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author durenhao
 * @date 2020/10/25 11:22
 **/
//@EnableRocketProducer
@SpringBootApplication
@EnableFeignClients
public class BpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpmsApplication.class, args);
    }

}
