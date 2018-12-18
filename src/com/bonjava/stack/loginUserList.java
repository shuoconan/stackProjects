package com.bonjava.stack;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.NEW;

import redis.clients.jedis.Jedis;
import tools.HttpsUtils;
import tools.testHttPInterface;

public class loginUserList extends JPanel implements MouseListener,subject{
	//ÓÃÀ´´æ·ÅºÍ¼ÇÂ¼¹Û²ìÕß
    private List<ObserverBonjava> observers=new ArrayList<ObserverBonjava>();
    //¼ÇÂ¼×´Ì¬µÄ×Ö·û´®
    private String info;
	private BufferedImage bgImage;
	private File file;
	private JLabel label = null;
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<JLabel> listLabels = new ArrayList<JLabel>();
	private String strUsr;
	private Jedis jedis = new Jedis("127.0.0.1");
	public loginUserList() {
		// TODO Auto-generated constructor stub
		
		file = new File("img/2.jpg");
		try {
			this.bgImage = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setBgImage(this.bgImage);
		this.jedis.flushDB();
		String host = "https://www.kpcodingoffice.com";
	    String path = "/api/getstackusers";
	    String method = "POST";
	    Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> bodys = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		bodys.put("stacknum", "LCGDS0001");
		try {
			HttpResponse response = HttpsUtils.doPost(host, path, method, headers, querys, bodys);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				JsonObject jObject = testHttPInterface.str2Json(EntityUtils.toString(entity));
				String code = jObject.get("code").getAsString();
				if(code.equals("0")){
					JsonArray jArray = jObject.getAsJsonArray("data");
					for(int i = 0;i < jArray.size();i++){
						JsonObject joTmp = jArray.get(i).getAsJsonObject();
						String nameTmp = joTmp.get("name").getAsString();
						System.out.println(nameTmp);
						this.jedis.set(joTmp.get("name").getAsString()+"id", joTmp.get("id").getAsString());
						this.jedis.set(joTmp.get("name").getAsString()+"num", joTmp.get("user_num").getAsString());
						list.add(nameTmp);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		list = DatabaseManipulate.searchUsers();
		for(int i = 0;i<list.size();i++){
			label = new JLabel(list.get(i));
			this.listLabels.add(label);
			System.out.println(this.jedis.get("list.get(i)"));
		}
		int j = 0;
		int k = 723;
		for(int i = 0;i<list.size();i++){
			if(j == 3){
				j = 0;
				k += 80; 
			}
			listLabels.get(i).setBounds(387+180*j,k,150,50);
			listLabels.get(i).setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 40));
			listLabels.get(i).setForeground(Color.white);
			listLabels.get(i).addMouseListener(this);
			j++;
			this.add(listLabels.get(i));
		}
		
		
	}
	public String getstrUsr() {
		return this.strUsr;
	}
	public void setBgImage(BufferedImage image){
		this.bgImage = image;
//		this.refreshThread = new Thread(this);
//		this.refreshThread.start();
	}
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graph = (Graphics2D) g;
		if (this.bgImage != null) {
		// È«ÆÁÃè»æÍ¼Æ¬
		graph.drawImage(this.bgImage, 0, 0, getWidth(), getHeight(), 0, 0, this.bgImage.getWidth(null), this.bgImage.getHeight(null), null);
		}
		super.paintComponents(g);
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		this.strUsr = ((JLabel)e.getComponent()).getText();
		this.info = "USERSUCC";
		notifyObserver();
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		((JLabel)e.getComponent()).setBackground(Color.white);
		((JLabel)e.getComponent()).setForeground(Color.black);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		((JLabel)e.getComponent()).setForeground(Color.white);
	}
	@Override
	public void addObserver(ObserverBonjava obj) {
		// TODO Auto-generated method stub
		this.observers.add(obj);
	}
	@Override
	public void deleteObserver(ObserverBonjava obj) {
		// TODO Auto-generated method stub
		int i = observers.indexOf(obj);
        if(i>=0){
            observers.remove(obj);
        }
	}
	@Override
	public void notifyObserver() {
		// TODO Auto-generated method stub
		for(int i=0;i<observers.size();i++){
			ObserverBonjava o=(ObserverBonjava)observers.get(i);
            o.update(this.info);
        }
	}

}
