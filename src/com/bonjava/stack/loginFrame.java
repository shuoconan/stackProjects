package com.bonjava.stack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class loginFrame extends JLabel implements Runnable{
	private BufferedImage bgImage;
	private Thread refreshThread;
	private File file;
	private loginUserList ll;
	public loginFrame(String strpath,loginUserList ll) {
		// TODO Auto-generated constructor stub
		this.ll = ll;
		file = new File(strpath);
		try {
			this.bgImage = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setBgImage(this.bgImage);
	}
	public loginFrame(String strpath) {
		// TODO Auto-generated constructor stub
		
		file = new File(strpath);
		try {
			this.bgImage = ImageIO.read(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setBgImage(this.bgImage);
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
		// 全屏描绘图片
		graph.drawImage(this.bgImage, 0, 0, getWidth(), getHeight(), 0, 0, this.bgImage.getWidth(null), this.bgImage.getHeight(null), null);
		}
	}
	public void run() {
		while (true) {
			this.repaint();// 这里调用了Paint
			try {
				Thread.sleep(10);// 休眠100毫秒
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
