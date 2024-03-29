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

package org.springframework.ui.velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;

/**
 * Utility class for working with a VelocityEngine.
 * Provides convenience methods to merge a Velocity template with a model.
 *
 * @author Juergen Hoeller
 * @since 22.01.2004
 */
public abstract class VelocityEngineUtils {

	private static final Log logger = LogFactory.getLog(VelocityEngineUtils.class);

	/**
	 * Merge the specified Velocity template with the given model and write
	 * the result to the given Writer.
	 *
	 * @param velocityEngine   VelocityEngine to work with
	 * @param templateLocation the location of template, relative to Velocity's
	 *                         resource loader path
	 * @param model            the Map that contains model names as keys and model objects
	 *                         as values
	 * @param writer           the Writer to write the result to
	 * @throws VelocityException if the template wasn't found or rendering failed
	 */
	public static void mergeTemplate(VelocityEngine velocityEngine, String templateLocation, Map model,
									 Writer writer) throws VelocityException {
		try {
			VelocityContext velocityContext = new VelocityContext(model);
			velocityEngine.mergeTemplate(templateLocation, velocityContext, writer);
		} catch (VelocityException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
			throw new VelocityException(ex.getMessage());
		}
	}

	/**
	 * Merge the specified Velocity template with the given model and write
	 * the result to the given Writer.
	 *
	 * @param velocityEngine   VelocityEngine to work with
	 * @param templateLocation the location of template, relative to Velocity's
	 *                         resource loader path
	 * @param encoding         the encoding of the template file
	 * @param model            the Map that contains model names as keys and model objects
	 *                         as values
	 * @param writer           the Writer to write the result to
	 * @throws VelocityException if the template wasn't found or rendering failed
	 */
	public static void mergeTemplate(VelocityEngine velocityEngine, String templateLocation, String encoding,
									 Map model, Writer writer) throws VelocityException {
		try {
			VelocityContext velocityContext = new VelocityContext(model);
			velocityEngine.mergeTemplate(templateLocation, encoding, velocityContext, writer);
		} catch (VelocityException ex) {
			throw ex;
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Why does VelocityEngine throw a generic checked exception, after all?", ex);
			throw new VelocityException(ex.getMessage());
		}
	}

	/**
	 * Merge the specified Velocity template with the given model into a String.
	 *
	 * @param velocityEngine   VelocityEngine to work with
	 * @param templateLocation the location of template, relative to Velocity's
	 *                         resource loader path
	 * @param model            the Map that contains model names as keys and model objects
	 *                         as values
	 * @return the result as String
	 * @throws VelocityException if the template wasn't found or rendering failed
	 */
	public static String mergeTemplateIntoString(VelocityEngine velocityEngine, String templateLocation,
												 Map model) throws VelocityException {
		StringWriter result = new StringWriter();
		mergeTemplate(velocityEngine, templateLocation, model, result);
		return result.toString();
	}

	/**
	 * Merge the specified Velocity template with the given model into a String.
	 *
	 * @param velocityEngine   VelocityEngine to work with
	 * @param templateLocation the location of template, relative to Velocity's
	 *                         resource loader path
	 * @param encoding         the encoding of the template file
	 * @param model            the Map that contains model names as keys and model objects
	 *                         as values
	 * @return the result as String
	 * @throws VelocityException if the template wasn't found or rendering failed
	 */
	public static String mergeTemplateIntoString(VelocityEngine velocityEngine, String templateLocation,
												 String encoding, Map model) throws VelocityException {
		StringWriter result = new StringWriter();
		mergeTemplate(velocityEngine, templateLocation, encoding, model, result);
		return result.toString();
	}

}
