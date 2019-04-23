package com.mcdaniel.serverless;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class BaseServerlessMojo extends AbstractMojo
{
	@Parameter(property="serviceName", required=true)
	protected String serviceName;
	
	@Parameter(property="environment", required=true)
	/** This is used for the         
	 *   The "BAD.*" should match exception classes that will end up returning an HTTP 400., under the API itself, and the Base Path for the mapping between the Custom Domain Name
	 * and the API definition.
	 */
	protected String environment;

	@Parameter(property="regions", required=true)
	protected String regions;

	@Parameter(property="policyRegions", required=false)
	protected String policyRegions;

	@Parameter(property="concurrencyLimit", required = false, defaultValue = "0")
	protected int concurrencyLimit;

	@Parameter(property="basedir", required=true)
	protected File basedir;

	@Parameter(property="targetVPC", required=false)
	protected SJMVpcConfig targetVPC;

	@Parameter(property="uploadJarBucket", required=true)
	protected String uploadJarBucket;
	
	@Parameter(property="description", required=false)
	protected String description;
	
	@Parameter(property="permissions", required=false)
	protected List<Permission> permissions;
	
	@Parameter( defaultValue = "${project}", readonly = true )
	protected MavenProject project; 
	
//	@Parameter(property="name", required=true)
//	protected String name;
	
	public BaseServerlessMojo()
	{
		// TODO Auto-generated constructor stub
	}

}
