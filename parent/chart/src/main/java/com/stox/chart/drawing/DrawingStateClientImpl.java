package com.stox.chart.drawing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.chart.drawing.Drawing.State;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;

@Lazy
@Async
@Component
public class DrawingStateClientImpl implements DrawingStateClient {

	@Autowired
	private DrawingStateRepository drawingStateRepository;

	@Override
	public void load(Instrument instrument, ResponseCallback<List<State<?>>> callback) {
		try {
			callback.onSuccess(new Response<>(drawingStateRepository.load(instrument)));
		} catch (final Exception exception) {
			exception.printStackTrace();
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

	@Override
	public void save(Instrument instrument, List<State<?>> states, ResponseCallback<Void> callback) {
		try {
			drawingStateRepository.save(instrument, states);
			callback.onSuccess(new Response<>(null));
		} catch (final Exception exception) {
			exception.printStackTrace();
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

}
