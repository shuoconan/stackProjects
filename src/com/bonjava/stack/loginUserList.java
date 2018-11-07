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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class loginUserList extends JPanel implements MouseListener,subject{
	//ÓÃÀ´´æ·ÅºÍ¼ÇÂ¼¹Û²ìÕß
    private List<ObserverBonjava> observers=new ArrayList<ObserverBonjava>();
    //¼ÇÂ¼×´Ì¬µÄ×Ö·û´®
    private String info;
	private BufferedImage bgImage;
	private File file;
	private JLabel label = null;
	private ArrayList<String> list;
	private ArrayList<JLabel> listLabels = new ArrayList<JLabel>();
	private String strUsr;
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
		list = DatabaseManipulate.searchUsers();
		for(int i = 0;i<list.size();i++){
			label = new JLabel(list.get(i));
			this.listLabels.add(label);
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
