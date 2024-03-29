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

package org.springframework.web.multipart.support;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Abstract base implementation of the MultipartHttpServletRequest interface.
 * Provides management of pre-generated MultipartFile instances.
 *
 * @author Juergen Hoeller
 * @since 06.10.2003
 */
public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper
	implements MultipartHttpServletRequest {

	protected final Log logger = LogFactory.getLog(getClass());

	private Map multipartFiles;

	protected AbstractMultipartHttpServletRequest(HttpServletRequest request) {
		super(request);
	}

	protected void setMultipartFiles(Map multipartFiles) {
		this.multipartFiles = multipartFiles;
	}

	public Iterator getFileNames() {
		return this.multipartFiles.keySet().iterator();
	}

	public MultipartFile getFile(String name) {
		return (MultipartFile) this.multipartFiles.get(name);
	}

	public Map getFileMap() {
		return this.multipartFiles;
	}

}
