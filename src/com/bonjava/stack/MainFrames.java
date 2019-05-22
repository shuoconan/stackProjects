package com.bonjava.stack;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.module.interaction.RXTXListener;
import com.module.interaction.ReaderHelper;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderConnector;
import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.util.StringTool;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import jdk.nashorn.internal.ir.Flags;
import redis.clients.jedis.Jedis;
import tools.HttpsUtils;
import tools.testHttPInterface;


public class MainFrames implements MouseListener,ObserverBonjava,Runnable{
	private RFIDReaderHelper mReaderHelper ;
	private CheckOutFrame cof = new CheckOutFrame();
	private HashSet<String> hSet = new HashSet<String>();
	private ReaderConnector mConnector = new ReaderConnector();
	private JFrame MainFrame = new JFrame();
	private JLabel LoggingFrame = new JLabel();
	private loginFrame lf = new loginFrame("img/1.jpg");
	private loginUserList lf3 = new loginUserList();
	private loginFrame2 lf2 = null;
	private JLayeredPane jp = new JLayeredPane();
	private BufferedImage img = null;
	private String strUser = null;
	private JLabel useEmergency = null;
	private JLabel dealOrder = null;
	private JLabel checkOut = null;
	private SerialPort serialPort = null;
	private Set<String> rfidName = new HashSet<String>();
	private ArrayList<JLabel> jLabels = new ArrayList<JLabel>();
	private ArrayList<String> extraArrayList = new ArrayList<String>();
	private int m = 0;
	private Thread thread2 = null;//RFID工作线程
	private rfidDuty fd = new rfidDuty();
	private Jedis jedis = new Jedis("127.0.0.1");
	private DefaultListModel<String>  toLModel = new DefaultListModel();
	private DefaultListModel<String>  doneLModel = new DefaultListModel();
	private JList<String> todoList = new JList<>(toLModel);
	private JList<String> doneList = new JList<>(doneLModel);
	private JScrollPane todoJScrollPane = new JScrollPane(todoList);
	private JScrollPane doneJScrollPane = new JScrollPane(doneList);
	private ArrayList<String> selectedList = new ArrayList<String>();
	private Observer mObserver = new RXObserver() {
		@Override
		protected void onInventoryTag(RXInventoryTag tag) {
			String tagTempString = tag.strEPC.replaceAll(" ", "");
			jedis.sadd("rfid_temp", tagTempString);
		}
	};
	private RXTXListener mListener = new RXTXListener() {
		@Override
		public void reciveData(byte[] btAryReceiveData) {
			// TODO Auto-generated method stub
			System.out.println("reciveData" + StringTool.byteArrayToString(btAryReceiveData, 0, btAryReceiveData.length));
		}

		@Override
		public void sendData(byte[] btArySendData) {
			// TODO Auto-generated method stub
			System.out.println("sendData" + StringTool.byteArrayToString(btArySendData, 0, btArySendData.length));
		}

		@Override
		public void onLostConnect() {
			// TODO Auto-generated method stub
		}
		
	};
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public MainFrames() {
		// TODO Auto-generated constructor stub
		this.mReaderHelper = (RFIDReaderHelper) mConnector.connectCom("COM5", 115200);
		//设置天线
		this.mReaderHelper.setWorkAntenna((byte)0xFF, (byte)0x00);
		this.mReaderHelper.setWorkAntenna((byte)0xFF, (byte)0x01);
		this.mReaderHelper.setWorkAntenna((byte)0xFF, (byte)0x02);
		this.mReaderHelper.setWorkAntenna((byte)0xFF, (byte)0x03);
		this.MainFrame = new JFrame("智慧仓储");
		this.MainFrame.setUndecorated(true);
		this.MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.MainFrame.setBounds(0, 0, 1280, 1024);
		this.jp.setBounds(0, 0, 1280, 1024);
		this.jp.setLayout(null);
		this.lf.setBounds(0,0,1280,1024);
		this.lf3.setLayout(null);
		this.lf3.setBounds(0,0,1280,1024);
		this.lf3.addObserver(this);
		this.jp.add(this.lf);
		this.MainFrame.add(this.jp);
		this.MainFrame.setVisible(true);
		this.lf.addMouseListener(this);
		this.lf3.addMouseListener(this);
		
	}
	

	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getComponent().equals(this.lf)){
			
			this.jp.remove(this.lf);
			this.jp.add(this.lf3,new Integer(100));
			this.jp.repaint();		
		}
		if(e.getComponent().equals(this.checkOut)){
			OutStack ot = new OutStack();
			ot.setVisible(true);
		}
		//领用物资按钮
		if(e.getComponent().equals(this.dealOrder)){
			byte[] bytes = {(byte)0xAA,(byte)0x5A,(byte)0x00,(byte)0xFD,(byte)0x00,(byte)0x00};
			System.out.println(this.selectedList.toString());
			this.selectedList.removeAll(Collections.singleton(null));
			for(int i = 0;i<this.selectedList.size();i=i+2){
				System.out.println(this.selectedList.toString());
				String location = this.jedis.get(this.selectedList.get(i)+"_num");
				System.out.println(this.selectedList.get(i)+"_num");
				System.out.println(location);
					   switch (Integer.parseInt(location)) {
					   case 1:
					   	   bytes[4] = (byte) (bytes[4]|(byte)0x80);
						   break;
					   case 2:
						   bytes[4] = (byte) (bytes[4]|(byte)0x40);
						   break;
					   case 3:
						   bytes[4] = (byte) (bytes[4]|(byte)0x20);
						   break;
					   case 4:
						   bytes[4] = (byte) (bytes[4]|(byte)0x10);
						   break;
					   case 5:
						   bytes[4] = (byte) (bytes[4]|(byte)0x08);
						   break;
					   case 6:
						   bytes[4] = (byte) (bytes[4]|(byte)0x04);
						   break;
					   case 7:
						   bytes[4] = (byte) (bytes[4]|(byte)0x02);
						   break;
					   case 8:
						   bytes[4] = (byte) (bytes[4]|(byte)0x01);
						   break;
					   case 9:
						   bytes[5] = (byte) (bytes[5]|(byte)0x80);
						   break;
					   default:
						   break;
					   }	
					
			}
			try {
				serialPort  =SerialTool.openPort("COM3", 9600);
				SerialTool.addListener(serialPort, new SerialListener());
				SerialTool.sendToPort(serialPort, bytes);
				byte[] bytess = {(byte)0xAA,(byte)0x5A,(byte)0x00,(byte)0xF1,(byte)0x00,(byte)0xFF};
				SerialTool.sendToPort(serialPort, bytess);
				SerialTool.closePort(serialPort);
			} catch (SerialPortParameterFailure e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		//确认出库按钮
		if(e.getComponent().equals(this.useEmergency)){
			int op = JOptionPane.showConfirmDialog(null, "物品已记录，出库成功！","成功",JOptionPane.YES_NO_OPTION);
			if(op == JOptionPane.YES_OPTION){
				try {
					this.selectedList.clear();
					this.doneLModel.clear();
					this.toLModel.clear();
					serialPort = SerialTool.openPort("COM3", 9600);
					SerialTool.addListener(serialPort, new SerialListener());
					//这边加锁复位的命令
					byte[] byteReset = {(byte)0xAA,(byte)0x5A,(byte)0x00,(byte)0xFE,(byte)0x00,(byte)0xFF};
					SerialTool.sendToPort(serialPort, byteReset);
					serialPort.close();
					
				} catch (SerialPortParameterFailure e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.jp.removeAll();
				this.jp.add(this.lf,new Integer(100));
				
			}
		}
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
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	//观察者模式更新函数
	@Override
	public void update(String info) {
		// TODO Auto-generated method stub
		if(info.equals("SUCCESS")){
			this.setLoggingFrame("认  证  成  功");
			this.jp.add(this.LoggingFrame,new Integer(300));
			this.hSet.clear();
			Thread thread = new Thread(this);
			thread.start();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					AudioInputStream as;
					try {
						as = AudioSystem.getAudioInputStream(new File("img/1.wav"));//音频文件在项目根目录的img文件夹下
						AudioFormat format = as.getFormat();
						SourceDataLine sdl = null;
						DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
						sdl = (SourceDataLine) AudioSystem.getLine(info);
						sdl.open(format);
						sdl.start();
						int nBytesRead = 0;
						byte[] abData = new byte[512];
						while (nBytesRead != -1) {
							nBytesRead = as.read(abData, 0, abData.length);
							if (nBytesRead >= 0)
								sdl.write(abData, 0, nBytesRead);
						}
					    //关闭SourceDataLine
						sdl.drain();
						sdl.close();
						}catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					    
					
				}
			}).start();
			this.fd.startMe();
			this.thread2 = new Thread(fd);
			this.thread2.start();
		}else if (info.equals("ERROR")) {
			this.setLoggingFrame("认  证  失  败");
			this.jp.add(this.LoggingFrame,new Integer(300));
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(2000);
						MainFrames.this.jp.removeAll();
						MainFrames.this.jp.add(MainFrames.this.lf,new Integer(100));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}else if (info.equals("USERSUCC")) {
			if(this.lf3.getstrUsr()!=null){
				this.jp.remove(this.lf3);
				this.lf2 = new loginFrame2("img/2.gif");
				DetecPanel dp = new DetecPanel(this.lf3);
				this.strUser = this.lf3.getstrUsr();
				this.lf2.setBounds(0,0,1280,1024);
				dp.setBounds(67,0,1280,1024);
				dp.addObserver(this);
				this.jp.add(this.lf2,new Integer(100));
				this.jp.add(dp,new Integer(200));
				dp.openCV();
				this.jp.repaint();
				
			}
		}
	}
	/**
	 * 
	 * @param strText日志界面的文字信息
	 */
	public void setLoggingFrame(String strText){
		this.LoggingFrame.setText(strText);
		this.LoggingFrame.setBounds(214, 342, 852, 200);
		this.LoggingFrame.setFont(new Font("楷体", Font.BOLD, 80));
		this.LoggingFrame.setBackground(new Color(0,161,252));
		this.LoggingFrame.setForeground(Color.white);
		this.LoggingFrame.setBorder(BorderFactory.createLineBorder(Color.white, 5));
		this.LoggingFrame.setOpaque(true);
		this.LoggingFrame.setHorizontalAlignment(JLabel.CENTER);
		this.LoggingFrame.setVerticalAlignment(JLabel.CENTER);
	}
	@Override
	public void update2(BufferedImage bgMImage) {
		// TODO Auto-generated method stub
		this.img = bgMImage;
	}
	//获取订单信息
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		
		try {
			int j = 0;
			JsonArray ja = null;
			JsonObject joTemp = null;
			Thread.sleep(1000);
			this.jp.removeAll();
			this.cof.setBounds(0, 0, 1280, 1024);
			IconImage ii = new IconImage(this.img);
			ii.setBounds(36,147,260,195);
			JLabel nameUser = new JLabel();
			nameUser.setText(this.strUser);
			nameUser.setFont(new Font("微软雅黑",Font.BOLD,40));
			nameUser.setHorizontalAlignment(JLabel.CENTER);
			nameUser.setVerticalAlignment(JLabel.CENTER);
			nameUser.setBounds(45, 376, 247, 110);
			nameUser.setForeground(Color.white);
			this.jp.add(cof,new Integer(100));
			this.jp.add(ii,new Integer(101));
			this.jp.add(nameUser,new Integer(102));
//			ja = DatabaseManipulate.queryOrder(this.strUser);
			String host = "http://118.25.40.2";
		    String path = "/api/returngoodscata/";
		    String method = "POST";
		    Map<String, String> querys = new HashMap<String, String>();
			Map<String, String> headers = new HashMap<String, String>();
			Map<String, String> bodys = new HashMap<String, String>();
			headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			bodys.put("stack_users", this.strUser);
			System.out.println(this.strUser);
			bodys.put("stack_name", "黎城供电所应急库");
			try {
				HttpResponse response = HttpsUtils.doPost(host, path, method, headers, querys, bodys);
				String strings = EntityUtils.toString(response.getEntity());
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					JsonObject jObject = testHttPInterface.str2Json(strings);
					System.out.println(jObject.toString());
					String code = jObject.get("code").getAsString();
					if(code.equals("0")){
						ja = jObject.getAsJsonArray("data");
						
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			this.todoList.setFont(new Font("微软雅黑",Font.PLAIN,30));
			this.todoJScrollPane.setBounds(340,239,434,733);
			this.doneList.setFont(new Font("微软雅黑",Font.PLAIN,30));
			this.doneJScrollPane.setBounds(789, 239, 434, 733);
			if(ja!=null){
				this.toLModel.clear();
				this.doneLModel.clear();
				for(int i = 0;i<ja.size();i++){
					joTemp = (JsonObject)ja.get(i);
					System.out.println(joTemp.get("name").getAsString());
					this.toLModel.addElement(joTemp.get("name").getAsString());
					this.jedis.set(joTemp.get("name").getAsString()+"_id",joTemp.get("remains").getAsString());
					JsonArray ja2 = joTemp.get("where").getAsJsonArray();
					String jot = ((JsonObject)ja2.get(0)).get("where_is").getAsString();
					this.jedis.set(joTemp.get("name").getAsString()+"_num",jot);			
				}
                this.todoList.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						do_list_valueChanged(e);
					}
				});
                this.doneList.addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						do_list_valueChanged2(e);
					}
				});
						
				this.dealOrder = new JLabel("领用订单物资",JLabel.CENTER);
				this.dealOrder.setBounds(41, 500, 250, 56);
				this.dealOrder.setFont(new Font("微软雅黑",Font.PLAIN,30));
			    this.dealOrder.setForeground(Color.white);
			    this.dealOrder.setBorder(BorderFactory.createLineBorder(Color.white));
      			this.dealOrder.addMouseListener(this);
      			this.jp.add(this.dealOrder,new Integer(199));
				this.jp.add(this.todoJScrollPane,new Integer(200));
				this.jp.add(this.doneJScrollPane,new Integer(300));
				
			}
//			this.checkOut = new JLabel("查看出库单",JLabel.CENTER);
//			this.checkOut.setBounds(41, 570, 250, 56);
//			this.checkOut.setFont(new Font("微软雅黑",Font.PLAIN,30));
//			this.checkOut.setForeground(Color.white);
//			this.checkOut.setBorder(BorderFactory.createLineBorder(Color.white));
//			this.checkOut.addMouseListener(this);
//			this.jp.add(this.checkOut,new Integer(301)); 
			//关门关灯
			this.useEmergency = new JLabel("确认出库",JLabel.CENTER);
			this.useEmergency.setBounds(41, 570, 250, 56);
			this.useEmergency.setFont(new Font("微软雅黑",Font.PLAIN,30));
			this.useEmergency.setForeground(Color.white);
			this.useEmergency.setBorder(BorderFactory.createLineBorder(Color.white));
			this.useEmergency.addMouseListener(this);
			this.jp.add(this.useEmergency,new Integer(301));
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void do_list_valueChanged(ListSelectionEvent e){
		this.selectedList.add(this.todoList.getSelectedValue());
		this.doneLModel.addElement(this.todoList.getSelectedValue());
		this.toLModel.removeElement(this.todoList.getSelectedValue());
	}
	public void do_list_valueChanged2(ListSelectionEvent e){
		this.toLModel.addElement(this.doneList.getSelectedValue());
		this.doneLModel.removeElement(this.doneList.getSelectedValue());
		
		
	}
//	public void dorfidData(String rfidNum){
////		String strCata = DatabaseManipulate.queryRfid(rfidNum);
//		System.out.println(strCata);
//		String realNum = DatabaseManipulate.queryStringrealNum(strCata, this.strUser );
//		System.out.println(realNum);
//		System.out.println(this.jLabels.toString());
//		if(this.jLabels.size()>0){
//			for(int i = 0;i<this.jLabels.size();i = i + 5 ){
//				if(jLabels.get(i).getText().equals(strCata)){
//					System.out.println("检测到");
//					realNum = String.valueOf(Integer.parseInt(realNum)-1);
//					DatabaseManipulate.setFoodsNums(realNum, this.strUser, strCata);
//					if(realNum.equals("0")){
//						this.jLabels.get(i+3).setText("已领足");
//					}else if(Integer.parseInt(realNum)>0){
//						this.jLabels.get(i+3).setText(realNum+"件未领");
//					}else{
//						this.jLabels.get(i+3).setText("超额领取");
//					}
//				}else{
//					this.extraArrayList.add(strCata);
//				}
//			}
//		}
//		
//	}
	private class SerialListener implements SerialPortEventListener {

		/**
		 * 处理监控到的串口事件
		 */
		public void serialEvent(SerialPortEvent serialPortEvent) {
			switch (serialPortEvent.getEventType()) {
			case SerialPortEvent.BI: // 10 通讯中断
			case SerialPortEvent.OE: // 7 溢位（溢出）错误
			case SerialPortEvent.FE: // 9 帧错误
			case SerialPortEvent.PE: // 8 奇偶校验错误
			case SerialPortEvent.CD: // 6 载波检测
			case SerialPortEvent.CTS: // 3 清除待发送数据
			case SerialPortEvent.DSR: // 4 待发送数据准备好了
			case SerialPortEvent.RI: // 5 振铃指示
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
				break;
			case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
				// System.out.println("found data");
				byte[] data = null;
				if (serialPort == null) {
				} else {
					data = SerialTool.readFromPort(serialPort); // 读取数据，存入字节数组
					// System.out.println(new String(data));
					// 自定义解析过程，你在实际使用过程中可以按照自己的需求在接收到数据后对数据进行解析
					if (data == null || data.length < 1) { // 检查数据是否读取正确

					} else {
						String dataOriginal = new String(data); // 将字节数组数据转换位为保存了原始数据的字符串
						String dataValid = ""; // 有效数据（用来保存原始数据字符串去除最开头*号以后的字符串）
						String[] elements = null; // 用来保存按空格拆分原始字符串后得到的字符串数组
						// 解析数据
						if (dataOriginal.charAt(0) == '*') { // 当数据的第一个字符是*号时表示数据接收完成，开始解析
							dataValid = dataOriginal.substring(1);
							elements = dataValid.split(" ");
							if (elements == null || elements.length < 1) { // 检查数据是否解析正确
							} else {

							}
						}
					}

				}
				break;
			}

		}

	}
	private class rfidDuty implements Runnable{
		private boolean flag = true;
		
		public void stopMe() {
			this.flag = false;
			
		}
		public void startMe() {
			this.flag = true;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mReaderHelper != null) {
				System.out.println("Connect success!");
				try {
					mReaderHelper.registerObserver(mObserver);
					while(flag){
						((RFIDReaderHelper) mReaderHelper).realTimeInventory((byte) 0xff,(byte)0x01);
					}
					
				} catch (Exception e) {	
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Connect faild!");
				mConnector.disConnect();
			}
		}
		
	}

}
	

