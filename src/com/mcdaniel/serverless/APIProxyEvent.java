package com.mcdaniel.serverless;

import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

public class APIProxyEvent
{

	@Parameter(property="apiName", required=true)
	protected String apiName;

	@Parameter(property="resource", required=true)
	protected String resource;
	
	@Parameter(property="subresources", required=false)
	protected List<String> subresources;


}
