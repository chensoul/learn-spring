package org.springframework.mvc;

import org.junit.Test;
import org.springframework.util.PathMatcher;

public class TestPathMatcher {
	@Test
	public void testPathMatcher() {
		String pattern = "com/**/t?st.jsp";
		String url = "com/a/b/test.jsp";
		boolean matched = PathMatcher.match(pattern, url);
		System.out.println("matched = " + matched);
	}
}
