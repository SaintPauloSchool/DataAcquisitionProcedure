package com.sp.dataacquisitionprocedure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataAcquisitionProcedureApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataAcquisitionProcedureApplication.class, args);
	}

}
