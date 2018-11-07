package com.bonjava.stack;

import java.awt.Font;

import javax.swing.JLabel;

public class TitlrFrame extends JLabel{
	private String strUser = null;
	public TitlrFrame(String strUser) {
		// TODO Auto-generated constructor stub
		this.strUser = strUser;
		this.setText(this.strUser);
		this.setFont(new Font("Î¢ÈíÑÅºÚ",Font.BOLD,40));
		
	}
}
