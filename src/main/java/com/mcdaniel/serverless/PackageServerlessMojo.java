/**
 * 
 */
package com.mcdaniel.serverless;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.amazonaws.auth.BasicAWSCredentials;

/**
 * @author dmcdaniel
 *
 */
@Mojo( name = "package", defaultPhase=LifecyclePhase.PACKAGE, requiresOnline=true, requiresProject=true)
public class PackageServerlessMojo extends BaseServerlessMojo
{

	/**
	 * 
	 */
	public PackageServerlessMojo()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{

    	getLog().info( "Hello, world." );
        getLog().info("Logger class: " + getLog().getClass().getName());
        
        if ( project != null )
        {
        	getLog().info("Project NOT NULL");
        	getLog().info("Name/Packaging: " + project.getName() + " / " + project.getPackaging());
        	getLog().info("Artifact name: " + project.getArtifact().getFile().getName());
        }
        // Create the credentials for all of the clients.
//        getLog().info("AccessKey: " + AWSAccessKey);
//        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWSAccessKey, AWSSecretKey);


	}

}
