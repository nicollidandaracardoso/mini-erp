package com.nicolli.mini_erp;

import org.springframework.boot.SpringApplication;

public class TestMiniErpApplication {

	public static void main(String[] args) {
		SpringApplication.from(MiniErpApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
