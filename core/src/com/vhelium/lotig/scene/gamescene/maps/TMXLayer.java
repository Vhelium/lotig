package com.vhelium.lotig.scene.gamescene.maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TMXLayer extends Actor
{
	private final FlippedTiledMapRenderer renderer;
	private final TiledMapTileLayer layer;
	private final OrthographicCamera camera;
	
	public TMXLayer(FlippedTiledMapRenderer renderer, TiledMapTileLayer layer, OrthographicCamera camera)
	{
		this.renderer = renderer;
		this.layer = layer;
		this.camera = camera;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
		renderer.setView(camera);
		renderer.renderTileLayer(layer);
	}
	
	@Override
	public String getName()
	{
		return layer.getName();
	}
}
