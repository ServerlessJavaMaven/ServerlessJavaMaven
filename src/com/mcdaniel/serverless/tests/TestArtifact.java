/**
 * 
 */
package com.mcdaniel.serverless.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * @author dmcdaniel
 *
 */
public class TestArtifact implements Artifact
{

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Artifact arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#addMetadata(org.apache.maven.artifact.metadata.ArtifactMetadata)
	 */
	@Override
	public void addMetadata(ArtifactMetadata arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getArtifactHandler()
	 */
	@Override
	public ArtifactHandler getArtifactHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getArtifactId()
	 */
	@Override
	public String getArtifactId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getAvailableVersions()
	 */
	@Override
	public List<ArtifactVersion> getAvailableVersions()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getBaseVersion()
	 */
	@Override
	public String getBaseVersion()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getClassifier()
	 */
	@Override
	public String getClassifier()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getDependencyConflictId()
	 */
	@Override
	public String getDependencyConflictId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getDependencyFilter()
	 */
	@Override
	public ArtifactFilter getDependencyFilter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getDependencyTrail()
	 */
	@Override
	public List<String> getDependencyTrail()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getDownloadUrl()
	 */
	@Override
	public String getDownloadUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getFile()
	 */
	@Override
	public File getFile()
	{
		File f =new File("target\\TemplateAPIProject-0.0.1-SNAPSHOT.jar"); 
		return f;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getGroupId()
	 */
	@Override
	public String getGroupId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getId()
	 */
	@Override
	public String getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getMetadataList()
	 */
	@Override
	public Collection<ArtifactMetadata> getMetadataList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getRepository()
	 */
	@Override
	public ArtifactRepository getRepository()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getScope()
	 */
	@Override
	public String getScope()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getSelectedVersion()
	 */
	@Override
	public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getType()
	 */
	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getVersion()
	 */
	@Override
	public String getVersion()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#getVersionRange()
	 */
	@Override
	public VersionRange getVersionRange()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#hasClassifier()
	 */
	@Override
	public boolean hasClassifier()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#isOptional()
	 */
	@Override
	public boolean isOptional()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#isRelease()
	 */
	@Override
	public boolean isRelease()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#isResolved()
	 */
	@Override
	public boolean isResolved()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#isSelectedVersionKnown()
	 */
	@Override
	public boolean isSelectedVersionKnown() throws OverConstrainedVersionException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#isSnapshot()
	 */
	@Override
	public boolean isSnapshot()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#selectVersion(java.lang.String)
	 */
	@Override
	public void selectVersion(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setArtifactHandler(org.apache.maven.artifact.handler.ArtifactHandler)
	 */
	@Override
	public void setArtifactHandler(ArtifactHandler arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setArtifactId(java.lang.String)
	 */
	@Override
	public void setArtifactId(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setAvailableVersions(java.util.List)
	 */
	@Override
	public void setAvailableVersions(List<ArtifactVersion> arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setBaseVersion(java.lang.String)
	 */
	@Override
	public void setBaseVersion(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setDependencyFilter(org.apache.maven.artifact.resolver.filter.ArtifactFilter)
	 */
	@Override
	public void setDependencyFilter(ArtifactFilter arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setDependencyTrail(java.util.List)
	 */
	@Override
	public void setDependencyTrail(List<String> arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setDownloadUrl(java.lang.String)
	 */
	@Override
	public void setDownloadUrl(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setFile(java.io.File)
	 */
	@Override
	public void setFile(File arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setGroupId(java.lang.String)
	 */
	@Override
	public void setGroupId(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setOptional(boolean)
	 */
	@Override
	public void setOptional(boolean arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setRelease(boolean)
	 */
	@Override
	public void setRelease(boolean arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setRepository(org.apache.maven.artifact.repository.ArtifactRepository)
	 */
	@Override
	public void setRepository(ArtifactRepository arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setResolved(boolean)
	 */
	@Override
	public void setResolved(boolean arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setResolvedVersion(java.lang.String)
	 */
	@Override
	public void setResolvedVersion(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setScope(java.lang.String)
	 */
	@Override
	public void setScope(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setVersion(java.lang.String)
	 */
	@Override
	public void setVersion(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#setVersionRange(org.apache.maven.artifact.versioning.VersionRange)
	 */
	@Override
	public void setVersionRange(VersionRange arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.maven.artifact.Artifact#updateVersion(java.lang.String, org.apache.maven.artifact.repository.ArtifactRepository)
	 */
	@Override
	public void updateVersion(String arg0, ArtifactRepository arg1)
	{
		// TODO Auto-generated method stub

	}

}
