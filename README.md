# AWS Lambda Java Serverless Maven Plugin [![Build Status](https://travis-ci.org/ServerlessJavaMaven/ServerlessJavaMaven.svg)](https://travis-ci.org/ServerlessJavaMaven/ServerlessJavaMaven)

This plugin provides a framework for managing Java code to be deployed into AWS' Lambda environment.

The plugin will (when complete) allow you to configure how and where the Lambda is deployed and what events it handles.  Those events
can be:
* API Gateway
* DynamoDB stream
* IoT rule Action
* S3 Event
* Cloudwatch Events and Logs
* CodeCommit
* Cognito Sync Trigger
* Kinesis
* SNS Events

The plugin will use the Maven Shade plugin to collect all of your Maven dependencies into one Jar for deployment to Lambda.
It will create or update IAM Policies as needed and give you some options on how to configure API Gateway to invoke the Lambda.

If anybody would like to contribute to the development of this, please contact me through GitHub or at david@mobile-360.com.

These plugins are built on top of [API v3](http://developer.github.com/) through the
[GitHub Java library](https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core).

(TBD) Released builds are available from [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ccom.mcdaniel.serverless).

## Core Configuration

The plugins support several configuration options that can be expressed
in your project's POM file. Where you put the
plugin settings depends on whether you want a specific setting to be configured
globally or on a per-project basis.

The notation below shows the plugin configuration property name followed
by the settings configuration property in parentheses.

 * `environment`
 	* The environment (dev, prod, stage, test, dummy, frank, ...) being deployed.
 * `regions`
 	* Which region(s) the Lambda should be deployed into.  Uses standard AWS region ids (us-west-2, us-east-2, etc).
 * `unauthenticatedRole`
 	* The IAM role to be granted when this Lambda is called from API Gateway and the caller is not authenticated (yet).
 * `description`
 	* Description of the function.
 * `serviceName`
 	* The name of the service.
 * uploadJarBucket`
 	* The bucket name specifying the upload location of the Lambda.
 * `permissions`
 	* Holder for a list of permissions to be given to the assumed role of the Lambda.
 * `permission`
 	* Defines a permission to be granted to the Lambda
 * `effect`
 	* Defines the effect of this permission.  Must be Allow or Deny.
 * `actions`
 	* Defines the list of actions to be effected by this permission set.
 * `action`
 	* Defines an action effected by this permission set.  Exmaple: logs:CreateLogStream
 * `resources`
 	* Defines the list of resources to be effected by this permission set.
 * `resource`
 	* Defines a resource effected by this permission set.  Exmaple: arn:aws:dynamodb:us-west-2:table/user
* `name`
	* The name of the Lambda.
* `apiEvent`
	* Defines that this Lambda should be invoked by API Gateway.  The following items define attributes specific to Lambdas to be called by API Gateway.
* `apiGroupName`
	* This is the name of the "REST API" within the API Gateway Console.
 * `customDomainName`
 	* Custom domain name to be used within the API Gateway.
 * `handlerMethod`
 	* The handler method within the project, using AWS notation: <fully-qualified-package-name>::<name-of-java-method>
* `requestParameters`
	* A List of field mappings between the API Gateway Method Request and the Integration Request.  See: http://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-method-settings-execution-console.html
* `responseParameters`
	* A List of field mappings between the API Gateway Method Response and the Integration Response.
* `cors`
	* Defines if CORS (Cross-Origin Resource Sharing) is enabled.  Must be true or false.
* `protocol`
	* Defines the protocol that is used by this resource.  Must be http or https.
* `swaggerUri`
	* Defines the URI at which to retrieve the Swagger 2.0 definition of the API.  If this is defined, the plugin will use the Swagger
	* output to define the interface of the Lambda and API Gateway resource.

* `dynamoDBEvent`
	* (TBD) Defines the parameters to be used in setting up this Lambda as an event listener to DynamoDB Streams.
* `iotAction`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS IoT Rule Action messages.
* `s3Event`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS S3 event notifications.
* `cloudWatchEvent`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS Cloud Watch (Scheduled) event notifications.
* `cloudWatchLogEvent`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS Cloud Watch Log event notifications.
* `codeCommit`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS CodeCommit event notifications.
* `cognitoTrigger`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS Cognito Sync Trigger event notifications.
* `kinesis`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS Kinesis event notifications.
* `snsEvent`
	* (TBD) Defines the parameters to be used in setting up this Lambda to receive AWS SNS event notifications.

# License
* [MIT License](http://www.opensource.org/licenses/mit-license.php)
