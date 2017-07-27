package com.stox;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableBatchProcessing
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
	public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager txManager) throws Exception {
		final MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public ResourcelessTransactionManager resourcelessTransactionManager() {
		return new ResourcelessTransactionManager();
	}

}
