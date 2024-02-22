package org.springframework.context.event;

import org.junit.Test;
import org.springframework.context.ApplicationEvent;

public class ApplicationEventMulticasterTest {
	@Test
	public void eventHello() {
		ApplicationEventMulticaster multicaster = new ApplicationEventMulticasterImpl();
		multicaster.addApplicationListener(new ConsoleListener());
		ApplicationEvent event = new MyEvent(this);
		multicaster.onApplicationEvent(event);
	}
}

