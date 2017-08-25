package com.stox.example.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.example.event.ExampleGroupCreatedEvent;
import com.stox.example.event.ExampleGroupDeletedEvent;
import com.stox.example.event.ExampleGroupEditedEvent;
import com.stox.example.model.ExampleGroup;
import com.stox.example.repository.ExampleGroupRepository;

@Lazy
@Async
@Component
public class ExampleGroupClientImpl implements ExampleGroupClient {

	@Autowired
	private ExampleGroupRepository exampleGroupRepository;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public void loadAll(ResponseCallback<List<ExampleGroup>> callback) {
		try {
			callback.onSuccess(new Response<>(exampleGroupRepository.loadAll()));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

	@Override
	public void save(ExampleGroup exampleGroup, ResponseCallback<ExampleGroup> callback) {
		try {
			final boolean created = null == exampleGroup.getId();
			final ExampleGroup managedExampleGroup = exampleGroupRepository.save(exampleGroup);
			final ApplicationEvent event = created ? new ExampleGroupCreatedEvent(this, managedExampleGroup) : new ExampleGroupEditedEvent(this, managedExampleGroup); 
			eventPublisher.publishEvent(event);
			callback.onSuccess(new Response<>(managedExampleGroup));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

	@Override
	public void delete(Integer exampleGroupId, ResponseCallback<ExampleGroup> callback) {
		try {
			final ExampleGroup exampleGroup = exampleGroupRepository.delete(exampleGroupId);
			eventPublisher.publishEvent(new ExampleGroupDeletedEvent(this, exampleGroup));
			callback.onSuccess(new Response<>(exampleGroup));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

}
