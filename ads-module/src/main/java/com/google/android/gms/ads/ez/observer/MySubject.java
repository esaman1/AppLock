package com.google.android.gms.ads.ez.observer;

import java.util.ArrayList;

public class MySubject {

    private ArrayList<MyObserver> observers = new ArrayList<MyObserver>();

    public void attach(MyObserver observer) {
        observers.add(observer);
    }

    public void detach(MyObserver observer) {
        observers.remove(observer);
    }

    public void notifyChange(String message) {
        for (MyObserver observer : observers) {
            observer.update(message);
        }
    }

    private static MySubject INSTANCE;


    public MySubject() {
        INSTANCE = this;
    }

    public static MySubject getInstance() {
        if (INSTANCE == null) {
            return new MySubject();
        }
        return INSTANCE;
    }
}
