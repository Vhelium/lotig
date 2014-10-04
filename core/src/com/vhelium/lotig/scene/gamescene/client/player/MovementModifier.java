package com.vhelium.lotig.scene.gamescene.client.player;

public class MovementModifier
{
	private final float treshold = 0.006f;
	public String name;
	public float dirX;
	public float dirY;
	public float velocity;
	public float decay;
	
	public MovementModifier(String name, float dirX, float dirY, float velocity, float decay)
	{
		this.name = name;
		this.dirX = dirX;
		this.dirY = dirY;
		this.velocity = velocity;
		this.decay = decay;
	}
	
	public void set(String name, float dirX, float dirY, float velocity, float decay)
	{
		this.name = name;
		this.dirX = dirX;
		this.dirY = dirY;
		this.velocity = velocity;
		this.decay = decay;
	}
	
	public boolean validate()
	{
		return Math.abs(velocity) > treshold;
	}
}
