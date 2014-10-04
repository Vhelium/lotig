package com.vhelium.lotig;

import java.util.HashMap;

public class EventResult
{
	public static final int SERVER_LIST_WLAN_RESULT_CODE = 200;
	public static final int SERVER_LIST_BT_RESULT_CODE = 100;
	public static final int REQUEST_ENABLE_BT = 101;
	public static final int REQUEST_TURN_ON_DISCOVERABLE = 6969;
	
	public static final int RESULT_OK = -1;
	public static final int RESULT_CANCELED = 0;
	
	public static final int DISCOVERABLE_DURATION = 120;
	
	private final int requestCode;
	private final int resultCode;
	private final Object dataObject;
	private final HashMap<String, Object> data;
	
	public EventResult(int requestCode, int resultCode, Object dataObject)
	{
		this.requestCode = requestCode;
		this.resultCode = resultCode;
		this.dataObject = dataObject;
		data = new HashMap<String, Object>();
	}
	
	public int getRequestCode()
	{
		return requestCode;
	}
	
	public int getResultCode()
	{
		return resultCode;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getDataExtra(String name, Class<T> type)
	{
		if(data.containsKey(name) && type.isAssignableFrom(data.get(name).getClass()))
			return (T) data.get(name);
		return null;
	}
	
	public void putDataExtra(String name, Object value)
	{
		data.put(name, value);
	}
	
	public Object getDataObject()
	{
		return dataObject;
	}
}
