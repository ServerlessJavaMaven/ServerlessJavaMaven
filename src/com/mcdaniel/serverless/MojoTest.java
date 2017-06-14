package com.mcdaniel.serverless;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.mcdaniel.serverless.*;
import com.mcdaniel.serverless.tests.TestArtifact;
import com.mcdaniel.serverless.tests.TestLog;

public class MojoTest
{

	public static void main(String[] args)
	{
		ServerlessDeployMojo m = new ServerlessDeployMojo();
		Log log = new TestLog();
		m.setLog(log);
		
		m.environment = "dev";
		m.regions  ="us-west-2,us-east-2";
		m.description = "Description of function.  TemplateAPI.";
		m.handlerMethod = "com.microstar.api.LambdaFunctionHandler::handleRequest";
//		m.customDomainName = "microstarlogistics.com";
//		m.rolePolicy = "" +
//			"{" +
//			"    \"Version\": \"2012-10-17\"," +
//			"    \"Statement\": [" +
//			"        {" +
//			"            \"Effect\": \"Allow\"," +
//			"            \"Action\": \"logs:CreateLogGroup\"," +
//			"            \"Resource\": \"arn:aws:logs:us-west-2:450017183792:*\"" +
//			"        }," +
//			"        {" +
//			"            \"Effect\": \"Allow\"," +
//			"            \"Action\": [" +
//			"                \"logs:CreateLogStream\"," +
//			"                \"logs:PutLogEvents\"" +
//			"            ]," +
//			"            \"Resource\": [" +
//			"                \"arn:aws:logs:us-west-2:450017183792:log-group:/aws/lambda/TemplateAPI:*\"" +
//			"            ]" +
//			"        }" +
//			"    ]" +
//			"}";
		m.AWSAccessKey = "AKIAJM7YMMLXKUH5EWRQ";
		m.AWSSecretKey = "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";
		m.serviceName="TemplateAPI";
		m.uploadJarBucket="serverless-maven-plugin-uploads";
//		m.function = new Function();
//		m.function.handler = "com.microstar.api.LambdaFunctionHandler";
//		m.function.name="Shipment";

		List<Permission> permissions = new ArrayList<Permission>();
		Permission p = new Permission();
		p.effect="Allow";
		ArrayList<String> actions = new ArrayList<>();
		actions.add("logs:CreateLogStream");
		actions.add("logs:PutLogEvents");
		m.permissions = permissions;
		
		APIEvent apiEvent = new APIEvent();
		apiEvent.apiTitle="TestAPI";
		apiEvent.method="ANY";
		apiEvent.protocol="https";
		apiEvent.swaggerUri="/v2-docs";

		m.apiEvent = apiEvent;
		
		m.project = new MavenProject();
		Artifact artifact = new TestArtifact();
//		m.project.addAttachedArtifact(artifact);
		m.project.setArtifact(artifact);
		
		try
		{
			m.execute();
		} catch (MojoExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
