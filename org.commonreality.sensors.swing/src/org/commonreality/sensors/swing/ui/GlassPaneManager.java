package org.commonreality.sensors.swing.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Window;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

public class GlassPaneManager
{

  private RootPaneContainer    _currentGPContainer;

  private Component            _originalGP;

  private AffineTransform      _transform = new AffineTransform();

  private EyeTrackingGlassPane _glassPane;

  public GlassPaneManager()
  {
    _glassPane = new EyeTrackingGlassPane();
  }

  public void setDebugShape(String id, String text, double[] location)
  {
    Graphics2D g2 = (Graphics2D) _glassPane.getGraphics();
    Font font = _glassPane.getFont();
    font = font.deriveFont(font.getSize() * 2);
    GlyphVector vector = font.createGlyphVector(g2.getFontRenderContext(),
        text);
    Shape textShape = vector.getOutline((int) location[0], (int) location[1]);
    setDebugShape(id, textShape);
  }

  public void setDebugShape(String id, Shape shape)
  {
    updateOffset();
    if (shape != null)
      _glassPane.debug(id, _transform.createTransformedShape(shape));
    else
      _glassPane.debug(id, null);

  }

  public void update(Point fixation)
  {
    Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .getFocusedWindow();

    if (window instanceof RootPaneContainer)
      updateInternal((RootPaneContainer) window, fixation);
  }

  protected void updateInternal(RootPaneContainer newContainer, Point fixation)
  {
    if (newContainer != _currentGPContainer)
    {
      detach(_currentGPContainer);
      attach(newContainer);
    }

    SwingUtilities.convertPointFromScreen(fixation, _glassPane.getParent());
    _glassPane.setFixation(fixation);
  }

  private void updateOffset()
  {
    Point offset = new Point();
    SwingUtilities.convertPointFromScreen(offset, _glassPane.getParent());
    _transform.setToTranslation(-offset.x, offset.y);
  }

  protected void detach(RootPaneContainer current)
  {
    if (_currentGPContainer != null)
      _currentGPContainer.setGlassPane(_originalGP);
    _originalGP = null;
    _currentGPContainer = null;
  }

  protected void attach(RootPaneContainer newContainer)
  {
    _currentGPContainer = newContainer;
    _originalGP = _currentGPContainer.getGlassPane();
    _currentGPContainer.setGlassPane(_glassPane);
    _glassPane.setVisible(true);
    _glassPane.repaint();
  }
}
