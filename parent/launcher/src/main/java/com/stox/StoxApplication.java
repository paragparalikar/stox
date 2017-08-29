package com.stox;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.stox.core.util.spring.DateFormatPropertyEditor;

import javafx.application.Application;
import javafx.stage.Stage;

@EnableCaching
@EnableScheduling
@EnableAsync(proxyTargetClass=true)
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
	public CustomEditorConfigurer customEditorConfigurer() {
		final Map<Class<?>, Class<? extends PropertyEditor>> editors = new HashMap<>();
		editors.put(DateFormat.class, DateFormatPropertyEditor.class);
		editors.put(SimpleDateFormat.class, DateFormatPropertyEditor.class);
		final CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
		customEditorConfigurer.setCustomEditors(editors);
		return customEditorConfigurer;
	}

	@Bean
	@Primary
	public ThreadPoolTaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setAwaitTerminationSeconds(5);
		taskExecutor.setCorePoolSize(3);
		return taskExecutor;
	}

	@Bean
	public ScheduledThreadPoolExecutor scheduledTaskExecutor() {
		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		return executor;
	}

}
