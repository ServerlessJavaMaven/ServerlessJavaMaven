package com.mcdaniel.serverless;

import java.util.HashMap;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class BaseLambdaMojo extends BaseServerlessMojo {

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
	
	@Parameter(property="apiEvent", required=false)
	protected APIEvent apiEvent;
	
	@Parameter(property="apiProxyEvent", required=false)
	protected APIProxyEvent apiProxyEvent;
	
	@Parameter(property="environmentVariables", required=false)
	protected HashMap<String, String> environmentVariables;
	
	@Parameter(property="memorySize", required=false)
	protected int memorySize;
	
	@Parameter(property="enableXRay", required=false)
	protected boolean enableXRay;
	
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

	@Parameter(property="sqsQueues", required=false)
	protected List<SQSEvent> sqsQueues;


}
