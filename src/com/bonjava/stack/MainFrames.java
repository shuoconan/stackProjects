package com.bonjava.stack;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

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
import com.util.StringTool;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class MainFrames extends RXObserver implements MouseListener,ObserverBonjava,Runnable{
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
	private SerialPort serialPort = null;
	private final ReaderConnector mConnector = new ReaderConnector();
	private ReaderHelper mReaderHelper;
	private Set<String> rfidName = new HashSet<String>();
	private ArrayList<JLabel> jLabels = new ArrayList<JLabel>();
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	RXTXListener mListener = new RXTXListener() {
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
	public MainFrames() {
		// TODO Auto-generated constructor stub
		this.MainFrame = new JFrame("智慧仓储");
		this.MainFrame.setUndecorated(true);
		this.MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.MainFrame.setBounds(0, 0, 1280, 1024);
		this.jp.setBounds(0, 0, 1280, 1024);
		this.jp.setLayout(null);
		this.lf.setBounds(0,0,1280,1024);
		this.lf3.setLayout(null);
		this.lf3.setBounds(0,0,1280,1024);
		this.jp.add(this.lf);
		this.MainFrame.add(this.jp);
		this.MainFrame.setVisible(true);
		this.lf.addMouseListener(this);
		this.lf3.addMouseListener(this);
		
	}
	@Override
	protected void onInventoryTag(RXInventoryTag tag) {
		if(this.rfidName.contains(tag.strEPC.replaceAll(" ", "").replaceAll("\t", ""))){
			return;
		}else {
			this.rfidName.add(tag.strEPC.replaceAll(" ", "").replaceAll("\t", ""));
			String goodsNum = DatabaseManipulate.queryRfid(tag.strEPC.replaceAll(" ", "").replaceAll("\t", ""));
			System.out.println(goodsNum);
			String realNum = DatabaseManipulate.queryStringrealNum(goodsNum, this.strUser);
			int realNumInt = Integer.parseInt(realNum)-1;
			if(realNumInt > 0){
				for(int i = 0;i<this.jLabels.size();i=i+5){
					DatabaseManipulate.setFoodsNums(String.valueOf(realNumInt), this.strUser, goodsNum);
				}
			}else if (realNumInt < 0) {
				for(int i = 0;i<this.jLabels.size();i=i+5){
					if (this.jLabels.get(i).getText().equals(goodsNum)) {
						JLabel jLabel = new JLabel("多领",JLabel.CENTER);
						jLabel.setBounds(812,224+55*(i/5),175,55);	
						jLabel.setForeground(Color.red);
						jLabel.setFont(new Font("微软雅黑",Font.PLAIN,30));
						this.jp.add(jLabel,new Integer(1002+(i/5)*10+3));
						this.jp.repaint();
					}
				}
			}else{
				for(int i = 0;i<this.jLabels.size();i=i+5){
					if (this.jLabels.get(i).getText().equals(goodsNum)) {
						JLabel jLabel = new JLabel("已领完",JLabel.CENTER);
						jLabel.setBounds(812,224+55*(i/5),175,55);	
						jLabel.setForeground(Color.green);
						jLabel.setFont(new Font("微软雅黑",Font.PLAIN,30));
						this.jp.add(jLabel,new Integer(1002+(i/5)*10+3));
						this.jp.repaint();
					}
				}
			}
		}

	}
	
	@Override
	protected void onInventoryTagEnd(RXInventoryTag.RXInventoryTagEnd endTag) {
//		System.out.println("inventory end:" + endTag.mTotalRead);
//		((RFIDReaderHelper) mReaderHelper).realTimeInventory((byte) 0xff,(byte)0x01);
	}
	
	@Override
	protected void onExeCMDStatus(byte cmd,byte status) {
		System.out.format("CDM:%s  Execute status:%S", 
				String.format("%02X",cmd),String.format("%02x", status));
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getComponent().equals(this.lf)){
			System.out.println("点击了");	
			this.jp.remove(this.lf);
			this.lf3.addObserver(this);
			this.jp.add(this.lf3,new Integer(100));
			this.jp.repaint();		
		}
		if(e.getComponent().equals(this.dealOrder)){
			try {
				serialPort  =SerialTool.openPort("COM9", 9600);
				byte[] bytes = {(byte)0xAA,(byte)0x5A,(byte)0x00,(byte)0xF1,(byte)0x00,(byte)0xFF};
				SerialTool.addListener(serialPort, new SerialListener());
				SerialTool.sendToPort(serialPort, bytes);
				SerialTool.closePort(serialPort);
				doRFID();
			} catch (SerialPortParameterFailure e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(e.getComponent().equals(this.useEmergency)){
			
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
	@Override
	public void update(String info) {
		// TODO Auto-generated method stub
		if(info.equals("SUCCESS")){
			this.setLoggingFrame("认  证  成  功");
			this.jp.add(this.LoggingFrame,new Integer(300));
			Thread thread = new Thread(this);
			thread.start();
		}else if (info.equals("ERROR")) {
			this.setLoggingFrame("认  证  失  败");
			this.jp.add(this.LoggingFrame,new Integer(300));
		}else if (info.equals("USERSUCC")) {
			if(this.lf3.getstrUsr()!=null){
				System.out.println("get");
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
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int j = 0;
			JsonArray ja = null;
			JsonObject joTemp = null;
			Thread.sleep(1000);
			this.jp.removeAll();
			CheckOutFrame cof = new CheckOutFrame();
			cof.setBounds(0, 0, 1280, 1024);
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
			ja = DatabaseManipulate.queryOrder(this.strUser);
			if(ja!=null){
				byte[] bytes = {(byte)0xAA,(byte)0x5A,(byte)0x00,(byte)0xFD,(byte)0x00,(byte)0x00};
				System.out.println(bytes.length);
				for(int i = 0;i<ja.size();i++){
					joTemp = (JsonObject)ja.get(i);
					JLabel jLabel = new JLabel(joTemp.get("goods_cata").getAsString());
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_name").getAsString(),JLabel.CENTER);
					jLabel.setBounds(367,224+55*i,245,55);	
					jLabel.setFont(new Font("微软雅黑",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+1));
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_kinds").getAsString(),JLabel.CENTER);
					jLabel.setBounds(613,224+55*i,199,55);	
					jLabel.setFont(new Font("微软雅黑",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+2));
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_nums").getAsString(),JLabel.CENTER);
					jLabel.setBounds(812,224+55*i,175,55);	
					jLabel.setFont(new Font("微软雅黑",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+3));
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_location").getAsString()+"号柜",JLabel.CENTER);
					switch (Integer.parseInt(joTemp.get("goods_location").getAsString())) {
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
					jLabel.setBounds(997,224+55*i,221,55);		
					jLabel.setFont(new Font("微软雅黑",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+4));
					this.jLabels.add(jLabel);
					
				}
				try {
					serialPort  =SerialTool.openPort("COM9", 9600);
				} catch (SerialPortParameterFailure e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SerialTool.addListener(serialPort, new SerialListener());
				SerialTool.sendToPort(serialPort, bytes);
				SerialTool.closePort(serialPort);
				this.dealOrder = new JLabel("领用订单物资",JLabel.CENTER);
				this.dealOrder.setBounds(732, 925, 217, 56);
				this.dealOrder.setFont(new Font("微软雅黑",Font.PLAIN,30));
				this.dealOrder.setForeground(Color.white);
				this.dealOrder.setBorder(BorderFactory.createLineBorder(Color.white));
				this.dealOrder.addMouseListener(this);
				this.jp.add(this.dealOrder,new Integer(200));		
			}
			this.useEmergency = new JLabel("领用抢修物资",JLabel.CENTER);
			this.useEmergency.setBounds(998, 925, 217, 56);
			this.useEmergency.setFont(new Font("微软雅黑",Font.PLAIN,30));
			this.useEmergency.setForeground(Color.white);
			this.useEmergency.setBorder(BorderFactory.createLineBorder(Color.white));
			this.useEmergency.addMouseListener(this);
			this.jp.add(this.useEmergency,new Integer(300));
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

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
	public void doRFID(){
		mReaderHelper = mConnector.connectCom("COM8", 115200);
		if(mReaderHelper != null) {
			System.out.println("Connect success!");
			try {
				mReaderHelper.registerObserver(this);
				while(true){
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
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if(MainFrames.this.rfidName.isEmpty()){
						continue;
					} else {
						Iterator it = MainFrames.this.rfidName.iterator();
						while (it.hasNext()) {
							System.out.println(it.next());
						}
					}
				}
			}
		});
		thread.start();
	}

}
	

