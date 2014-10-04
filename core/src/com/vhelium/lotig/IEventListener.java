package com.vhelium.lotig;

public interface IEventListener
{
	boolean isDead();
	
	void eventResultReceived(EventResult result);
}
