/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.util.ExpressionEvaluationUtils;

/**
 * Sets default HTML escape value for the current page. The actual value
 * can be overridden by escaping-aware tags. The default is "false".
 *
 * <p>Note: You can also set a "defaultHtmlEscape" web.xml context-param.
 * A page-level setting overrides a context-param.
 *
 * @author Juergen Hoeller
 * @see RequestContextAwareTag#setHtmlEscape
 * @since 04.03.2003
 */
public class HtmlEscapeTag extends TagSupport {

	/**
	 * ServletContext init parameter (web.xml context-param)
	 */
	public static final String HTML_ESCAPE_CONTEXT_PARAM = "defaultHtmlEscape";

	/**
	 * PageContext attribute for page-level default
	 */
	public static final String HTML_ESCAPE_PAGE_ATTR = "org.springframework.web.servlet.tags.HTML_ESCAPE";

	private String defaultHtmlEscape;

	/**
	 * Retrieve the default HTML escaping setting from the given PageContext,
	 * falling back to the ServletContext init parameter.
	 */
	public static boolean isDefaultHtmlEscape(PageContext pageContext) {
		Boolean defaultValue = (Boolean) pageContext.getAttribute(HTML_ESCAPE_PAGE_ATTR);
		if (defaultValue != null) {
			return defaultValue.booleanValue();
		} else {
			String param = pageContext.getServletContext().getInitParameter(HTML_ESCAPE_CONTEXT_PARAM);
			return Boolean.valueOf(param).booleanValue();
		}
	}

	/**
	 * Set the default value for HTML escaping,
	 * to be put in the current PageContext.
	 */
	public void setDefaultHtmlEscape(String defaultHtmlEscape) throws JspException {
		this.defaultHtmlEscape = defaultHtmlEscape;
	}

	public int doStartTag() throws JspException {
		super.doStartTag();
		boolean resolvedDefaultHtmlEscape = ExpressionEvaluationUtils.evaluateBoolean("defaultHtmlEscape",
			this.defaultHtmlEscape, pageContext);

		// simply add a respective PageContext attribute, for detection by other tags
		this.pageContext.setAttribute(HTML_ESCAPE_PAGE_ATTR, new Boolean(resolvedDefaultHtmlEscape));

		return EVAL_BODY_INCLUDE;
	}

}
