package org.commonreality.sensors.swing.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;

public class EyeTrackingGlassPane extends JComponent {

	private Point _fixation;
	private int _fixationRadius = 20;
	private int _stroke = 2;

	public EyeTrackingGlassPane() {
		_fixation = new Point();
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
	}

	public void setFixation(Point p) {
		_fixation = new Point(p);
		repaint();
	}

	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		g.setColor(Color.orange);
		
		//cross hair
		g.drawLine(_fixation.x-_fixationRadius, _fixation.y, _fixation.x+_fixationRadius, _fixation.y);
		g.drawLine(_fixation.x, _fixation.y-_fixationRadius, _fixation.x, _fixation.y+_fixationRadius);
		
		
		
		g2.setStroke( new BasicStroke(_stroke));
		g.drawOval(_fixation.x - _fixationRadius, _fixation.y - _fixationRadius, _fixationRadius * 2,
				_fixationRadius * 2);
		
		
	}
}
