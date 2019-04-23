package com.mcdaniel.serverless;

public class DynamoEvent implements Event
{
	public enum StartingPosition {TRIM_HORIZON, LATEST};
	
	protected String tableName;
	protected StartingPosition startingPosition;
	protected int batchSize;
}
