/*
 * (C) Quartet FS 2007-2014
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.qfs.planes.query;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.qfs.planes.rest.CypherQuery;

/**
 *
 * @author Quartet FS
 */
public class Queries {
	
	public static String QUERIES = "src/main/resources/queries/";
	
	public static String[] FILES = new String[]{"hubManager/useCaseThree/delayedFlight1.txt"};
	//"/hubManager/useCaseOne/delayedFlight1.txt" 
	//"hubManager/useCaseTwo/delayedFlight.txt"
	//"capacityManager/useCaseOne/delayedFlight.txt"
	//"capacityManager/useCaseOne/nextFlight.txt"
	//"hubManager/useCaseThree/delayedFlight.txt"
	
	public static void main(String[] args) throws IOException {
		
		for (String file : FILES) {
			System.out.println(file);
			String query = new String(Files.readAllBytes(new File(QUERIES + file).toPath()), Charset.defaultCharset());
			query(query);
		}
	}

	public static void query(String mdx) throws ClientProtocolException, IOException {
		long init = System.currentTimeMillis();
		HttpPost request = new HttpPost( "http://localhost:" + 9090 + "/rest/query/");
		CypherQuery mdxQuery = new CypherQuery(mdx);
		StringEntity input = new StringEntity(JSONModule.serialize(mdxQuery));
		input.setContentType("application/json");
		request.setEntity(input);

		// Response
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String response = EntityUtils.toString(httpResponse.getEntity());	// TODO Auto-generated method stub
		System.out.println(response);
		System.out.println(System.currentTimeMillis() - init);

	}
}

