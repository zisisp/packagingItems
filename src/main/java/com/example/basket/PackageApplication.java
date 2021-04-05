package com.example.basket;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PackageApplication implements ApplicationRunner
{
	private final PackageService packageService;

	public PackageApplication(PackageService packageService) {
		this.packageService = packageService;
	}


	public static void main(String[] args) {
		SpringApplication.run(PackageApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		packageService.printPackageResults(args);
	}
}
