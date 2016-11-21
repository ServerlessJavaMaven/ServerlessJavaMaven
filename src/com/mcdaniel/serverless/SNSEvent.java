package com.mcdaniel.serverless;

public class SNSEvent implements Event
{
	protected String topicArn;
	protected String topicName;
	protected String displayName;
}
