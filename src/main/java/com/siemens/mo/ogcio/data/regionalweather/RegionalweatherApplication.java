package com.siemens.mo.ogcio.data.regionalweather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RegionalweatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegionalweatherApplication.class, args);
	}

}
