package com.mcdaniel.serverless;

import java.util.List;

public class Permission
{

	protected String effect;
	protected List<String> resources;
	protected List<String> actions;

	@Override
	public String toString()
	{
		return "Permission{" +
				"effect='" + effect + '\'' +
				", resources=" + resources +
				", actions=" + actions +
				'}';
	}
}
