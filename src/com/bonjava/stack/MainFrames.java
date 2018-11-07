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
		this.MainFrame = new JFrame("�ǻ۲ִ�");
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
						JLabel jLabel = new JLabel("����",JLabel.CENTER);
						jLabel.setBounds(812,224+55*(i/5),175,55);	
						jLabel.setForeground(Color.red);
						jLabel.setFont(new Font("΢���ź�",Font.PLAIN,30));
						this.jp.add(jLabel,new Integer(1002+(i/5)*10+3));
						this.jp.repaint();
					}
				}
			}else{
				for(int i = 0;i<this.jLabels.size();i=i+5){
					if (this.jLabels.get(i).getText().equals(goodsNum)) {
						JLabel jLabel = new JLabel("������",JLabel.CENTER);
						jLabel.setBounds(812,224+55*(i/5),175,55);	
						jLabel.setForeground(Color.green);
						jLabel.setFont(new Font("΢���ź�",Font.PLAIN,30));
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
			System.out.println("�����");	
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
			this.setLoggingFrame("��  ֤  ��  ��");
			this.jp.add(this.LoggingFrame,new Integer(300));
			Thread thread = new Thread(this);
			thread.start();
		}else if (info.equals("ERROR")) {
			this.setLoggingFrame("��  ֤  ʧ  ��");
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
		this.LoggingFrame.setFont(new Font("����", Font.BOLD, 80));
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
			nameUser.setFont(new Font("΢���ź�",Font.BOLD,40));
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
					jLabel.setFont(new Font("΢���ź�",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+1));
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_kinds").getAsString(),JLabel.CENTER);
					jLabel.setBounds(613,224+55*i,199,55);	
					jLabel.setFont(new Font("΢���ź�",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+2));
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_nums").getAsString(),JLabel.CENTER);
					jLabel.setBounds(812,224+55*i,175,55);	
					jLabel.setFont(new Font("΢���ź�",Font.PLAIN,30));
					this.jp.add(jLabel,new Integer(102+i*10+3));
					this.jLabels.add(jLabel);
					jLabel = new JLabel(joTemp.get("goods_location").getAsString()+"�Ź�",JLabel.CENTER);
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
					jLabel.setFont(new Font("΢���ź�",Font.PLAIN,30));
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
				this.dealOrder = new JLabel("���ö�������",JLabel.CENTER);
				this.dealOrder.setBounds(732, 925, 217, 56);
				this.dealOrder.setFont(new Font("΢���ź�",Font.PLAIN,30));
				this.dealOrder.setForeground(Color.white);
				this.dealOrder.setBorder(BorderFactory.createLineBorder(Color.white));
				this.dealOrder.addMouseListener(this);
				this.jp.add(this.dealOrder,new Integer(200));		
			}
			this.useEmergency = new JLabel("������������",JLabel.CENTER);
			this.useEmergency.setBounds(998, 925, 217, 56);
			this.useEmergency.setFont(new Font("΢���ź�",Font.PLAIN,30));
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
		 * �����ص��Ĵ����¼�
		 */
		public void serialEvent(SerialPortEvent serialPortEvent) {
			switch (serialPortEvent.getEventType()) {
			case SerialPortEvent.BI: // 10 ͨѶ�ж�
			case SerialPortEvent.OE: // 7 ��λ�����������
			case SerialPortEvent.FE: // 9 ֡����
			case SerialPortEvent.PE: // 8 ��żУ�����
			case SerialPortEvent.CD: // 6 �ز����
			case SerialPortEvent.CTS: // 3 �������������
			case SerialPortEvent.DSR: // 4 ����������׼������
			case SerialPortEvent.RI: // 5 ����ָʾ
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 ��������������
				break;
			case SerialPortEvent.DATA_AVAILABLE: // 1 ���ڴ��ڿ�������
				// System.out.println("found data");
				byte[] data = null;
				if (serialPort == null) {
				} else {
					data = SerialTool.readFromPort(serialPort); // ��ȡ���ݣ������ֽ�����
					// System.out.println(new String(data));
					// �Զ���������̣�����ʵ��ʹ�ù����п��԰����Լ��������ڽ��յ����ݺ�����ݽ��н���
					if (data == null || data.length < 1) { // ��������Ƿ��ȡ��ȷ

					} else {
						String dataOriginal = new String(data); // ���ֽ���������ת��λΪ������ԭʼ���ݵ��ַ���
						String dataValid = ""; // ��Ч���ݣ���������ԭʼ�����ַ���ȥ���ͷ*���Ժ���ַ�����
						String[] elements = null; // �������水�ո���ԭʼ�ַ�����õ����ַ�������
						// ��������
						if (dataOriginal.charAt(0) == '*') { // �����ݵĵ�һ���ַ���*��ʱ��ʾ���ݽ�����ɣ���ʼ����
							dataValid = dataOriginal.substring(1);
							elements = dataValid.split(" ");
							if (elements == null || elements.length < 1) { // ��������Ƿ������ȷ
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
	

