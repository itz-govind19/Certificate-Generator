package com.demo.certificate.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CertificateGeneratorApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(CertificateGeneratorApplication.class, args);
		System.out.println("********************************🚀Certificate Generator Application Started Successfully!🚀********************************");
	}


}
