package com.stox.example.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.example.event.ExampleCreatedEvent;
import com.stox.example.event.ExampleDeletedEvent;
import com.stox.example.model.Example;
import com.stox.example.repository.ExampleRepository;

@Lazy
@Async
@Component
public class ExampleClientImpl implements ExampleClient {
	
	@Autowired
	private ExampleRepository exampleRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Override
	public void load(Integer exampleId, ResponseCallback<List<Example>> callback) {
		try {
			callback.onSuccess(new Response<>(exampleRepository.load(exampleId)));
		}catch(final Exception exception) {
			callback.onFailure(null, exception);
		}finally {
			callback.onDone();
		}
	}

	@Override
	public void save(Example example, ResponseCallback<Example> callback) {
		try {
			final Example managedExample = exampleRepository.save(example); 
			eventPublisher.publishEvent(new ExampleCreatedEvent(this, managedExample));
			callback.onSuccess(new Response<>(managedExample));
		}catch(final Exception exception) {
			callback.onFailure(null, exception);
		}finally {
			callback.onDone();
		}
	}

	@Override
	public void delete(final Integer exampleGroupId, final String exampleId, ResponseCallback<Example> callback) {
		try {
			final Example example = exampleRepository.delete(exampleGroupId, exampleId);
			eventPublisher.publishEvent(new ExampleDeletedEvent(this, example));
			callback.onSuccess(new Response<>(example));
		}catch(final Exception exception) {
			callback.onFailure(null, exception);
		}finally {
			callback.onDone();
		}
	}

}
