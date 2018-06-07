package com.mcdaniel.serverless;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.auth.policy.conditions.SNSConditionFactory;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.ContentHandlingStrategy;
import com.amazonaws.services.apigateway.model.CreateResourceRequest;
import com.amazonaws.services.apigateway.model.CreateResourceResult;
import com.amazonaws.services.apigateway.model.DeleteResourceRequest;
import com.amazonaws.services.apigateway.model.DeleteResourceResult;
import com.amazonaws.services.apigateway.model.GetResourcesRequest;
import com.amazonaws.services.apigateway.model.GetResourcesResult;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.Integration;
import com.amazonaws.services.apigateway.model.IntegrationResponse;
import com.amazonaws.services.apigateway.model.IntegrationType;
import com.amazonaws.services.apigateway.model.Method;
import com.amazonaws.services.apigateway.model.PutIntegrationRequest;
import com.amazonaws.services.apigateway.model.PutIntegrationResult;
import com.amazonaws.services.apigateway.model.PutMethodRequest;
import com.amazonaws.services.apigateway.model.PutMethodResponseRequest;
import com.amazonaws.services.apigateway.model.PutMethodResponseResult;
import com.amazonaws.services.apigateway.model.PutMethodResult;
import com.amazonaws.services.apigateway.model.Resource;
import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreatePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreatePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.identitymanagement.model.DeletePolicyRequest;
import com.amazonaws.services.identitymanagement.model.DeletePolicyResult;
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleResult;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.identitymanagement.model.NoSuchEntityException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.AddPermissionRequest;
import com.amazonaws.services.lambda.model.AddPermissionResult;
import com.amazonaws.services.lambda.model.CreateAliasRequest;
import com.amazonaws.services.lambda.model.CreateAliasResult;
import com.amazonaws.services.lambda.model.CreateFunctionRequest;
import com.amazonaws.services.lambda.model.CreateFunctionResult;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
import com.amazonaws.services.lambda.model.DeleteFunctionResult;
import com.amazonaws.services.lambda.model.Environment;
import com.amazonaws.services.lambda.model.FunctionCode;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.GetPolicyRequest;
import com.amazonaws.services.lambda.model.GetPolicyResult;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.PublishVersionRequest;
import com.amazonaws.services.lambda.model.PublishVersionResult;
import com.amazonaws.services.lambda.model.RemovePermissionRequest;
import com.amazonaws.services.lambda.model.RemovePermissionResult;
import com.amazonaws.services.lambda.model.UpdateAliasRequest;
import com.amazonaws.services.lambda.model.UpdateAliasResult;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.ListSubscriptionsRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.Subscription;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.model.UnsubscribeRequest;
import com.amazonaws.services.sns.model.UnsubscribeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mcdaniel.serverless.policy.LambdaPolicy;
import com.mcdaniel.serverless.policy.Statement;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.parser.SwaggerParser;
 
/**
 * Says "Hi" to the user.
 *
 */
@Mojo( name = "deploy", defaultPhase=LifecyclePhase.DEPLOY, requiresOnline=true, requiresProject=true)
public class ServerlessDeployMojo extends BaseServerlessMojo
{
	
	private static final String DEFAULT_PRODUCES_CONTENT_TYPE = "application/json";
    private static final String EXTENSION_AUTH = "x-amazon-apigateway-auth";
    private static final String EXTENSION_INTEGRATION = "x-amazon-apigateway-integration";
    
    private Swagger swagger;
    
	public void execute() throws MojoExecutionException
    {
    	String rolePolicy = null;

    	getLog().info("serverless-maven-plugin:deploy starting");
        getLog().info("Logger class: " + getLog().getClass().getName());
        
        if ( project != null )
        {
        	getLog().info("Name/Packaging: " + project.getName() + " / " + project.getPackaging());
        	getLog().info("Artifact name: " + project.getArtifact().getFile().getName());
        }
        
        // Create the credentials for all of the clients.
//        getLog().info("AccessKey: " + AWSAccessKey);
//        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWSAccessKey, AWSSecretKey);
        
        // Setup needed variables
        String assumedRoleName = serviceName + "_AssumedRole";
        
        // Create all of the needed clients.
        getLog().info("Getting clients...");
//        AWSLambdaClient lambdaClient = new AWSLambdaClient(awsCredentials);
//        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);
//        AmazonIdentityManagementClient iamClient = new AmazonIdentityManagementClient(awsCredentials);
//        AmazonApiGatewayClient apiClient = new AmazonApiGatewayClient(awsCredentials);
        

        getLog().info("Done.");
        
        String accountNumber = null;
        
        //
        // Storage for the clients
        //
        HashMap<String, Object> clients = new HashMap<>();
        AmazonIdentityManagement iamClient = AmazonIdentityManagementClientBuilder.standard().build();
        
        //
        // Upload the jar to S3
        //
        HashMap<String,Bucket> uploadBucket = new HashMap<>();
        for ( String region : regions.split(","))
        {
        	Regions regionEnum = Regions.fromName(region);
            AWSLambda lambdaClient = AWSLambdaClientBuilder.standard().withRegion(regionEnum).build();
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(regionEnum).build();
            AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withRegion(regionEnum).build();
            AmazonApiGateway apiClient = AmazonApiGatewayClientBuilder.standard().withRegion(regionEnum).build();
            clients.put(region+"-lambda", lambdaClient);
            clients.put(region+"-s3", s3Client);
            clients.put(region+"-apigw", apiClient);
            clients.put(region+"-sns", snsClient);
            
            //
            // Get the account number.
            //
            if ( accountNumber == null )
            {
	            GetUserResult guRes = iamClient.getUser();
	            String[] accountArn = guRes.getUser().getArn().split(":");
	            getLog().info("Account Number: " + accountArn[4]);
	            accountNumber = accountArn[4];
            }
            
            getLog().info("Environment: " + environment);
            String uploadJarBucketName = environment + "." + region + "." + uploadJarBucket;
            uploadJarBucketName = uploadJarBucketName.toLowerCase();
//	        s3Client.setRegion(Region.getRegion(Regions.fromName(region)));
            getLog().info("Checking if bucket " + uploadJarBucketName.toLowerCase() + " exists...");
	    	if ( !s3Client.doesBucketExist(uploadJarBucketName.toLowerCase()))	// If bucket doesn't exist, create it.
	    	{
	    		getLog().info("Creating bucket " + uploadJarBucketName);
	    		Bucket cbRes = null;
	    		try
	    		{
	    			cbRes = s3Client.createBucket(uploadJarBucketName);
	    		}
	    		catch ( Exception ex )
	    		{
	    			getLog().error("Caught exception creating bucket " + uploadJarBucketName);
	    			throw ex;
	    		}
//	    		uploadBucket.put(region, cbRes);
	    	}
	    	else
	    	{
	    		getLog().info("Bucket already exists");
	    	}
	
	    	getLog().info("Uploading Jar in " + region + "...");
	    	PutObjectRequest poReq = new PutObjectRequest(uploadJarBucketName, project.getArtifact().getFile().getName(), 
	    			project.getArtifact().getFile());
	    	poReq.setGeneralProgressListener(new UploadProgressListener());
	    	PutObjectResult poRes = null;
	    	
	    	try
	    	{
	    		getLog().info("Uploading JAR " + project.getArtifact().getFile().getName() + "...");
	    		poRes = s3Client.putObject(poReq);
	    	}
	    	catch ( Exception ex )
	    	{
    			getLog().error("Caught exception uploading JAR " + project.getArtifact().getFile());
	    		ex.printStackTrace();
	    		return;
	    	}
        }
        
    	//
    	// Deal with the role to be assumed
    	//
    	GetRoleRequest grReq = new GetRoleRequest()
    			.withRoleName(assumedRoleName);
    	boolean assumedRoleExists = true;
    	GetRoleResult grRes = null;
    	try
    	{
	    	grRes = iamClient.getRole(grReq);
	    	if ( grRes == null )
	    		getLog().info("Role " + assumedRoleName + " does not already exist, creating.");
	    	else
	    	{
	    		getLog().info("Role " + assumedRoleName + " already exists, updating.");
//	    		getLog().info("Status code: " + grRes.getSdkHttpMetadata().getHttpStatusCode());
//	    		getLog().info("grRes != null, Arn: " + grRes.getRole().getArn());
	    	}
    	}
    	catch ( Exception ex )
    	{
    		assumedRoleExists = false;
    	}
    	
    	String roleArn = null;
    	if ( assumedRoleExists )
    	{
    		roleArn = grRes.getRole().getArn();
    		
    		// Update the policy in case it changed
    		String policyArn = "arn:aws:iam::" + accountNumber + ":policy/" + serviceName + "_AssumedPolicy";
    		getLog().info("Detaching policy " + policyArn);
    		DetachRolePolicyRequest drpReq = new DetachRolePolicyRequest()
    				.withPolicyArn(policyArn)
    				.withRoleName(assumedRoleName);
    		DetachRolePolicyResult drpRes = null;
    		try
    		{
    			drpRes = iamClient.detachRolePolicy(drpReq);
    		} catch ( NoSuchEntityException nsee )
    		{
    			// Do nothing
    		} catch ( Exception ex ) 
    		{
    			ex.printStackTrace();
    		}
    		getLog().info("Deleting policy " + policyArn);
    		DeletePolicyRequest dpReq = new DeletePolicyRequest()
    				.withPolicyArn(policyArn);
    		try
    		{
    			DeletePolicyResult dpRes = iamClient.deletePolicy(dpReq);
    			int x = 1;
    		} catch ( Exception ex ) { ex.printStackTrace();}
    		sleep(5);
    	}
    	else
    	{
    		String assumeRolePolicy = "{\"Version\": \"2012-10-17\"," +
    				"\"Statement\": [{" +
    				"\"Sid\": \"\", " +
    				"\"Effect\": \"Allow\", " +
    				"\"Principal\": {\"Service\": \"lambda.amazonaws.com\"}, " +
    				"\"Action\": [\"sts:AssumeRole\"] " +
    				"}]}";
    		CreateRoleRequest crReq = new CreateRoleRequest()
    				.withRoleName(assumedRoleName);
    		crReq.setAssumeRolePolicyDocument(assumeRolePolicy);
    		
    		getLog().info("Creating role with policy: " + rolePolicy);
    		getLog().info("Trust policy: " + assumeRolePolicy);
    		CreateRoleResult crRes = iamClient.createRole(crReq);
    		roleArn = crRes.getRole().getArn();
    	}
    	
		// Create the custom policy
    	rolePolicy = "{" +
			"    \"Version\": \"2012-10-17\",\n" +
    		"    \"Statement\": [\n";

    	// Loop through all of the Permission entries
    	Permission [] perms = permissions.toArray(new Permission[0]);
    	String permPolicy = "";
    	try
    	{
	    	for ( int pi = 0; pi < perms.length; pi ++ )
	    	{
	    		Permission p = perms[pi];
	    		
	    		String permComma = pi == perms.length-1 ? "" : ",";	// If we're at the end, comma="", else comma=","
	
	    		permPolicy = "{\"Effect\": \"" + p.effect + "\",";
	    		
		    	String[] regionList = regions.split(",");
		    	permPolicy += "\"Resource\": [";
		    	String tmp = "";
		    	for ( int i = 0; i < regionList.length; i ++ )
		    	{
		    		String region = regionList[i];
		    		String regionComma = i == regionList.length-1 ? "" : ",";	// If we're at the end, comma="", else comma=","
		    		
		    		List<String> resourceList = p.resources;
		    		for ( String resource : resourceList )
		    		{ 
		    			tmp += ",\"" + resource + "\"";
		    		}
		    	}
		    	permPolicy += tmp.substring(1);
		    	permPolicy += "],";
	    		
	    		permPolicy += "\"Action\": [";
	    		String [] actionList = p.actions.toString().split(",");
	    		
	    		for ( int ai = 0; ai < actionList.length; ai ++ )
	    		{
	    			String action = actionList[ai];
		    		String actionComma = ai == actionList.length-1 ? "" : ",";	// If we're at the end, comma="", else comma=","
		    		action = action.replace("[", "").replace("]", "").replace(" ", "");
	    			permPolicy += "\"" + action + "\"" + actionComma;
	    		}
	    		permPolicy += "]}" + permComma;
	    		rolePolicy += permPolicy;
	    		
	    	} // End of loop on Permission entries
    	}
    	catch ( Exception ex )
    	{
    		getLog().info("Caught ex: " + ex.getMessage() + "\nPermPolicy: " + permPolicy + "\nRolePolicy: " + rolePolicy);
    		ex.printStackTrace();
    	}
    	rolePolicy += 
			"    ]\n" +
			"}\n";
    	
    	// First update the variables
    	rolePolicy = rolePolicy.replace("$regions$", regions);
    	rolePolicy = rolePolicy.replace("$region$", regions);
    	rolePolicy = rolePolicy.replace("$accountId$", accountNumber);
    	rolePolicy = rolePolicy.replace("$serviceName$", serviceName);
    	rolePolicy = rolePolicy.replace("$environment$", environment);
    	
		String policyName = serviceName + "_AssumedPolicy";
		getLog().info("Creating custom policy name: " + policyName);
		getLog().info("Creating custom policy: " + rolePolicy);
		CreatePolicyRequest cpReq = new CreatePolicyRequest()
				.withPolicyDocument(rolePolicy)
				.withPolicyName(policyName);
		CreatePolicyResult cpRes = null;
		try
		{
			cpRes = iamClient.createPolicy(cpReq);
			if ( cpRes.getSdkHttpMetadata().getHttpStatusCode() > 201 )
			{
				getLog().error("Error from CreatePolicy: " + cpRes.getSdkHttpMetadata().getHttpStatusCode());
				throw new Exception("Eror from CreatePolicy: " + cpRes.getSdkHttpMetadata().getHttpStatusCode());
			}
		}
		catch ( Exception ex )
		{
			getLog().error("Caught exception creating the Assume Policy: " + rolePolicy);
			ex.printStackTrace();
			return;
		}
		
		// Attach the custom policy
		try
		{
			getLog().info("Attaching Policy to Role...");
			AttachRolePolicyRequest arpReq = new AttachRolePolicyRequest()
					.withRoleName(assumedRoleName)
					.withPolicyArn(cpRes.getPolicy().getArn());
			AttachRolePolicyResult arpRes = iamClient.attachRolePolicy(arpReq);
		}
		catch ( Exception ex )
		{
			getLog().error(String.format("Caught exception Attaching Policy (arn: %s) to Role (name: %s): %s", cpRes.getPolicy().getArn(), assumedRoleName, ex.getMessage()));
			ex.printStackTrace();
			return;
		}
		//
    	// Upload the function
    	//
    	for ( String region : regions.split(",") )
    	{
    		getLog().debug("Processing for Region: " + region);
    		
    		// Set the client region
//        	lambdaClient.setRegion(Region.getRegion(Regions.fromName(region)));
//        	s3Client.setRegion(Region.getRegion(Regions.fromName(region)));
//        	apiClient.setRegion(Region.getRegion(Regions.fromName(region)));
            String uploadJarBucketName = environment + "." + region + "." + uploadJarBucket;
            uploadJarBucketName = uploadJarBucketName.toLowerCase();

            
	    	AWSLambda lambdaClient = (AWSLambda) clients.get(region+"-lambda");
	    	AmazonApiGateway apiClient = (AmazonApiGateway) clients.get(region+"-apigw");
	    	AmazonS3 s3Client = (AmazonS3) clients.get(region+"-s3");
	    	AmazonSNS snsClient = (AmazonSNS) clients.get(region+"-sns");
	    	
			// Try deleting the function
	    	getLog().debug("Deleting function: " + serviceName);
	    	DeleteFunctionRequest dfReq = new DeleteFunctionRequest();
	    	dfReq.setFunctionName(serviceName);
	    	try
	    	{
	    		DeleteFunctionResult dfRes = lambdaClient.deleteFunction(dfReq);
	    		getLog().info("Delete function returned: " + dfRes.getSdkHttpMetadata().getHttpStatusCode());
	    	}
	    	catch ( Exception ex )
	    	{
	    		// DO NOTHING.
	    	}
	    	
	    	// See if the function already exists
	        ListFunctionsResult lfRes = lambdaClient.listFunctions();
	        List<FunctionConfiguration> functions = lfRes.getFunctions();
	        boolean updateFunction = false;
	        
	        for ( FunctionConfiguration function : functions )
	        {
	        	getLog().debug("Found existing function: " + function.getFunctionName());
	        	if ( function.getFunctionName().equals(serviceName))
	        		updateFunction = true;
	        }

	        updateFunction = false;	// Because we deleted it
	        
	        // Create or update the function
	        String serviceArn = null;
	        if ( !updateFunction )
	        {
	        	getLog().info("Creating function!");
	        	
	            CreateFunctionRequest cfReq = new CreateFunctionRequest();
	        	cfReq.setFunctionName(serviceName);
	        	cfReq.setPublish(true);
	        	cfReq.setHandler(handlerMethod);
	        	
	        	if ( environmentVariables != null && environmentVariables.size() != 0 )
	        	{
		        	Environment env = new Environment()
		        			.withVariables(environmentVariables);
		        	cfReq.setEnvironment(env);
		        	for ( String k : environmentVariables.keySet() )
		        	{
		        		String v = environmentVariables.get(k);
		        		getLog().info("Setting environment variable " + k);
		        	}
	        	}
	        	
	        	FunctionCode code = new FunctionCode();
	        	code.setS3Bucket(uploadJarBucketName);
	        	code.setS3Key(project.getArtifact().getFile().getName());
	        	cfReq.setCode(code);
	        	cfReq.setRole(roleArn);
	        	cfReq.setRuntime("java8");
	        	if ( timeout == 0 )
	        	{
		        	cfReq.setTimeout(3*60);
	        	}
	        	else
	        	{
	        		cfReq.setTimeout(timeout);
	        	}
	        	
	        	if ( memorySize == 0 )
	        	{
	        		cfReq.setMemorySize(448);
	        	}
	        	else
	        	{
	        		cfReq.setMemorySize(memorySize);
	        	}
        		getLog().info("Setting memory size to: " + cfReq.getMemorySize());
	        	
	        	if ( ! Strings.isNullOrEmpty(description))
	        		cfReq.setDescription(description);
	        	while ( true )
	        	{
	        		try
	        		{
	    				getLog().info("Trying Function create");
			        	CreateFunctionResult cfRes = lambdaClient.createFunction(cfReq);
			        	getLog().info("cfres: " + cfRes.getLastModified());
			        	serviceArn = cfRes.getFunctionArn();
			        	getLog().info("Function Arn: " + cfRes.getFunctionArn());
			        	break;
	        		}
	        		catch ( com.amazonaws.services.lambda.model.InvalidParameterValueException ipve )
	        		{
	        			getLog().debug("Caught ipve, sleeping...");
	        			if ( ipve.getMessage() != null && ipve.getMessage().contains("The role defined for the function cannot be assumed"))
	        			{
		        			try
		        			{
		        				Thread.sleep(5000);
		        			}
		        			catch ( Exception ex ) {}
	        			}
	        			else
	        			{
	        				getLog().error("Caught unknown IPVE: " + ipve.getMessage());
	        				return;
	        			}
	        		}
	        		catch ( Throwable ex )
	        		{
	        			getLog().error("Caught exception creating function", ex);
	        			return;
	        		}
	        		getLog().info("Shouldn't be here!");
	        	}
	        	
	        	if ( apiEvent != null && apiEvent.apiTitle != null )
	        	{
	        		getLog().info("Finding Rest API: " + apiEvent.apiTitle);
	        		// Find the API Id of the API, if it exists
	        		GetRestApisRequest graReq = new GetRestApisRequest();
					GetRestApisResult graRes = apiClient.getRestApis(graReq);
					String apiId = null;
					for ( RestApi ra : graRes.getItems() )
					{
						if ( ra.getName().equals(apiEvent.apiTitle))
							apiId = ra.getId();
					}
	        		getLog().info("Found Rest API, id: " + apiId);
					
					if ( apiId != null )
					{
						// Update
						addLambdaPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient);
//						addLambdaPermission(apiId, region, accountNumber, serviceArn + ":dev", serviceName, lambdaClient);
//						addLambdaPermission(apiId, region, accountNumber, serviceArn + ":test", serviceName, lambdaClient);
					}
	        	}
	        }
	        else
	        {
	        	getLog().info("Updating function!");
	            UpdateFunctionCodeRequest ufReq = new UpdateFunctionCodeRequest();
	            ufReq.setFunctionName(serviceName);
	            ufReq.setPublish(true);
	            ufReq.setS3Bucket(uploadJarBucketName);
	            ufReq.setS3Key(project.getArtifact().getFile().getName());
	        	if ( environmentVariables != null && environmentVariables.size() != 0 )
	        	{
		        	Environment env = new Environment()
		        			.withVariables(environmentVariables);
		        	UpdateFunctionConfigurationRequest ufcReq = new UpdateFunctionConfigurationRequest()
		        			.withEnvironment(env)
		        			.withFunctionName(serviceName);
		        	UpdateFunctionConfigurationResult ufcRes = lambdaClient.updateFunctionConfiguration(ufcReq);
		        	for ( String k : environmentVariables.keySet() )
		        	{
		        		String v = environmentVariables.get(k);
		        		getLog().info("Updating environment variable " + k);
		        	}
	        	}
	        	
	            UpdateFunctionCodeResult ufRes = lambdaClient.updateFunctionCode(ufReq);
	            if ( ufRes.getSdkHttpMetadata().getHttpStatusCode() == 200 )
	            {
	            	getLog().info("Update function succeeded!");
	            	serviceArn = ufRes.getFunctionArn();
	            }
	            else
	            {
	            	getLog().info("Update function failed: " + ufRes.getSdkHttpMetadata().getHttpStatusCode());
	            }

	        	if ( apiEvent != null && apiEvent.apiTitle != null )
	        	{
	        		getLog().info("Finding Rest API: " + apiEvent.apiTitle);
	        		// Find the API Id of the API, if it exists
	        		GetRestApisRequest graReq = new GetRestApisRequest();
					GetRestApisResult graRes = apiClient.getRestApis(graReq);
					String apiId = null;
					for ( RestApi ra : graRes.getItems() )
					{
						if ( ra.getName().equals(apiEvent.apiTitle))
							apiId = ra.getId();
					}
	        		getLog().info("Found Rest API, id: " + apiId);
					
					if ( apiId != null )
					{
						addLambdaPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient);
//						addLambdaPermission(apiId, region, accountNumber, serviceArn, serviceName + ":dev", lambdaClient);
//						addLambdaPermission(apiId, region, accountNumber, serviceArn, serviceName + ":test", lambdaClient);
					}
	        	}
	        }
	        
	        String serviceArnUnqualified = "arn:aws:lambda:" + region + ":" + accountNumber + ":function:" + serviceName;
	        
			// Publish a function
	        PublishVersionRequest pvReq = new PublishVersionRequest()
	        		.withFunctionName(serviceArnUnqualified);
	        PublishVersionResult pvRes = null;
	        try
	        {
	        	pvRes = lambdaClient.publishVersion(pvReq);
	        }
	        catch ( Exception ex )
	        {
	        	ex.printStackTrace();
	        	return;
	        }
	        
	        String pubVersion = pvRes.getVersion();
	        getLog().info("Published version: " + pubVersion);
	        
	        // Setup the alias
	        String aliasId = "";
	        UpdateAliasRequest uaReq = new UpdateAliasRequest()
	        		.withName(environment)
	        		.withDescription(environment + " Version")
	        		.withFunctionName(serviceName)
	        		.withFunctionVersion(pubVersion);
	        UpdateAliasResult uaRes = null;
	        getLog().info("Setting Alias to " + environment);
	        try
	        {
	        	uaRes = lambdaClient.updateAlias(uaReq);
	        	getLog().info("Update Alias succeeded.");
	        }
	        catch ( Exception ex )
	        {
	        	getLog().info("Update Alias failed, doing Create Alias");
	        	CreateAliasRequest createaReq = new CreateAliasRequest()
	        			.withDescription(environment + " version")
	        			.withName(environment)
	        			.withFunctionName(serviceName)
	        			.withFunctionVersion(pubVersion);
	        	CreateAliasResult createaRes = lambdaClient.createAlias(createaReq);
	        	getLog().info("Create Alias succeeded.");
	        }
    	}
    	
    	// Handle sns events
    	if ( snsTopic != null && snsTopic.displayName != null )
    	{
        	snsTopic.topicArn = snsTopic.topicArn.replace("$regions$", regions);
        	snsTopic.topicArn = snsTopic.topicArn.replace("$region$", regions);
        	snsTopic.topicArn = snsTopic.topicArn.replace("$accountId$", accountNumber);

    		for ( String region : regions.split(","))
    		{
	    		getLog().info(String.format("Processing SNS Subscription configuration in region %s for %s/%s/%s ", region, snsTopic.displayName, 
	    				snsTopic.topicName, snsTopic.topicArn));
	    		AmazonSNS snsClient = (AmazonSNS) clients.get(region + "-sns");
	    		String endpoint = "arn:aws:lambda:" + region + ":" + accountNumber + ":function:" + serviceName;
				String protocol = "lambda";
				String topicArn = snsTopic.topicArn;
	    		
				// Check to see if the topic exists; if not, create it
	    		ListTopicsRequest ltReq = new ListTopicsRequest();
	    		ListTopicsResult listRes = snsClient.listTopics(ltReq);
	    		if ( listRes.getSdkHttpMetadata().getHttpStatusCode() != 200 )
	    		{
	    			getLog().error("Failed to list SNS Topics!");
	    		}
	    		
	    		boolean foundTopic = false;
	    		for ( Topic t : listRes.getTopics() )
	    		{
	    			getLog().debug("Found topic: " + t.getTopicArn());
	    			if ( t.getTopicArn().contains(":" + snsTopic.topicName))
	    				foundTopic = true;
	    		}
	    		getLog().debug("FoundTopic: " + foundTopic);
	    		
	    		if ( !foundTopic )
	    		{
	    			CreateTopicRequest ctReq = new CreateTopicRequest()
	    					.withName(snsTopic.topicName);
					CreateTopicResult ctRes = snsClient.createTopic(ctReq);
					getLog().info("Created Topic " + snsTopic.topicName + ", status: " + ctRes.getSdkHttpMetadata().getHttpStatusCode());
	    		}
	    		
				// See if the subscription already exists; if so, delete it
	    		boolean foundSub = false;
	    		String foundSubArn = null;
	    		if ( foundTopic )
	    		{
		    		ListSubscriptionsResult lsRes = snsClient.listSubscriptions();
		    		for ( Subscription s : lsRes.getSubscriptions() )
		    		{
		    			getLog().debug(String.format("Found subscription %s to %s", s.getSubscriptionArn(), s.getEndpoint()));
		    			if ( s.getEndpoint().equals(endpoint))
		    			{
		    				foundSub = true;
		    				foundSubArn = s.getSubscriptionArn();
		    			}
		    		}
	    		}
	    		getLog().debug("FoundSub: " + foundSub);
	    		
	    		if ( foundSub )
	    		{
	    			UnsubscribeRequest uReq = new UnsubscribeRequest()
	    					.withSubscriptionArn(foundSubArn);
					UnsubscribeResult uRes = snsClient.unsubscribe(uReq);
					getLog().info("Unsubscribed from topic: " + uRes.getSdkHttpMetadata().getHttpStatusCode());
	    		}
	    		
	    		// Create the subscriptions
				SubscribeRequest subReq = new SubscribeRequest()
	    				.withEndpoint(endpoint)
	    				.withProtocol(protocol)
	    				.withTopicArn(topicArn);
				SubscribeResult subRes = snsClient.subscribe(subReq);
				getLog().info(String.format("Subscribed with %s/%s%s, status: %d", endpoint, protocol, topicArn, subRes.getSdkHttpMetadata().getHttpStatusCode()));
    		}
    	}
    	
    	// Handle API Proxy Events - Where all resources have an ANY proxy attached to them.
    	getLog().debug("apiProxyEvent: " + apiProxyEvent + "\nAPI Name: " + apiProxyEvent.apiName);
    	if ( apiProxyEvent != null && apiProxyEvent.apiName != null )
    	{
    		for ( String region : regions.split(","))
    		{
	    		getLog().info("Processing API Gateway configuration for " + region);
//	    		apiClient.setRegion(Region.getRegion(Regions.fromName(region)));
	    		AmazonApiGateway apiClient = (AmazonApiGateway) clients.get(region+"-apigw");
	    		
		    	// Now update/create the API Gateway configuration.  We must first figure out if the 
	    		// REST API already exists.
	    		GetRestApisRequest graReq = new GetRestApisRequest();
	    		GetRestApisResult graRes = apiClient.getRestApis(graReq);
	    		RestApi theApi = null;
	    		for ( RestApi api : graRes.getItems() )
	    		{
	    			getLog().info("Found existing REST API: " + api.getName());
	    			if ( apiProxyEvent.apiName.equalsIgnoreCase(api.getName()))
	    				theApi = api;
	    		}
	    		
	    		if ( theApi != null )
	    		{
	    			getLog().debug("Found existing API, updating...");
	    			

//					apiClient.updateRestApi(uraReq);
	    			
	    			// So the "REST API" exists, but that doesn't mean that this resource exists
	    			GetResourcesRequest grReq1 = new GetResourcesRequest()
	    					.withRestApiId(theApi.getId());
	    			GetResourcesResult grRes1 = apiClient.getResources(grReq1);
	    			String baseResourceId = null;
	    			String rootResourceId = null;
	    			for ( Resource resource : grRes1.getItems())
	    			{
	    				getLog().info(String.format("Resource: id=%s, path=%s, pathPart=%s, parentId=%s", resource.getId(), resource.getPath(), resource.getPathPart(), resource.getParentId()));
	    				if ( ("/" + apiProxyEvent.resource).equals(resource.getPath()))
	    				{
	    					baseResourceId = resource.getId();
	    				}
	    				else if ( "/".equals(resource.getPath()))
	    				{
	    					rootResourceId = resource.getId();
	    				}
	    				
//	    				GetIntegrationRequest giReq = new GetIntegrationRequest()
//	    						.withRestApiId(theApi.getId())
//	    						.withHttpMethod("ANY")
//	    						.withResourceId(resource.getId());
//	    				GetIntegrationResult giRes = apiClient.getIntegration(giReq);
	    				Map<String, Method> methods = resource.getResourceMethods();
	    				if ( methods != null )
	    				{
		    				for ( String methodName : methods.keySet())
		    				{
		    					Method method = methods.get(methodName);
		    					Integration integ = method.getMethodIntegration();
		    					if ( methodName.equals("ANY"))
		    					{
		    						getLog().info("ANY method");
		    					}
		    					else
		    					{
				    				getLog().info(String.format("\tMethod %s: authType: %s, authId: %s, httpMethod: %s, intType: %s, intUri: %s\n", 
				    						methodName, method.getAuthorizationType(), method.getAuthorizerId(), 
				    						method.getHttpMethod(), method.getMethodIntegration().getType(), method.getMethodIntegration().getUri()));
		    					}
		    					
		    				}
	    				}
	    			}

//	    			GetResourceRequest baseResourceReq = new GetResourceRequest()
//	    					.withRestApiId(theApi.getId())
//	    					.withResourceId(baseResourceId);
//	    			GetResourceResult baseResourceResp = apiClient.getResource(baseResourceReq);
	    			
	    			// Get the base level resource and delete everything under it
	    			DeleteResourceRequest deleteBaseResourceReq = new DeleteResourceRequest()
	    					.withRestApiId(theApi.getId())
	    					.withResourceId(baseResourceId);
	    			DeleteResourceResult deleteBaseResourceResp = apiClient.deleteResource(deleteBaseResourceReq);
	    			if ( deleteBaseResourceResp.getSdkHttpMetadata().getHttpStatusCode() != 202 )
	    			{
	    				getLog().info("ERROR: Delete Base Resource of " + apiProxyEvent.resource + " failed: " + deleteBaseResourceResp.getSdkHttpMetadata().getHttpStatusCode());
	    			}
	    			
	    			// Call createResource
	    			String parentId = createResource(apiClient, theApi.getId(), 
	    					apiProxyEvent.resource, rootResourceId, region, accountNumber, serviceName);
	    			for ( String subresource : apiProxyEvent.subresources )
	    			{
	    				parentId = createResource(apiClient, theApi.getId(),
	    						subresource, parentId, region, accountNumber, serviceName);
	    			}
	    		}
	    		else
	    		{
	    			getLog().debug("Creating new API...");
	    		}
    		}    		
    	}
        
    	// Now that the function is deployed, figure out what else we need to create
    	if ( apiEvent != null && apiEvent.apiTitle != null )
    	{
    		if ( apiEvent.apiTitle == null )
    		{
    			throw new MojoExecutionException("API Event defined but has no Group Name attribute");
    		}
    		for ( String region : regions.split(","))
    		{
	    		getLog().info("Processing API Gateway configuration for " + region);
//	    		apiClient.setRegion(Region.getRegion(Regions.fromName(region)));
	    		AmazonApiGateway apiClient = (AmazonApiGateway) clients.get(region+"-apigw");
	    		
		    	// Now update/create the API Gateway configuration.  We must first figure out if the 
	    		// REST API already exists.
	    		GetRestApisRequest graReq = new GetRestApisRequest();
	    		GetRestApisResult graRes = apiClient.getRestApis(graReq);
	    		RestApi theApi = null;
	    		for ( RestApi api : graRes.getItems() )
	    		{
	    			getLog().debug("Found existing REST API: " + api.getName());
	    			if ( apiEvent.apiTitle.equalsIgnoreCase(api.getName()))
	    				theApi = api;
	    		}
	    		
    		}
    		
	        /**
	         * Create or Update the API Gateway API.  We should use something like the following for responses:
	         * 	"default":
	         * 		statusCode: "200"
	         * 	"BAD.*":
	         * 		statusCode: "400"
	         * 	"INT.*":
	         * 		statusCode: "500"
	         * 
	         *  The "BAD.*" should match exception classes that will end up returning an HTTP 400.
	         *  The "NOT_FOUND" should match exception classes that will end up returning an HTTP 404.
	         *  The "INT.*" should match exception classes that will end up returning an HTTP 500.
	         *  
	         *  I believe the matching string is based upon the toString() of the Exception class.
	         *  
	         *  When creating the Integration Request, set "Invoke with caller credentials" to true for Authenticated calls.
	         *  
	         *  Integration with API Gateway should have two modes:
	         *  	1. Swagger 2.0: The code must provide and end-point that will generate the Swagger 2.0 definition.  This Swagger definition
	         *  	   will be used to deploy the API in the API Gateway.
	         *  	2. Proxy+: The new API Gateway feature of "proxy+" where everything after the resource name can be left off of the definition.
	         *  	   In this mode, the Lambda is called with a body that contains all query parameters, path parameters, headers, etc to the
	         *  	   Lambda.
	         */
    	}         
        
    }
    
	private boolean hasPathParam ( Operation op )
	{
		boolean ret = false;
		
		List<Parameter> params = op.getParameters();
		for ( Parameter p : params )
		{
			String in = p.getIn();
			
			if( in.equals("path"))
				ret = true;
		}
		
		return ret;
	}
	
	private String getPermString ( Operation op, String pathKey, String opName )
	{
		if ( op == null )
			return null;
		
		String permString = "";
		permString += "/*/" + opName + pathKey;
		
		if ( hasPathParam(op))
		{
			while ( permString.contains("{"))
			{
				int idx1 = permString.indexOf('{');
				int idx2 = permString.indexOf('}', idx1+1);
				String s = permString.substring(1, idx1-1);
				s += "/*";
				s += permString.substring(idx2+1);
				permString = s;
			}
		}
		
		return permString;
	}

	private boolean addPermission(String apiId, String region, String accountNumber, String serviceArn, String serviceName, AWSLambda lambdaClient, String permString)
	{
		boolean ret = false;
		
		if ( ! permString.startsWith("/"))
			permString = "/" + permString;
		
		getLog().info("Adding permission to lambda: " + permString);
    	AddPermissionRequest apReq = new AddPermissionRequest()
    			.withFunctionName(serviceName)
    			.withAction("lambda:InvokeFunction")
//    			.withSourceArn("arn:aws:execute-api:" + region + ":" + accountNumber + ":" + apiId + permString)
    			.withSourceArn("arn:aws:execute-api:" + region + ":" + accountNumber + ":*" + permString)
    			.withPrincipal("apigateway.amazonaws.com")
    			.withStatementId(UUID.randomUUID().toString());
		// Now update the permissions
    	AddPermissionResult apRes = lambdaClient.addPermission(apReq);
    	getLog().info("Statement: " + apRes.getStatement());
    	ret = apRes.getSdkHttpMetadata().getHttpStatusCode() == 201;
		getLog().info("Status: " + apRes.getSdkHttpMetadata().getHttpStatusCode());
    	
		return ret;
	}
	
	private boolean addLambdaPermission(String apiId, String region, String accountNumber, String serviceArn, String serviceName,
			AWSLambda lambdaClient)
	{
		boolean success = false;

		
		ObjectMapper mapper = new ObjectMapper();
		LambdaPolicy policy = null;
		
		try
		{
			GetPolicyRequest getPolicyRequest = new GetPolicyRequest()
					.withFunctionName(serviceName);
			GetPolicyResult gpRes = null;
			try
			{
				gpRes = lambdaClient.getPolicy(getPolicyRequest);
			}
			catch ( Exception ex )
			{
				
			}
			
			if ( gpRes != null )
			{
				String policyStr = gpRes.getPolicy();
	
				policy = mapper.readValue(policyStr, LambdaPolicy.class);
	
				List<Statement> statements = policy.getStatement();
				RemovePermissionRequest rpReq = new RemovePermissionRequest();
				RemovePermissionResult rpRes = null;
				for ( Statement s : statements )
				{
					getLog().info("Removing permission statement: " + s.getSid());
					getLog().info("Action: " + s.getAction());
					getLog().info("Effect: " + s.getEffect());
					getLog().info("Resource: " + s.getResource());
					getLog().info("Condition: " + s.getCondition().getArnLike().getAWSSourceArn());
					getLog().info("Condition: " + s.getCondition().getAdditionalProperties());
					getLog().info("Principal: " + s.getPrincipal().getService());
					getLog().info("Principal: " + s.getPrincipal().getAdditionalProperties());
					getLog().info("Additional Properties: " + s.getAdditionalProperties().toString());
					rpReq.setFunctionName(serviceName);
					rpReq.setStatementId(s.getSid());
					rpRes = lambdaClient.removePermission(rpReq);
					getLog().info("Status: " + rpRes.getSdkHttpMetadata().getHttpStatusCode());
				}
			}

			String swaggerFile = project.getBasedir().getCanonicalPath() + "/target/jaxrs-analyzer/swagger.json";
			if ( ! new File(swaggerFile).exists() )
			{
				System.out.println("Can't find swagger file: " + swaggerFile);
				return true;
			}
			
			Swagger swagger = new SwaggerParser().read(swaggerFile);
			Map<String, Path> paths = swagger.getPaths();
			for ( String pathKey : paths.keySet() )
			{
				getLog().info("Processing path: " + pathKey);
				Path p = paths.get(pathKey);
				String permString = null;
	
				permString = getPermString(p.getPost(), pathKey, "POST");
				if ( permString != null ) addPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient, permString);
				permString = getPermString(p.getOptions(), pathKey, "OPTIONS");
				if ( permString != null ) addPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient, permString);
				permString = getPermString(p.getDelete(), pathKey, "DELETE");
				if ( permString != null ) addPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient, permString);
				permString = getPermString(p.getGet(), pathKey, "GET");
				if ( permString != null ) addPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient, permString);
				permString = getPermString(p.getHead(), pathKey, "HEAD");
				if ( permString != null ) addPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient, permString);
				permString = getPermString(p.getPut(), pathKey, "PUT");
				if ( permString != null ) addPermission(apiId, region, accountNumber, serviceArn, serviceName, lambdaClient, permString);
	
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return success;
	}

	private void sleep(int seconds)
    {
    	try
    	{
    		Thread.sleep(seconds*1000);
    	}
    	catch ( Exception ex ) {}
    }
    
    private class UploadProgressListener implements ProgressListener
    {
		private long contentLength = 0L;
		private long contentSent = 0L;
		private int lastTenPct = 0;
		
		@Override
		public void progressChanged(ProgressEvent event)
		{
			if ( event.getEventType() == ProgressEventType.REQUEST_CONTENT_LENGTH_EVENT )
			{
				contentLength = event.getBytes();
				getLog().info("Content size: " + contentLength + " bytes");
			}
			else if ( event.getEventType() == ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT )
			{
				contentSent += event.getBytesTransferred();
				double div = (double) (((double)contentSent/(double)contentLength));
				double mul = div*(double)100.0;
				int mod = (int)mul / 10;
				if ( mod > lastTenPct )
				{
					lastTenPct = mod;
					getLog().info("Uploaded " + (mod*10) + "% of " + (contentLength/(1024*1024)) + " MB");
				}
			}
		}
    }
    
    private String createResource(AmazonApiGateway apiClient, 
    		String apiId, String resource, String parentId,
    		String region, String accountNumber, String serviceName)
    {
    	// Show some info
    	getLog().info("Creating Resource " + resource);
		// Now create the resources and methods
		CreateResourceRequest crReq = null;
		CreateResourceResult crRes = null;
		crReq = new CreateResourceRequest()
				.withParentId(parentId)
				.withPathPart(resource)
				.withRestApiId(apiId);
		crRes = apiClient.createResource(crReq);
		if ( crRes.getSdkHttpMetadata().getHttpStatusCode() != 201 )
		{
			getLog().info("ERROR: Create Resource failed: " + crRes.getSdkHttpMetadata().getHttpStatusCode());
		}
		getLog().info("Created new Resource for " + apiProxyEvent.resource + ", id: " + crRes.getId());
		
		// Now create the method

		String uri = "arn:aws:apigateway:" + region + ":lambda:path/2015-03-31/functions/arn:aws:lambda:" 
				+ region + ":" + accountNumber + ":function:" + serviceName + "/invocations";
		Method m = new Method();

		Integration integ = null;
//		integ.setType("LAMBDA_PROXY");
//		integ.set
//		m.setMethodIntegration(integ);
//		CreateResourceResult xyz = crRes.addResourceMethodsEntry("ANY", m);
//		getLog().info("AddResourceMethodsEntry result: " + xyz.getSdkHttpMetadata().getHttpStatusCode());
		
		PutMethodRequest pmReq = new PutMethodRequest()
				.withRestApiId(apiId)
				.withResourceId(crRes.getId())
				.withHttpMethod("ANY")
				.withAuthorizationType("NONE");
		PutMethodResult pmRes = apiClient.putMethod(pmReq);
		getLog().info("PutMethod result: " + pmRes.getSdkHttpMetadata().getHttpStatusCode());
		if ( pmRes.getSdkHttpMetadata().getHttpStatusCode() != 201 )
		{
			getLog().info("ERROR: Put Method failed: " + pmRes.getSdkHttpMetadata().getHttpStatusCode());
		}
		getLog().info("Created new Method ANY for ResourceId: " + crRes.getId());
		
		PutIntegrationRequest piReq = new PutIntegrationRequest()
				.withContentHandling(ContentHandlingStrategy.CONVERT_TO_TEXT)
				.withHttpMethod("ANY")
				.withRestApiId(apiId)
				.withResourceId(crRes.getId())
				.withPassthroughBehavior("WHEN_NO_MATCH")
				.withType(IntegrationType.AWS_PROXY)
				.withUri(uri);
		piReq.setIntegrationHttpMethod("POST");
//		piReq.setCredentials(credentials);
		PutIntegrationResult piRes = apiClient.putIntegration(piReq);
		if ( piRes.getSdkHttpMetadata().getHttpStatusCode() != 201 )
		{
			getLog().info("ERROR: Put Integration failed: " + piRes.getSdkHttpMetadata().getHttpStatusCode());
		}
		getLog().info("Created new Integration for ResourceId: " + crRes.getId());
		
		IntegrationResponse integResponse = new IntegrationResponse();
		Map<String, String> responseTemplates = new HashMap<>();
		responseTemplates.put("application/json", null);
		integResponse.setResponseTemplates(responseTemplates);
		piRes = piRes.addIntegrationResponsesEntry("200", integResponse);

		// Now do the Method Response
		
		Map<String, String> responseModels = new HashMap<>();
		responseModels.put("application/json", "Empty");
		PutMethodResponseRequest pmrReq = new PutMethodResponseRequest()
				.withRestApiId(apiId)
				.withResourceId(crRes.getId())
				.withHttpMethod("ANY")
				.withStatusCode("200")
				.withResponseModels(responseModels);
		PutMethodResponseResult pmrRes = apiClient.putMethodResponse(pmrReq);
		if ( pmrRes.getSdkHttpMetadata().getHttpStatusCode() != 201 )
		{
			getLog().info("ERROR: Put Method Response failed: " + pmrRes.getSdkHttpMetadata().getHttpStatusCode());
		}
		getLog().info("Created new Method Response for ResourceId: " + crRes.getId());
		int x = 0;
		
		return crRes.getId();

    }
        
}
