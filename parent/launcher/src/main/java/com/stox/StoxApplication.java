package com.stox;

import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.stox.core.util.Constant;

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
	@Primary
	public DataSource dataSource() {
		final String username = "paragparalikar";
		final String password = "admin";
		final String url = "jdbc:hsqldb:file:" + Constant.PATH + "database" + File.separator + "hsqldb";
		return DataSourceBuilder.create().url(url).username(username).password(password).build();
	}

}
