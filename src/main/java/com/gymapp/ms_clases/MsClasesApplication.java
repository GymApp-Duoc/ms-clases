package com.gymapp.ms_clases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MsClasesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsClasesApplication.class, args);
	}

}