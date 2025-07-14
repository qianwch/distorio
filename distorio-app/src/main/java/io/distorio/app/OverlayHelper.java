package io.distorio.app;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OverlayHelper {

  private final Pane overlay;
  private Rectangle selectionRect;

  public OverlayHelper(Pane overlay) {
    this.overlay = overlay;
  }

  public void showSelectionRect(double x, double y, double w, double h) {
    clear();
    selectionRect = new Rectangle(x, y, w, h);
    selectionRect.setStroke(Color.BLUE);
    selectionRect.setStrokeWidth(2);
    selectionRect.setFill(Color.color(0, 0, 1, 0.15));
    overlay.getChildren().add(selectionRect);
  }

  public void clear() {
    overlay.getChildren().clear();
    selectionRect = null;
  }
}
