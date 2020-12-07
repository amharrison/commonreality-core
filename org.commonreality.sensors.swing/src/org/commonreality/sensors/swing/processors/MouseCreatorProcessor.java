package org.commonreality.sensors.swing.processors;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IMutableObject;
import org.commonreality.sensors.base.IObjectProcessor;
import org.commonreality.sensors.base.impl.AbstractObjectCreator;
import org.commonreality.sensors.base.impl.DefaultObjectKey;
import org.commonreality.sensors.swing.internal.Coordinates;
import org.commonreality.sensors.swing.internal.MouseState;

public class MouseCreatorProcessor extends AbstractObjectCreator implements IObjectProcessor<DefaultObjectKey> {

	private Coordinates _coordinates;
	
	public MouseCreatorProcessor(Coordinates coordinates) {
		_coordinates = coordinates;
	}

	@Override
	public boolean handles(Object arg0) {
		return arg0 instanceof MouseState;
	}

	@Override
	public void deleted(DefaultObjectKey arg0) {
		
	}

	@Override
	public boolean handles(DefaultObjectKey arg0) {
		
		return arg0.getObject() instanceof MouseState;
	}

	@Override
	public void process(DefaultObjectKey key, IMutableObject afferentPercept) {
		
		afferentPercept.setProperty(IVisualPropertyHandler.IS_VISUAL, Boolean.TRUE);
		afferentPercept.setProperty(IVisualPropertyHandler.VISIBLE, Boolean.TRUE);
		
		//white rgba
		afferentPercept.setProperty(IVisualPropertyHandler.COLOR, new double[] {1,1,1,1});
		afferentPercept.setProperty(IVisualPropertyHandler.TYPE, new String[] {"cursor"});
		afferentPercept.setProperty(IVisualPropertyHandler.TOKEN, "cursor");
		
		Point point = ((MouseState)key.getObject()).getLocation();
		Rectangle rect = new Rectangle(point.x, point.y, 16, 16);
		Rectangle2D retino = _coordinates.toRetinotopic(rect, null);
		
		afferentPercept.setProperty(IVisualPropertyHandler.RETINAL_LOCATION, new double[] {retino.getCenterX(), retino.getCenterY()});
		afferentPercept.setProperty(IVisualPropertyHandler.RETINAL_SIZE, new double[] {retino.getWidth(), retino.getHeight()});
		
	}

	

}
