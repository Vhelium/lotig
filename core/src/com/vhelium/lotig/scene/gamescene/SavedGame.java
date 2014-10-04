package com.vhelium.lotig.scene.gamescene;

import java.io.File;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SavedGame
{
	public static final int maxSavedGameFiles = 6;
	private int fileNr;
	private ConcurrentHashMap<String, String> datas;
	
	private SavedGame(ConcurrentHashMap<String, String> datas)
	{
		this.datas = datas;
	}
	
	public SavedGame(int fileNr)
	{
		this.fileNr = fileNr;
	}
	
	public void save(ConcurrentHashMap<String, String> datas)
	{
		this.datas = datas;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			Element rootElement = doc.createElement("savedgame");
			doc.appendChild(rootElement);
			
			Element version = doc.createElement("version");
			version.appendChild(doc.createTextNode(String.valueOf(GameHelper.platformResolver.getVersion())));
			rootElement.appendChild(version);
			
			Element data = doc.createElement("data");
			for(Entry<String, String> e : datas.entrySet())
			{
				Element node = doc.createElement(e.getKey());
				node.setTextContent(e.getValue());
				data.appendChild(node);
			}
			rootElement.appendChild(data);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			
			OutputStream fos = GameHelper.platformResolver.getOutputStream("saved" + fileNr + ".txt");
			StreamResult file = new StreamResult(fos);
			
			//write data
			transformer.transform(source, file);
			
//			File myFile = new File("/sdcard/" + "saved" + fileNr + ".txt");
//			Log.e("savedgame", "path: " + myFile.getPath());
//			FileOutputStream fOut = new FileOutputStream(myFile);
//			
//			StreamResult sres = new StreamResult(fOut);
//			
//			//write data
//			transformer.transform(source, sres);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static SavedGame load(int fileNr)
	{
		File file = GameHelper.platformResolver.getLocalFile("saved" + fileNr + ".txt");
		if(file.exists())
		{
			try
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				
				NodeList rawDatas = doc.getElementsByTagName("data").item(0).getChildNodes();
				ConcurrentHashMap<String, String> datas = new ConcurrentHashMap<String, String>();
				for(int i = 0; i < rawDatas.getLength(); i++)
				{
					Node node = rawDatas.item(i);
					if(node.getNodeType() == Node.ELEMENT_NODE)
					{
						Element el = (Element) node;
						datas.put(el.getNodeName(), el.getTextContent());
					}
				}
				
				SavedGame res = new SavedGame(datas);
				res.fileNr = fileNr;
				
				//check for different versions
				NodeList version = doc.getElementsByTagName("version");
				int previousVersion = Integer.parseInt(version.item(0).getTextContent());
				
				int currentVersion = GameHelper.platformResolver.getVersion();
				
				if(previousVersion != currentVersion)
				{
					//merge the two different versions!
					SavedGameVersionMerger.merge(previousVersion, currentVersion, res);
				}
				
				return res;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else
			return null;
	}
	
	public static void delete(int fileNr)
	{
		File file = GameHelper.platformResolver.getLocalFile("saved" + fileNr + ".txt");
		if(!file.exists())
			return;
		if(!file.isDirectory())
		{
			file.delete();
			return;
		}
	}
	
	public ConcurrentHashMap<String, String> getData()
	{
		return datas;
	}
	
	public String getDataValue(String key)
	{
		if(datas != null)
			return datas.get(key);
		else
			return null;
	}
	
	public void removeData(String key)
	{
		if(datas != null && datas.containsKey(key))
			datas.remove(key);
	}
}
