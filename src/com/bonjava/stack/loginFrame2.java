package com.bonjava.stack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import tools.Mat2BufferedImage;


public class loginFrame2 extends JLabel implements Runnable{
	private BufferedImage bgImage;
	private Thread refreshThread;
	private File file;
	private String strName;
	private String strID;
	private loginUserList ll;
	private BufferedImage mImg = null;
	private Mat img = new Mat();
	public loginFrame2(String strpath) {
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
		this.refreshThread = new Thread(this);
		this.refreshThread.start();
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
			for(int i = 1;i<=12;i++){
				try {
					this.bgImage = ImageIO.read(new File("img/2/2_"+String.valueOf(i)+".jpeg"));
				    this.repaint();
					Thread.sleep(50);// 休眠100毫秒
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 这里调用了Paint	
		}
	}
}
