package com.bonjava.stack;

import java.util.Observer;

public interface subject {
	//添加观察者
    void addObserver(ObserverBonjava obj);
    //移除观察者
    void deleteObserver(ObserverBonjava obj);
    //当主题方法改变时,这个方法被调用,通知所有的观察者
    void notifyObserver();
}
