package com.bonjava.stack;

import java.awt.image.BufferedImage;

public interface ObserverBonjava {
	//当主题状态改变时,会将一个String类型字符传入该方法的参数,每个观察者都需要实现该方法
    public void update(String info);
    public void update2(BufferedImage bgMImage);
}
