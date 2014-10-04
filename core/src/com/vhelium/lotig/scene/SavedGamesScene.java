package com.vhelium.lotig.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.vhelium.lotig.components.Label;
import com.vhelium.lotig.scene.gamescene.FontCategory;
import com.vhelium.lotig.scene.gamescene.GameHelper;

public class SavedGamesScene extends MenuScene
{
	private final Label txtDeleting;
	public boolean isDeleteSaveGame = false;
	
	public SavedGamesScene(IOnMenuItemClickListener listener)
	{
		super(listener);
		txtDeleting = new Label("Select the caracter you want to delete", new LabelStyle(GameHelper.getInstance().getMainFont(FontCategory.MainMenu, 28), Color.RED));
		txtDeleting.pack();
		txtDeleting.setPosition(0, 405);
		txtDeleting.setX(SceneManager.CAMERA_WIDTH / 2 - txtDeleting.getWidth() / 2);
	}
	
	public void deleteSaveGameActivated()
	{
		isDeleteSaveGame = true;
		this.addActor(txtDeleting);
	}
	
	public void abordDeleting()
	{
		isDeleteSaveGame = false;
		txtDeleting.remove();
	}
}
