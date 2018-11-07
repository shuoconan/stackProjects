package tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageManipulate {
	 /**
     * ��ͼƬ�������� 
     * @param originalImage ԭʼͼƬ
     * @param times �Ŵ���
     * @return
     */
    public static BufferedImage resizeImage(BufferedImage  originalImage, double times){
        int width = (int)(originalImage.getWidth()*times);
        int height = (int)(originalImage.getHeight()*times);
        int tType = originalImage.getType(); 
        if(0 == tType){
        	tType = 5; 
        }
        BufferedImage newImage = new BufferedImage(width,height, tType);
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0,0,width,height,null);
        g.dispose();
        return newImage;
    }
}
