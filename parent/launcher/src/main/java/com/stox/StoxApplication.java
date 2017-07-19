package com.stox;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoxApplication extends Application {

	public static void main(String[] args) {
		Application.launch(StoxApplication.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		SpringApplication.run(StoxApplication.class, new String[] {});
	}

}
