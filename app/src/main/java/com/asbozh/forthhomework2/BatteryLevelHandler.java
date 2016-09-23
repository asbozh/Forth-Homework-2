package com.asbozh.forthhomework2;

import java.util.Observable;


public class BatteryLevelHandler extends Observable {

    public static BatteryLevelHandler handler;

    public static  BatteryLevelHandler getInstance() {
        if (handler == null) {
            handler = new BatteryLevelHandler();
        }
        return handler;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void updateValue(Object data) {

        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
