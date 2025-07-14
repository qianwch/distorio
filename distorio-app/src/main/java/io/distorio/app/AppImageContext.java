package io.distorio.app;

import io.distorio.operation.api.ImageOperation;
import javafx.scene.image.Image;

public class AppImageContext implements ImageOperation.OperationContext {

  private Image image;
  private double selectionX, selectionY, selectionWidth, selectionHeight;
  private java.io.File imageFile;

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public double getSelectionX() {
    return selectionX;
  }

  public double getSelectionY() {
    return selectionY;
  }

  public double getSelectionWidth() {
    return selectionWidth;
  }

  public double getSelectionHeight() {
    return selectionHeight;
  }

  public void setSelection(double x, double y, double w, double h) {
    this.selectionX = x;
    this.selectionY = y;
    this.selectionWidth = w;
    this.selectionHeight = h;
  }

  public boolean hasSelection() {
    return selectionWidth > 0 && selectionHeight > 0;
  }

  public java.io.File getImageFile() {
    return imageFile;
  }

  public void setImageFile(java.io.File file) {
    this.imageFile = file;
  }
}
