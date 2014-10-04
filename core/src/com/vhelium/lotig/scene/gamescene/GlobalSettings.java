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

public class GlobalSettings
{
	private ConcurrentHashMap<String, String> datas;
	
	private static GlobalSettings instance;
	
	public static GlobalSettings getInstance()
	{
		if(instance == null)
			instance = new GlobalSettings();
		
		return instance;
	}
	
	public GlobalSettings()
	{
		
	}
	
	public static void save()
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try
		{
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			Element rootElement = doc.createElement("settings");
			doc.appendChild(rootElement);
			
			Element data = doc.createElement("data");
			for(Entry<String, String> e : getInstance().getData().entrySet())
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
			
			OutputStream fos = GameHelper.platformResolver.getOutputStream("settings.txt");
			StreamResult file = new StreamResult(fos);
			
			//write data
			transformer.transform(source, file);
			
//			//TODO: TESTING: FOR DEBUG INFOS: WRITE TO SD
//			File myFile = new File("/sdcard/settings.txt");
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
	
	public static void load()
	{
		File file = GameHelper.platformResolver.getLocalFile("settings.txt");
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
				
				getInstance().datas = datas;
			}
			catch (Exception e)
			{
				
			}
		}
		else
		{
			getInstance().datas = new ConcurrentHashMap<String, String>();
			setDefaultSettings(getInstance().datas);
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
	
	public static void update(String k, String v)
	{
		getInstance().getData().put(k, v);
		save();
	}
	
	private static void setDefaultSettings(ConcurrentHashMap<String, String> data)
	{
		data.put("tutorialseen", String.valueOf(false));
		data.put("Music", String.valueOf(true));
		data.put("Sound", String.valueOf(true));
		data.put("Vibrate", String.valueOf(true));
		data.put("Aim-Help", String.valueOf(false));
		data.put("Center-Minimap", String.valueOf(true));
		data.put("lasttipp", String.valueOf(0));
	}
}
