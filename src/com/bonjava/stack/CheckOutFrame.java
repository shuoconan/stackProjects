package com.bonjava.stack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class CheckOutFrame extends JLabel{
	private BufferedImage bgImage = null;
	public CheckOutFrame() {
		// TODO Auto-generated constructor stub
		try {
			this.bgImage = ImageIO.read(new File("img/4.jpg"));
			this.repaint();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graph = (Graphics2D) g;
		if (this.bgImage != null) {
		// È«ÆÁÃè»æÍ¼Æ¬
		graph.drawImage(this.bgImage, 0, 0, getWidth(), getHeight(), 0, 0, this.bgImage.getWidth(null), this.bgImage.getHeight(null), null);
		
		}
	}
}
