package com.taishi.speachbookbrowser;

import android.app.Application;

/**
 * Created by yamasakitaishi on 2016/12/08.
 */

public class MyApplication extends Application {

	private double globalSpeed = 1.0;
	private double globalPitch = 1.0;

	public double getGlobalSpeed() {
		return globalSpeed;
	}

	public void setGlobalSpeed(double globalSpeed) {
		this.globalSpeed = globalSpeed;
	}

	public double getGlobalPitch() {
		return globalPitch;
	}

	public void setGlobalPitch(double globalPitch) {
		this.globalPitch = globalPitch;
	}

}