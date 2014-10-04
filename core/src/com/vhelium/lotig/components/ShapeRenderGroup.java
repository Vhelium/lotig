package com.vhelium.lotig.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Group;

public class ShapeRenderGroup extends Group
{
	private final Matrix4 oldBatchTransform = new Matrix4();
	private final ShapeRendererBatch rendererBatch;
	
	public ShapeRenderGroup(ShapeRendererBatch batch)
	{
		this.rendererBatch = batch;
	}
	
	@Override
	protected void applyTransform(Batch batch, Matrix4 transform)
	{
		super.applyTransform(batch, transform);
		oldBatchTransform.set(rendererBatch.getTransformMatrix());
		rendererBatch.setTransformMatrix(transform);
	}
	
	@Override
	protected void resetTransform(Batch batch)
	{
		super.resetTransform(batch);
		rendererBatch.setTransformMatrix(oldBatchTransform);
	}
}
