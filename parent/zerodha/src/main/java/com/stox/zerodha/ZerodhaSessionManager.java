package com.stox.zerodha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasLogin;
import com.stox.zerodha.model.ZerodhaSession;
import com.stox.zerodha.ui.ZerodhaLoginModal;

import javafx.application.Platform;

@Lazy
@Component
public class ZerodhaSessionManager implements HasLogin{

	private ZerodhaSession session;

	@Autowired
	private TaskExecutor taskExecutor;

	@Override
	public void login(final Runnable runnable) throws Throwable {
		if (Platform.isFxApplicationThread()) {
			doLogin(runnable);
		} else {
			Platform.runLater(() -> {
				try {
					doLogin(runnable);
				} catch (Throwable e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
		}
	}
	

	private void doLogin(final Runnable runnable) throws Throwable {
		final ZerodhaLoginModal zerodhaLoginModel = new ZerodhaLoginModal(session ->  {
			ZerodhaSessionManager.this.session = session;
			taskExecutor.execute(runnable);
		});
		zerodhaLoginModel.show();
	}

	@Override
	public boolean isLoggedIn() {
		return null != session;
	}
	
	public ZerodhaSession getSession() {
		return session;
	}

}
