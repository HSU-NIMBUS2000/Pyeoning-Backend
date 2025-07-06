package com.hsu.pyeoning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PyeoningApplication {

	public static void main(String[] args) {
		SpringApplication.run(PyeoningApplication.class, args);
	}

}
