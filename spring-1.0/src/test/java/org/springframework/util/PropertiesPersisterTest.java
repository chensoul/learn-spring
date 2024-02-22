package org.springframework.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Test;

public class PropertiesPersisterTest {
	@Test
	public void testPersisterLoadProperties() throws Exception {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		Properties properties = new Properties();
		ClassLoader classLoader = Thread.currentThread()
			.getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream("messages/label.properties");
		persister.load(properties, stream);
		properties.list(System.out);
	}

	@Test
	public void testPersisterStoreProperties() throws Exception {
		DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
		Properties properties = new Properties();
		properties.put("a", "aaaa");
		properties.put("b", "bbb");
		FileOutputStream outputStream = new FileOutputStream("test.properties");
		persister.store(properties, outputStream, "this is a header");
	}

}
