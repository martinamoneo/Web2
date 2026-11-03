package com.example.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("El chequeo de contexto completo de Spring Boot se realiza en las pruebas de integración (*IT) con Testcontainers")
class DemoApplicationTests {

	static {
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
	}

	@Test
	void contextLoads() {
	}

}
