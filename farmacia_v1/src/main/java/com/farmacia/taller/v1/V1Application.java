package com.farmacia.taller.v1;

import com.farmacia.taller.v1.model.Categoria;
import com.farmacia.taller.v1.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;

@SpringBootApplication
public class V1Application {

	public static void main(String[] args) {
		SpringApplication.run(V1Application.class, args);
	}

	@Bean
	CommandLineRunner initCategorias(CategoriaRepository categoriaRepository) {
		return args -> {
			if (categoriaRepository.count() == 0) {
				categoriaRepository.saveAll(List.of(
					new Categoria("Antibióticos", "Medicamentos antibacterianos"),
					new Categoria("Analgésicos", "Alivio del dolor e inflamación"),
					new Categoria("Equipo Médico", "Dispositivos e instrumental médico"),
					new Categoria("Cuidado Personal", "Higiene y primeros auxilios")
				));
			}
		};
	}
}
