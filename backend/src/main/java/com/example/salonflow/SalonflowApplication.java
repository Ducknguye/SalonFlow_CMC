package com.example.salonflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class SalonflowApplication {

	public static void main(String[] args) {
		loadEnv();
		String nodeEnv = System.getenv("NODE_ENV");
		if (nodeEnv == null || nodeEnv.isBlank()) {
			nodeEnv = System.getProperty("NODE_ENV");
		}
		String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
		if (activeProfile == null || activeProfile.isBlank()) {
			activeProfile = System.getProperty("SPRING_PROFILES_ACTIVE");
		}
		System.out.println("NODE_ENV=" + nodeEnv);
		System.out.println("SPRING_PROFILES_ACTIVE=" + activeProfile);
		SpringApplication.run(SalonflowApplication.class, args);
	}

	private static void loadEnv() {
		loadEnvFile(Paths.get(".env"));
		String profile = System.getenv("SPRING_PROFILES_ACTIVE");
		if (profile != null) {
			profile = profile.trim();
		}
		if (profile != null && !profile.isBlank()) {
			loadEnvFile(Paths.get(".env." + profile));
		}
	}

	private static void loadEnvFile(Path path) {
		if (!Files.exists(path)) {
			return;
		}
		try {
			List<String> lines = Files.readAllLines(path);
			for (String line : lines) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				int separator = line.indexOf('=');
				if (separator > 0) {
					String key = line.substring(0, separator).trim();
					String value = line.substring(separator + 1).trim();
					if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
						value = value.substring(1, value.length() - 1);
					}
					// Do not overwrite real environment variables (OS-level),
					// but allow later-loaded files (profile) to override earlier ones.
					if (System.getenv(key) == null) {
						System.setProperty(key, value);
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Could not load env file: " + path + " -> " + e.getMessage());
		}
	}

}
