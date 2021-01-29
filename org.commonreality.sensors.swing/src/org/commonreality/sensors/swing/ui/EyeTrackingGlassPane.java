package org.commonreality.sensors.swing.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;

public class EyeTrackingGlassPane extends JComponent
{

  private Point              _fixation;

  private int                _fixationRadius = 20;

  private int                _stroke         = 2;

  private Point              _offset;

  private Stroke             _basicStroke    = new BasicStroke(_stroke);

  private Map<String, Shape> _debugShapes    = new TreeMap<>();

  public EyeTrackingGlassPane()
  {
    _fixation = new Point();
    setOpaque(false);
    setBackground(new Color(0, 0, 0, 0));
  }

  public void debug(String id, Shape shape)
  {
    if (shape == null)
      _debugShapes.remove(id);
    else
      _debugShapes.put(id, shape);
    repaint();
  }

  public void setFixation(Point p)
  {
    _fixation = new Point(p);
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g)
  {

    Graphics2D g2 = (Graphics2D) g;
    // debug shapes in red
    g.setColor(Color.cyan);
    _debugShapes.forEach((id, shape) -> {
      g2.draw(shape);
      Rectangle bounds = shape.getBounds();
      g2.drawString(id, bounds.x, bounds.y);
    });

    // fixation cross
    g.setColor(Color.orange);

    // cross hair
    g.drawLine(_fixation.x - _fixationRadius, _fixation.y,
        _fixation.x + _fixationRadius, _fixation.y);
    g.drawLine(_fixation.x, _fixation.y - _fixationRadius, _fixation.x,
        _fixation.y + _fixationRadius);

    g2.setStroke(_basicStroke);
    g.drawOval(_fixation.x - _fixationRadius, _fixation.y - _fixationRadius,
        _fixationRadius * 2, _fixationRadius * 2);

  }
}
