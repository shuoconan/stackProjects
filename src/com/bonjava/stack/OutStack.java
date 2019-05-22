package com.bonjava.stack;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;

import tools.HttpsUtils;
import tools.testHttPInterface;

public class OutStack extends JFrame {

	private JPanel contentPane;
	/**
	 * Create the frame.
	 */
	public OutStack() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100,800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
	}
public void connet() {
	String host = "http://118.25.40.2";
    String path = "/api/getrfidgoods/";
    String method = "POST";
    Map<String, String> querys = new HashMap<String, String>();
	Map<String, String> headers = new HashMap<String, String>();
	Map<String, String> bodys = new HashMap<String, String>();
	headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	bodys.put("stack_users", "顾阳");
	bodys.put("stack_name", "黎城供电所应急库");
	
	try {
		HttpResponse response = HttpsUtils.doPost(host, path, method, headers, querys, bodys);
		String strings = EntityUtils.toString(response.getEntity());
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			JsonObject jObject = testHttPInterface.str2Json(strings);
			System.out.println(jObject.toString());
			String code = jObject.get("code").getAsString();
			if(code.equals("0")){
				//ja = jObject.getAsJsonArray("data");
				
			}
		}
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
}
}
