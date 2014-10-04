package com.vhelium.lotig;

import java.util.EventObject;
import android.content.Intent;

public class EventActivityResult extends EventObject
{
	private static final long serialVersionUID = 1L;
	private final int requestCode;
	private final int resultCode;
	private final Intent data;
	
	public EventActivityResult(Object source, int requestCode, int resultCode, Intent data)
	{
		super(source);
		this.requestCode = requestCode;
		this.resultCode = resultCode;
		this.data = data;
	}
	
	public int getRequestCode()
	{
		return requestCode;
	}
	
	public int getResultCode()
	{
		return resultCode;
	}
	
	public Intent getData()
	{
		return data;
	}
}
