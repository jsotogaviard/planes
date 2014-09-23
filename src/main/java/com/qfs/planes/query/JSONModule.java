/*
 * (C) Quartet FS 2013
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.qfs.planes.query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * Our own simple module that add the serializers for the class whose
 * serialization needs to be customized.
 *
 * @author QuartetFS
 */
public class JSONModule extends SimpleModule {

	public JSONModule() {
		super("JSON", new Version(1, 0, 0, "")); // TODO see if this version is relevant
	}

	/**
	 * Ensure we use the same json provider in tests than in webapp.
	 */
	public static JacksonJsonProvider jsonProvider() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		 
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		mapper.registerModule(new JSONModule());
        return new JacksonJsonProvider(mapper);
	}

	public static String serialize(Object o) {
		JacksonJsonProvider jsonProvider = JSONModule.jsonProvider();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MultivaluedMap<String,Object> httpHeaders = new MultivaluedHashMap<>();
		try {
			jsonProvider.writeTo(o, o.getClass(), o.getClass(), new Annotation[0], MediaType.APPLICATION_JSON_TYPE, httpHeaders, os);
		} catch (IOException e) {
			throw new AssertionError("Could not serialize " + o, e);
		}
		return os.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Class<T> clazz, String s) {
		JacksonJsonProvider jsonProvider = JSONModule.jsonProvider();
		InputStream is = new ByteArrayInputStream(s.getBytes());
		Object o = s;
		MultivaluedMap<String,String> httpHeaders = new MultivaluedHashMap<>();
		T result ;
		try {
			result = (T) jsonProvider.readFrom(Object.class, clazz, new Annotation[0], MediaType.APPLICATION_JSON_TYPE, httpHeaders, is);
		} catch (IOException e) {
			throw new AssertionError("Could not deserialize " + o, e);
		}
		return result;
	}
}
