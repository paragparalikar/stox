package com.stox.core.client;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginAspect {

	@Around("target(com.stox.core.client.HasLogin) && execution(@com.stox.core.client.Secured * *(..))")
	public Object advice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		final HasLogin hasLogin = (HasLogin) proceedingJoinPoint.getTarget();
		if (hasLogin.isLoggedIn()) {
			return proceedingJoinPoint.proceed();
		} else {
			hasLogin.login(v -> {
				try {
					return proceedingJoinPoint.proceed();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			});
			
			return null; // TODO bug No data will ever be returned at the login time
		}
	}
}
