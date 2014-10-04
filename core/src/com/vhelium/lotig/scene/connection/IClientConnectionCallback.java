package com.vhelium.lotig.scene.connection;

import java.io.IOException;
import com.vhelium.lotig.Main;

public interface IClientConnectionCallback
{
	void connectionEstablished() throws IOException;
	
	void messageReceived(byte[] data) throws IOException;
	
	void connectionLost();
	
	Main getActivity();
	
	void returnToMainMenu();
	
	void returnToMainMenu(int code);
}
