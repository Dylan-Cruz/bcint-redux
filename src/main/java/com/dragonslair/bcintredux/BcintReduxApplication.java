package com.dragonslair.bcintredux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BcintReduxApplication {

	public static void main(String[] args) {
		SpringApplication.run(BcintReduxApplication.class, args);
	}

}
