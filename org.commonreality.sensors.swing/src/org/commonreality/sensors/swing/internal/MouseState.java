package org.commonreality.sensors.swing.internal;

import java.awt.MouseInfo;
import java.awt.Point;

public class MouseState {

	public MouseState() {
	}
	
	public Point getLocation() {
		return MouseInfo.getPointerInfo().getLocation();
	}

}
