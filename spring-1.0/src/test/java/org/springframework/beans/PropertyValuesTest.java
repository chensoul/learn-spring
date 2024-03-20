package org.springframework.beans;

import org.junit.Test;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public class PropertyValuesTest {
	@Test
	public void testMutablePropertyValues() {
		PropertyValue p1 = new PropertyValue("id", 100);
		PropertyValue p2 = new PropertyValue("name", "test");

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.addPropertyValue(p1);
		pvs.addPropertyValue(p2);

		MutablePropertyValues old = new MutablePropertyValues(pvs);

		PropertyValue p3 = new PropertyValue("id", 20);
		pvs.setPropertyValueAt(p3, 0);

		System.out.println(pvs.getPropertyValue("id"));
		System.out.println(pvs.getPropertyValue("name"));

		System.out.println(pvs.changesSince(old));

	}
}
