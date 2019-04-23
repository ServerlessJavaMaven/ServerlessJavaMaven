package com.mcdaniel.serverless.tests;

import org.apache.maven.plugin.logging.Log;

public class TestLog implements Log
{

	@Override
	public void debug(CharSequence arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void debug(Throwable arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void debug(CharSequence arg0, Throwable arg1)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void error(CharSequence arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void error(Throwable arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void error(CharSequence arg0, Throwable arg1)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void info(CharSequence arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void info(Throwable arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void info(CharSequence arg0, Throwable arg1)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return true;
	}

	@Override
	public boolean isErrorEnabled()
	{
		return true;
	}

	@Override
	public boolean isInfoEnabled()
	{
		return true;
	}

	@Override
	public boolean isWarnEnabled()
	{
		return true;
	}

	@Override
	public void warn(CharSequence arg0)
	{
		System.out.println("[DEBUG] " + arg0);
	}

	@Override
	public void warn(Throwable arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(CharSequence arg0, Throwable arg1)
	{
		System.out.println("[DEBUG] " + arg0);
	}

}
