package com.bonjava.stack;

import java.awt.EventQueue;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 System.out.println(System.getProperty("java.library.path"));
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	new MainFrames();
	        }
	    });
            	
       
		
	}

}
