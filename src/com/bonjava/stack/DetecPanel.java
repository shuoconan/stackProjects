package com.bonjava.stack;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.NEW;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import jdk.nashorn.internal.ir.Flags;
import redis.clients.jedis.Jedis;
import tools.*;


public class DetecPanel extends JLabel implements Runnable,subject{
	//用来存放和记录观察者
    private List<ObserverBonjava> observers=new ArrayList<ObserverBonjava>();
    //记录状态的字符串
    private String info;
	private BufferedImage mImg = null;
	private int flag = 0;
	private BufferedImage okMImage = null;
	private Mat img = new Mat();
	private Thread refreshThread = null;
	private MatOfRect mor = new MatOfRect();
	private loginUserList ll = null;
	private String user = null;
	private String userId = null;
	private VideoCapture vc = new VideoCapture(0);
	private Jedis jedis = new Jedis("127.0.0.1");
	public DetecPanel(loginUserList ll) {
		// TODO Auto-generated constructor stub
		this.ll = ll;	
		this.user = this.ll.getstrUsr();
//		this.userId = DatabaseManipulate.queryStringID(this.user);
	}
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		Graphics2D graph = (Graphics2D) g;
		if(this.mImg != null){
			graph.drawImage(this.mImg, 67, 0, 534, 400, null);
		}
	}
	public void openCV() {
		this.refreshThread = new Thread(this);
		this.refreshThread.start();
	}
	public void setImg(BufferedImage img){
		this.mImg = img;
	}
	public void setOpenCVCamera(){
		this.vc.open(0);
		int height = (int) vc.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
		int width = (int) vc.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
		this.setBounds(385, 195,height,width);
		if((height == 0)||(width == 0)){
			JOptionPane.showMessageDialog(this, "摄像头打开失败!");
			return;
		}else{
			Mat temp = new Mat();
			while(this.flag <= 20){
				vc.read(this.img);
				Imgproc.cvtColor(img, temp, Imgproc.COLOR_RGB2BGR555);
				this.mImg = Mat2BufferedImage.matToBufferedImage(detecFace(img));
				this.repaint();
			}
			this.vc.release();
			
		}
		
	}
	public Mat detecFace(Mat img){
		CascadeClassifier cascadeClassifier = new CascadeClassifier("img/lbpcascade_frontalface_improved.xml");
		cascadeClassifier.detectMultiScale(img, mor);
		JsonObject jObject = new JsonObject();
		String host = "https://faceid.shumaidata.com";
	    String path = "/face_idcard/verify";
	    String method = "POST";
	    String appcode = "8a4e82280b3844dabe76b037a1e44ce3";
		Rect[] rects = mor.toArray();
		if(rects != null && rects.length >= 1){
			if(rects.length == 1){
				if(this.flag == 10){	
					System.out.println(this.jedis.get(this.user));
//					以下为联网识别，用该段代码要把146,147行注释掉。
//					Map<String, String> querys = new HashMap<String, String>();
//				    Map<String, String> bodys = new HashMap<String, String>();
//					Map<String, String> headers = new HashMap<String, String>();
//					headers.put("Authorization", "APPCODE " + appcode);
//					headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//					this.okMImage = Mat2BufferedImage.matToBufferedImage(img);
//					bodys.put("idcard", this.userId);
//				    bodys.put("image",BufferedImage2Base64.bufferedImage2Base64(this.okMImage));
//				    bodys.put("name", this.user.trim());
//					try {
//						HttpResponse response = HttpsUtils.doPost(host, path, method, headers, querys, bodys);
//						if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//					        HttpEntity entity = response.getEntity();
//					       // 返回json格式
//					        jObject = testHttPInterface.str2Json(EntityUtils.toString(entity));
//					        String code = jObject.get("code").getAsString();
//					        if(code.equals("-1")){
//					        	this.info = "ERROR";
//					        	notifyObserver();
//					        }else{
//					        	if(code.equals("0")){
//					        		jObject = jObject.get("result").getAsJsonObject();
//					        		Double result = Double.valueOf(jObject.get("score").getAsString());
//					        		if(result >= 0.9){
//					        			this.info = "SUCCESS";
//					        			notifyObserver();
//					        		}else{
//					        			this.info = "ERROR";
//					        			notifyObserver();
//					        		}
//					        	}
//					        }
//					      }
//					    } catch (Exception e) {
//					      throw new RuntimeException(e);
//					    }
					this.info = "SUCCESS";
					this.vc.release();
					notifyObserver();
				}
			}else {
				this.info = "ERROR";
    			notifyObserver();
			}
			for(Rect rect:rects){
				Imgproc.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,255,245),2);
			}
			this.flag++;
		}
		return img;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		setOpenCVCamera();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
            o.update2(this.mImg);
        }
	}



}
