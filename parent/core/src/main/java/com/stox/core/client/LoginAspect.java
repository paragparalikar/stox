package com.stox.core.client;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginAspect {

	@Around("target(com.stox.core.client.HasLogin) && execution(@com.stox.core.client.Secured * *(..))")
	public void advice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		final HasLogin hasLogin = (HasLogin) proceedingJoinPoint.getTarget();
		if (hasLogin.isLoggedIn()) {
			proceedingJoinPoint.proceed();
		} else {
			hasLogin.login(v -> {
				try {
					proceedingJoinPoint.proceed();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
				return null;
			});
		}
	}
}
