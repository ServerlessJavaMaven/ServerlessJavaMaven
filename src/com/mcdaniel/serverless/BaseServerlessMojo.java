package com.mcdaniel.serverless;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class BaseServerlessMojo extends AbstractMojo
{
	@Parameter(property="serviceName", required=true)
	protected String serviceName;
	
	@Parameter(property="environment", required=true)
	/** This is used for the         
	 *   The "BAD.*" should match exception classes that will end up returning an HTTP 400., under the API itself, and the Base Path for the mapping between the Custom Domain Name
	 * and the API definition.
	 */
	protected String environment;
	
	@Parameter(property="handlerMethod", required=true)
	protected String handlerMethod;
	
	@Parameter(property="unauthenticatedRole", required=false)
	/**
	 * This parameter defines the IAM Role to be assumed by this API Gateway entry.  Only supply this if this call does
	 * NOT need to be authenticated.
	 */
	protected String unauthenticatedRole;
	/**
	 * When authenticated access is required, the API Gateway Auth (x-amazon-apigateway-auth) must be set to Type: aws_iam.
	 * Then the integration credentials look like: arn:aws:iam::*:user/* .
	 */
	
	@Parameter(property="regions", required=true)
	protected String regions;
	
	@Parameter(property="basedir", required=true)
	protected File basedir;
	
	@Parameter(property="uploadJarBucket", required=true)
	protected String uploadJarBucket;
	
	@Parameter(property="apiEvent", required=false)
	protected APIEvent apiEvent;
	
	@Parameter(property="apiProxyEvent", required=false)
	protected APIProxyEvent apiProxyEvent;
	
	@Parameter(property="description", required=false)
	protected String description;
	
	@Parameter(property="permissions", required=false)
	protected List<Permission> permissions;
	
	@Parameter(property="environmentVariables", required=false)
	protected HashMap<String, String> environmentVariables;
	
	@Parameter( defaultValue = "${project}", readonly = true )
	protected MavenProject project; 
	
//	@Parameter(property="name", required=true)
//	protected String name;
	
	@Parameter(property="memorySize", required=false)
	protected int memorySize;
	
	@Parameter(property="timeout", required=false)
	protected int timeout;
	
	@Parameter(property="dynamoEvent", required=false)
	protected DynamoEvent dynamoEvent;
	
	@Parameter(property="s3Event", required=false)
	protected S3Event s3Event;
	
	@Parameter(property="scheduleEvent", required=false)
	protected ScheduleEvent scheduleEvent;
	
	@Parameter(property="snsTopics", required=false)
	protected List<SNSEvent> snsTopics;

	public BaseServerlessMojo()
	{
		// TODO Auto-generated constructor stub
	}

}
