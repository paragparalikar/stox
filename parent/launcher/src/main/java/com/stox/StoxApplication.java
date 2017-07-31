package com.stox;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableScheduling
@EnableAsync(mode = AdviceMode.ASPECTJ)
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class StoxApplication extends Application {

	public static void main(String[] args) {
		Application.launch(StoxApplication.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		SpringApplication.run(StoxApplication.class, new String[] {});
	}

	@Bean
	@Primary
	public ThreadPoolTaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setAwaitTerminationSeconds(5);
		taskExecutor.setDaemon(true);
		taskExecutor.setCorePoolSize(3);
		return taskExecutor;
	}

}
