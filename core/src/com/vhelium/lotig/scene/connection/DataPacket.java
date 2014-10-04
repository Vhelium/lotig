package com.vhelium.lotig.scene.connection;

import com.vhelium.lotig.constants.ByteConverter;

public class DataPacket
{
	public static int PACKET_SIZE = 4096;
	public static int PACKET_SIZE_BIG = 4096;
	private byte[] data = null;
	private int index = Integer.SIZE / Byte.SIZE;
	
	public DataPacket(byte[] daten)
	{
		data = daten;
	}
	
	public DataPacket()
	{
		data = new byte[PACKET_SIZE];
	}
	
	public DataPacket(int type)
	{
		data = new byte[PACKET_SIZE];
		setInt(type);
	}
	
	public DataPacket(boolean big)
	{
		if(big)
			data = new byte[PACKET_SIZE_BIG];
		else
			data = new byte[PACKET_SIZE];
	}
	
	public boolean getBoolean()
	{
		byte[] ba = new byte[1];
		System.arraycopy(data, index, ba, 0, ba.length);
		boolean b = ByteConverter.toBoolean(ba);
		index += 1;
		return b;
	}
	
	public void setBoolean(boolean b)
	{
		byte[] ba = ByteConverter.toByta(b);
		System.arraycopy(ba, 0, data, index, ba.length);
		index += 1;
	}
	
	public int getInt()
	{
		// int i=Array.getInt(data, index);
		byte[] ia = new byte[Integer.SIZE / Byte.SIZE];
		System.arraycopy(data, index, ia, 0, ia.length);
		int i = ByteConverter.toInt(ia);
		index += Integer.SIZE / Byte.SIZE;
		return i;
	}
	
	public void setInt(int i)
	{
		// Array.setInt(data, index, i);
		byte[] ia = ByteConverter.toByta(i);
		System.arraycopy(ia, 0, data, index, ia.length);
		index += Integer.SIZE / Byte.SIZE;
	}
	
	public long getLong()
	{
		byte[] la = new byte[Long.SIZE / Byte.SIZE];
		System.arraycopy(data, index, la, 0, la.length);
		long l = ByteConverter.toLong(la);
		index += Long.SIZE / Byte.SIZE;
		return l;
	}
	
	public void setLong(long l)
	{
		byte[] la = ByteConverter.toByta(l);
		System.arraycopy(la, 0, data, index, la.length);
		index += Long.SIZE / Byte.SIZE;
	}
	
	public float getFloat()
	{
		// float f= Array.getFloat(data, index);
		byte[] fa = new byte[Float.SIZE / Byte.SIZE];
		System.arraycopy(data, index, fa, 0, fa.length);
		index += Float.SIZE / Byte.SIZE;
		return ByteConverter.toFloat(fa);
	}
	
	public void setFloat(float f)
	{
		// Array.setFloat(data, index, f);
		byte[] fa = ByteConverter.toByta(f);
		System.arraycopy(fa, 0, data, index, fa.length);
		index += Float.SIZE / Byte.SIZE;
	}
	
	public String getString()
	{
		int lenght = getInt();
		byte[] sa = new byte[lenght];
		System.arraycopy(data, index, sa, 0, lenght);
		index += lenght;
		return ByteConverter.toString(sa);
		
	}
	
	public void setString(String s)
	{
		byte[] sa = ByteConverter.toByta(s);
		setInt(sa.length);
		System.arraycopy(sa, 0, data, index, sa.length);
		index += sa.length;
	}
	
	public byte[] finish()
	{
		int dataSize = index;
		// Array.setInt(data,0,dataSize);
		byte[] ia = ByteConverter.toByta(dataSize);
		System.arraycopy(ia, 0, data, 0, Integer.SIZE / Byte.SIZE);
		byte[] arr = new byte[dataSize];
		System.arraycopy(data, 0, arr, 0, dataSize);
		return arr;
	}
	
	public void reset()
	{
		index = Integer.SIZE / Byte.SIZE;
	}
}
