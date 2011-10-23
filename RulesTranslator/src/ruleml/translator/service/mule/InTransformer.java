/*
 * $Id: HttpRequestToNameString.java 11328 2008-03-12 10:27:11Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package ruleml.translator.service.mule;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import org.mule.transformers.AbstractTransformer;
import org.mule.umo.transformer.TransformerException;
import org.mule.util.PropertiesUtils;

import ruleml.translator.service.RulesLanguage;

public class InTransformer extends AbstractTransformer {
	public InTransformer() {
		super();
		this.registerSourceType(String.class);
		this.registerSourceType(byte[].class);
		this.setReturnClass(RulesLanguage.class);
	}

	public Object doTransform(Object src, String encoding)
			throws TransformerException {
		try {
			if (src instanceof byte[]) {
				byte[] bytes = (byte[]) src;
				String request;
				request = new String(bytes, "UTF8");
				request = URLDecoder.decode(request,"UTF8");
				int i = request.indexOf('?');
				String query = request.substring(i + 1);
				Properties p = PropertiesUtils
						.getPropertiesFromQueryString(query);
				for (Object k : p.keySet()) {
					System.out.println("!!! " + k + " - " + p.get(k));
				}
			} else {
				System.out.println("*** " + src.getClass());
			}

			RulesLanguage rulesLanguage = new RulesLanguage("Test", "1.0");
			return rulesLanguage;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Error";
		}
	}
}
