/**
 * 
 */
package com.mcdaniel.serverless.tests;

import java.util.List;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.GetResourcesRequest;
import com.amazonaws.services.apigateway.model.GetResourcesResult;
import com.amazonaws.services.apigateway.model.GetRestApiRequest;
import com.amazonaws.services.apigateway.model.GetRestApiResult;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.ImportRestApiRequest;
import com.amazonaws.services.apigateway.model.Resource;
import com.amazonaws.services.apigateway.model.RestApi;

/**
 * @author dmcdaniel
 *
 */
public class Dummy
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String AWSAccessKey = "AKIAJM7YMMLXKUH5EWRQ";
		String AWSSecretKey = "u1Ri5WQBn6lnYw2tKlLKL7puYsIaJAfupvR2yaRA";
		
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWSAccessKey, AWSSecretKey);
        
        AmazonApiGatewayClient apiClient = new AmazonApiGatewayClient(awsCredentials);
    	apiClient.setRegion(Region.getRegion(Regions.US_WEST_2));
    	
        GetRestApisRequest graReq = new GetRestApisRequest();
        GetRestApisResult graRes = apiClient.getRestApis(graReq);
        List<RestApi> apis = graRes.getItems();
        for ( RestApi r : apis )
        {
        	System.out.println(String.format("API Id: %s, Name: %s, Desc: %s", r.getId(), r.getName(), r.getDescription()));
        }
        
//        ImportRestApiRequest iraReq = new ImportRestApiRequest();
//        apiClient.importRestApi(iraReq);
        
        GetResourcesRequest grReq = new GetResourcesRequest()
        		.withRestApiId("y8m4qszor5");
        GetResourcesResult grRes = apiClient.getResources(grReq);
        
        List<Resource> resources = grRes.getItems();
        for ( Resource r : resources )
        {
        	System.out.println(String.format("ResourceId: %s, ParentId: %s, Path: %s, PathPart: %s", r.getId(), r.getParentId(), r.getPath(), r.getPathPart()));
        }

	}

}
