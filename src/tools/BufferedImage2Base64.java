package tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;


public class BufferedImage2Base64 {
	public static String bufferedImage2Base64(BufferedImage image){
		String imageString = null;
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    try {
	    	ImageIO.write(image, "jpg", bos);
	        byte[] imageBytes = bos.toByteArray();
	        BASE64Encoder encoder = new BASE64Encoder();
	        imageString = encoder.encode(imageBytes);
	        imageString = imageString.replaceAll("\n", "").replaceAll("\r", "");
	        bos.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return imageString;
	    
	}
}
