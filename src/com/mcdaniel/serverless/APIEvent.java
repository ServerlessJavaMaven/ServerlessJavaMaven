package com.mcdaniel.serverless;

import java.util.HashMap;

import org.apache.maven.plugins.annotations.Parameter;

public class APIEvent implements Event
{
	/**
	 * The "RestAPI" name. Will be created if it doesn't exist.
	 */
	@Parameter(property="apiTitle", required=true)
	protected String apiTitle;
	
	@Parameter(property="customDomainName", required=false)
	protected String customDomainName;
	
	@Parameter(property="protocol", required=false, defaultValue="http")
	protected String protocol;

	@Parameter(property="method", required=true)
	protected String method;
	
	@Parameter(property="uri", required=true)
	protected String uri;
	
	@Parameter(property="cors", required=false, defaultValue="false")
	protected boolean cors;
	
	@Parameter(property="requestParameters", required=false)
	protected HashMap<String, String> requestParameters;
	
	@Parameter(property="responeParameters", required=false)
	protected HashMap<String, String> responseParameters;
	
	@Parameter(property="request")
	protected APIRequest request;
	
	@Parameter(property="request")
	protected APIResponse response;
	
	/**
	 * The URI to be appended to the service URI to retrieve the Swagger 2.0 definition for the
	 * Lambda class.  Implies that we'll being using Swagger for API definition and 
	 * NOT using the {proxy+} method.
	 */
	@Parameter(property="swaggerUri", required=false)
	protected String swaggerUri;

	/**
	 * @return the apiTitle
	 */
	public String getApiGroupName()
	{
		return apiTitle;
	}

	/**
	 * @param apiTitle the apiTitle to set
	 */
	public void setApiGroupName(String apiGroupName)
	{
		this.apiTitle = apiGroupName;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol()
	{
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	/**
	 * @return the method
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}

	/**
	 * @return the uri
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 * @return the cors
	 */
	public boolean isCors()
	{
		return cors;
	}

	/**
	 * @param cors the cors to set
	 */
	public void setCors(boolean cors)
	{
		this.cors = cors;
	}

	/**
	 * @return the request
	 */
	public APIRequest getRequest()
	{
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(APIRequest request)
	{
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public APIResponse getResponse()
	{
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(APIResponse response)
	{
		this.response = response;
	}

	/**
	 * @return the swaggerUri
	 */
	public String getSwaggerUri()
	{
		return swaggerUri;
	}

	/**
	 * @param swaggerUri the swaggerUri to set
	 */
	public void setSwaggerUri(String swaggerUri)
	{
		this.swaggerUri = swaggerUri;
	}
	
}
