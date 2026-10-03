package uk.org.spire.emissions_calculator_beta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // <--- Adicione esta anotação
@SpringBootApplication
public class EmissionsCalculatorBetaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmissionsCalculatorBetaApplication.class, args);
	}
}
