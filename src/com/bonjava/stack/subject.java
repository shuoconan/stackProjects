package com.bonjava.stack;

import java.util.Observer;

public interface subject {
	//��ӹ۲���
    void addObserver(ObserverBonjava obj);
    //�Ƴ��۲���
    void deleteObserver(ObserverBonjava obj);
    //�����ⷽ���ı�ʱ,�������������,֪ͨ���еĹ۲���
    void notifyObserver();
}
