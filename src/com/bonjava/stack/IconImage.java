package com.bonjava.stack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import tools.ImageManipulate;

public class IconImage extends JLabel{
	private BufferedImage bgImg = null;
	public IconImage(BufferedImage bgImage) {
		// TODO Auto-generated constructor stub
		this.bgImg = bgImage;
	}
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D graph = (Graphics2D) g;
		this.bgImg = ImageManipulate.resizeImage(this.bgImg, 0.5);
		if (this.bgImg != null) {
		// È«ÆÁÃè»æÍ¼Æ¬
		graph.drawImage(this.bgImg, 0, 0, getWidth(), getHeight(), 0, 0, this.bgImg.getWidth(null), this.bgImg.getHeight(null), null);
		}
	}
}
