package com.mcdaniel.serverless;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGatewayClient;
import com.amazonaws.services.apigateway.model.GetExportRequest;
import com.amazonaws.services.apigateway.model.GetExportResult;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.Op;
import com.amazonaws.services.apigateway.model.PatchOperation;
import com.amazonaws.services.apigateway.model.RestApi;

public class Tester
{
	public static void main(String []args)
	{
		Tester t = new Tester();
		t.run();
	}
	
	public void run()
	{
		String AWSAccessKey="AKIAJM7YMMLXKUH5EWRQ";
		String AWSSecretKey="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";
		
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWSAccessKey, AWSSecretKey);

        AmazonApiGatewayClient apiClient = new AmazonApiGatewayClient(awsCredentials);
        apiClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        
        GetRestApisRequest graReq = new GetRestApisRequest();
		GetRestApisResult graRes = apiClient.getRestApis(graReq);
		RestApi theApi = null;
		for ( RestApi api : graRes.getItems() )
		{
			System.out.println("Found existing REST API: " + api.getName());
			if ( "TestAPI".equals(api.getName()))
			{
				theApi = api;
				GetExportRequest geReq = new GetExportRequest()
						.withRestApiId(api.getId())
						.withExportType("swagger")
						.withStageName("TEST");
				GetExportResult geRes = apiClient.getExport(geReq);
				String f = new String(geRes.getBody().array());
				int x = 1;
				String from = "";
				PatchOperation po = new PatchOperation()
						.withFrom(from)
						.withOp(Op.Replace)
						.withPath("");
			}
		}

	}
}
