package com.mcdaniel.serverless;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.GetResourcesRequest;
import com.amazonaws.services.apigateway.model.GetResourcesResult;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.Op;
import com.amazonaws.services.apigateway.model.PatchOperation;
import com.amazonaws.services.apigateway.model.Resource;
import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.apigateway.model.UpdateRestApiRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.AttachRolePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreatePolicyRequest;
import com.amazonaws.services.identitymanagement.model.CreatePolicyResult;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.CreateRoleResult;
import com.amazonaws.services.identitymanagement.model.DeletePolicyRequest;
import com.amazonaws.services.identitymanagement.model.DetachRolePolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.identitymanagement.model.GetRoleResult;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.CreateAliasRequest;
import com.amazonaws.services.lambda.model.CreateAliasResult;
import com.amazonaws.services.lambda.model.CreateFunctionRequest;
import com.amazonaws.services.lambda.model.CreateFunctionResult;
import com.amazonaws.services.lambda.model.FunctionCode;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.PublishVersionRequest;
import com.amazonaws.services.lambda.model.PublishVersionResult;
import com.amazonaws.services.lambda.model.UpdateAliasRequest;
import com.amazonaws.services.lambda.model.UpdateAliasResult;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.common.base.Strings;
 
/**
 * Says "Hi" to the user.
 *
 */
@Mojo( name = "deploy", defaultPhase=LifecyclePhase.DEPLOY, requiresOnline=true, requiresProject=true)
public class ServerlessMojo extends AbstractMojo
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
	
	@Parameter(property="description", required=false)
	protected String description;
	
	@Parameter(property="AWSAccessKey", required=true)
	protected String AWSAccessKey;
	
	@Parameter(property="AWSSecretKey", required=true)
	protected String AWSSecretKey;
	
	@Parameter(property="permissions", required=false)
	protected List<Permission> permissions;
	
	@Parameter( defaultValue = "${project}", readonly = true )
	protected MavenProject project; 
	
	@Parameter(property="name", required=true)
	protected String name;
	
	@Parameter(property="dynamoEvent", required=false)
	protected DynamoEvent dynamoEvent;
	
	@Parameter(property="s3Event", required=false)
	protected S3Event s3Event;
	
	@Parameter(property="scheduleEvent", required=false)
	protected ScheduleEvent scheduleEvent;
	
	@Parameter(property="snsEvent", required=false)
	protected SNSEvent snsEvent;
	
	public void execute() throws MojoExecutionException
    {
    	String rolePolicy = null;

    	getLog().info( "Hello, world." );
        getLog().info("Logger class: " + getLog().getClass().getName());
        
        if ( project != null )
        {
        	getLog().info("Project NOT NULL");
        	getLog().info("Name/Packaging: " + project.getName() + " / " + project.getPackaging());
        	getLog().info("Artifact name: " + project.getArtifact().getFile().getName());
        }
        // Create the credentials for all of the clients.
        getLog().info("AccessKey: " + AWSAccessKey);
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWSAccessKey, AWSSecretKey);
        
        // Setup needed variables
        String assumedRoleName = serviceName + "_AssumedRole";
        
        // Create all of the needed clients.
        getLog().info("Getting clients...");
        AWSLambdaClient lambdaClient = new AWSLambdaClient(awsCredentials);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);
        AmazonIdentityManagementClient iamClient = new AmazonIdentityManagementClient(awsCredentials);
        AmazonApiGatewayClient apiClient = new AmazonApiGatewayClient(awsCredentials);
        
        getLog().info("Done.");
        
        //
        // Get the account number.
        //
        GetUserResult guRes = iamClient.getUser();
        String[] accountArn = guRes.getUser().getArn().split(":");
        getLog().info("Account Number: " + accountArn[4]);
        String accountNumber = accountArn[4];
        
        
        //
        // Upload the jar to S3
        //
        HashMap<String,Bucket> uploadBucket = new HashMap<>();
        for ( String region : regions.split(","))
        {
            String uploadJarBucketName = serviceName + "." + environment + "." + region + "." + uploadJarBucket;
            uploadJarBucketName = uploadJarBucketName.toLowerCase();
	        s3Client.setRegion(Region.getRegion(Regions.fromName(region)));
	    	if ( !s3Client.doesBucketExist(uploadJarBucketName.toLowerCase()))	// If bucket doesn't exist, create it.
	    	{
	    		getLog().info("Creating bucket " + uploadJarBucketName);
	    		uploadBucket.put(region, s3Client.createBucket(uploadJarBucketName));
	    	}
	
	    	getLog().info("Uploading Jar in " + region + "...");
	    	PutObjectRequest poReq = new PutObjectRequest(uploadJarBucketName, project.getArtifact().getFile().getName(), 
	    			project.getArtifact().getFile());
	    	poReq.setGeneralProgressListener(new UploadProgressListener());
	    	PutObjectResult poRes = null;
	    	
	    	try
	    	{
	    		poRes = s3Client.putObject(poReq);
	    	}
	    	catch ( Exception ex )
	    	{
	    		ex.printStackTrace();
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
	    		getLog().info("grRes == null");
	    	else
	    	{
	    		getLog().info("Status code: " + grRes.getSdkHttpMetadata().getHttpStatusCode());
	    		getLog().info("grRes != null, Arn: " + grRes.getRole().getArn());
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
    		try
    		{
    			iamClient.detachRolePolicy(drpReq);
    		} catch ( Exception ex ) {}
    		getLog().info("Deleting policy " + policyArn);
    		DeletePolicyRequest dpReq = new DeletePolicyRequest()
    				.withPolicyArn(policyArn);
    		try
    		{
    			iamClient.deletePolicy(dpReq);
    		} catch ( Exception ex ) {}
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

    	String[] regionList = regions.split(",");
    	for ( int i = 0; i < regionList.length; i ++ )
    	{
    		String region = regionList[i];
    		String comma = i == regionList.length-1 ? "" : ",";	// If we're at the end, comma="", else comma=","
    		
    		rolePolicy += String.format( 
				"        {\n" +
				"            \"Effect\": \"Allow\",\n" +
				"            \"Action\": \"logs:CreateLogGroup\",\n" +
				"            \"Resource\": \"arn:aws:logs:%s:%s:*\"\n" +
				"        },\n" +
				"        {\n" +
				"            \"Effect\": \"Allow\",\n" +
				"            \"Action\": [\n" +
				"                \"logs:CreateLogStream\",\n" +
				"                \"logs:PutLogEvents\"\n" +
				"            ],\n" +
				"            \"Resource\": [\n" +
				"                \"arn:aws:logs:%s:%s:log-group:/aws/lambda/%s:%s\"\n" +
				"            ]\n" +
				"        }%s\n", region, accountNumber, region, accountNumber, serviceName, environment, comma);
    	}
    	rolePolicy += 
			"    ]\n" +
			"}\n";
    	
    	// First update the variables
//    	rolePolicy = rolePolicy.replace("$regions$", regions);
//    	rolePolicy = rolePolicy.replace("$accountId$", accountNumber);
//    	rolePolicy = rolePolicy.replace("$serviceName$", serviceName);
//    	rolePolicy = rolePolicy.replace("$environment$", environment);
    	
		String policyName = serviceName + "_AssumedPolicy";
		getLog().info("Creating custom policy: " + policyName);
		CreatePolicyRequest cpReq = new CreatePolicyRequest()
				.withPolicyDocument(rolePolicy)
				.withPolicyName(policyName);
		CreatePolicyResult cpRes = null;
		try
		{
			cpRes = iamClient.createPolicy(cpReq);
		}
		catch ( Exception ex )
		{
			getLog().error("Caught exception creating the Assume Policy: " + rolePolicy);
			ex.printStackTrace();
			return;
		}
		
		// Attach the custom policy
		getLog().info("Attaching Policy to Role...");
		AttachRolePolicyRequest arpReq = new AttachRolePolicyRequest()
				.withRoleName(assumedRoleName)
				.withPolicyArn(cpRes.getPolicy().getArn());
		AttachRolePolicyResult arpRes = iamClient.attachRolePolicy(arpReq);
    	
    	//
    	// Upload the function
    	//
    	for ( String region : regions.split(",") )
    	{
    		getLog().debug("Processing for Region: " + region);
    		
    		// Set the client region
        	lambdaClient.setRegion(Region.getRegion(Regions.fromName(region)));
        	s3Client.setRegion(Region.getRegion(Regions.fromName(region)));
        	apiClient.setRegion(Region.getRegion(Regions.fromName(region)));
            String uploadJarBucketName = serviceName + "." + environment + "." + region + "." + uploadJarBucket;
            uploadJarBucketName = uploadJarBucketName.toLowerCase();
            
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
	
	        // Create or update the function
	        String serviceArn = null;
	        if ( !updateFunction )
	        {
	        	getLog().info("Creating function!");
	            CreateFunctionRequest cfReq = new CreateFunctionRequest();
	        	cfReq.setFunctionName(serviceName);
	        	cfReq.setPublish(true);
	        	cfReq.setHandler(handlerMethod);
	        	FunctionCode code = new FunctionCode();
	        	code.setS3Bucket(uploadJarBucketName);
	        	code.setS3Key(project.getArtifact().getFile().getName());
	        	cfReq.setCode(code);
	        	cfReq.setRole(roleArn);
	        	cfReq.setRuntime("java8");
	        	if ( ! Strings.isNullOrEmpty(description))
	        		cfReq.setDescription(description);
				getLog().info("Trying Function create");
	        	while ( true )
	        	{
	        		try
	        		{
			        	CreateFunctionResult cfRes = lambdaClient.createFunction(cfReq);
			        	serviceArn = cfRes.getFunctionArn();
			        	getLog().info("Function Arn: " + cfRes.getFunctionArn());
			        	break;
	        		}
	        		catch ( com.amazonaws.services.lambda.model.InvalidParameterValueException ipve )
	        		{
	        			getLog().debug("Caught ipve, sleeping...");
	        			try
	        			{
	        				Thread.sleep(5000);
	        			}
	        			catch ( Exception ex ) {}
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
	        getLog().debug("Published version: " + pubVersion);
	        
	        // Setup the alias
	        String aliasId = "";
	        UpdateAliasRequest uaReq = new UpdateAliasRequest()
	        		.withName(environment)
	        		.withDescription(environment + " Version")
	        		.withFunctionName(serviceName)
	        		.withFunctionVersion(pubVersion);
	        UpdateAliasResult uaRes = null;
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
	        }
    	}
        
    	// Now that the function is deployed, figure out what else we need to create
    	if ( apiEvent != null )
    	{
    		if ( apiEvent.apiGroupName == null )
    		{
    			throw new MojoExecutionException("API Event defined but has no Group Name attribute");
    		}
    		for ( String region : regions.split(","))
    		{
	    		getLog().info("Processing API Gateway configuration for " + region);
	    		apiClient.setRegion(Region.getRegion(Regions.fromName(region)));
	    		
		    	// Now update/create the API Gateway configuration.  We must first figure out if the 
	    		// REST API already exists.
	    		GetRestApisRequest graReq = new GetRestApisRequest();
	    		GetRestApisResult graRes = apiClient.getRestApis(graReq);
	    		RestApi theApi = null;
	    		for ( RestApi api : graRes.getItems() )
	    		{
	    			getLog().debug("Found existing REST API: " + api.getName());
	    			if ( apiEvent.apiGroupName.equalsIgnoreCase(api.getName()))
	    				theApi = api;
	    		}
	    		
	    		if ( theApi != null )
	    		{
	    			// So the "REST API" exists, but that doesn't mean that this resource exists
	    			GetResourcesRequest grReq1 = new GetResourcesRequest()
	    					.withRestApiId(theApi.getId());
	    			GetResourcesResult grRes1 = apiClient.getResources(grReq1);
	    			for ( Resource resource : grRes1.getItems())
	    			{
	    				getLog().debug(String.format("Resource: id=%s, path=%s, pathPart=%s, parentId=%s", resource.getId(), resource.getPath(), resource.getPathPart(), resource.getParentId()));
	    			}
	    			PatchOperation patOp = new PatchOperation()
	    					.withOp(Op.Replace);
			    	UpdateRestApiRequest uraReq = new UpdateRestApiRequest()
			    			.withRestApiId(theApi.getId())
			    			.withPatchOperations(patOp);
					apiClient.updateRestApi(uraReq);
	    		}
	    		else
	    		{
	    			
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
				getLog().info("Content size: " + contentLength);
			}
			else if ( event.getEventType() == ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT )
			{
				contentSent += event.getBytesTransferred();
				double div = (double) (((double)contentSent/(double)contentLength));
				double mul = div*(double)100.0;
				int mod = (int)mul / 10;
				if ( mod > lastTenPct )
				{
					getLog().info("Uploaded " + mod*10 + "% of " + contentLength + " bytes.");
					lastTenPct = mod;
				}
			}
		}
    }
}
