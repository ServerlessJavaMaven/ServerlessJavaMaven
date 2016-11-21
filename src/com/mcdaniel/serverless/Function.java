package com.mcdaniel.serverless;

import java.util.List;

public class Function
{

	protected String name;
	protected String handler;
	protected APIEvent apiEvent;
	protected DynamoEvent dynamoEvent;
	protected S3Event s3Event;
	protected ScheduleEvent scheduleEvent;
	protected SNSEvent snsEvent;
	protected List<Permission> permissions;
}
