package com.mcdaniel.serverless;

import java.util.HashMap;

public class APIRequest
{
	public enum PassThrough {NEVER, WHEN_NO_MATCH, WHEN_NO_TEMPLATES};
	
	protected PassThrough passThrough;
	protected HashMap<String, String> template;
}
