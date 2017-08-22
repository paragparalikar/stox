package com.stox.core.ui;

import javafx.scene.control.MenuItem;

public interface TargetAwareMenuItemProvider {
	
	boolean supports(final Object target);
	
	MenuItem build(final Object target);
}
