package com.gitquality.git_quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // 🟢 Ajouté

@SpringBootApplication
@EnableAsync // 🟢 Activation de la programmation asynchrone
public class GitQualityApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitQualityApplication.class, args);
	}

}
