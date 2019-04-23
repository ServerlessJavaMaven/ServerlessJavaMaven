package com.mcdaniel.serverless;

public class S3Event implements Event
{

	protected String bucket;
	protected String event;
	protected S3Rules rules;
}
