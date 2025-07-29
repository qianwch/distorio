package io.distorio.op.transform;

import io.distorio.operation.api.ImageOperation;
import java.lang.reflect.Method;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

public class TransformOperation implements ImageOperation {

  private Image originalImage; // Store original image for undo

  @Override
  public OperationMetadata getMetadata() {
    return new OperationMetadata() {
      @Override
      public String getId() {
        return "transform";
      }

      @Override
      public String getDisplayName() {
        return "Transform";
      }

      @Override
      public Optional<String> getIconPath() {
        return Optional.of("aspect_ratio.svg");
      }

      @Override
      public Optional<String> getHotkey() {
        return Optional.of("Ctrl+T");
      }

      @Override
      public Optional<String> getMenuPath() {
        return Optional.of("Tools/Transform");
      }
    };
  }

  @Override
  public boolean prepare(OperationContext context) {
    // Always ready for now
    return true;
  }

  @Override
  public void preview(OperationContext context) {
  }

  @Override
  public void apply(OperationContext context) {
    try {
      Method getImage = context.getClass().getMethod("getImage");
      Method setImage = context.getClass().getMethod("setImage", Image.class);
      
      Image src = (Image) getImage.invoke(context);
      if (src == null) return;
      
      // Store original image for undo
      originalImage = src;
      
      Method getSelectionX = context.getClass().getMethod("getSelectionX");
      Method getSelectionY = context.getClass().getMethod("getSelectionY");
      Method getSelectionWidth = context.getClass().getMethod("getSelectionWidth");
      Method getSelectionHeight = context.getClass().getMethod("getSelectionHeight");
      
      double x = (double) getSelectionX.invoke(context);
      double y = (double) getSelectionY.invoke(context);
      double w = (double) getSelectionWidth.invoke(context);
      double h = (double) getSelectionHeight.invoke(context);
      
      System.out.println("Transform selection: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
      
      // For now, just resize the image to 200% as a simple transform
      int newWidth = (int) (src.getWidth() * 2);
      int newHeight = (int) (src.getHeight() * 2);
      
      PixelReader reader = src.getPixelReader();
      WritableImage transformed = new WritableImage(newWidth, newHeight);
      PixelWriter writer = transformed.getPixelWriter();
      
      // Simple nearest neighbor scaling
      for (int ty = 0; ty < newHeight; ty++) {
        for (int tx = 0; tx < newWidth; tx++) {
          int srcX = (int) (tx * src.getWidth() / newWidth);
          int srcY = (int) (ty * src.getHeight() / newHeight);
          int argb = reader.getArgb(srcX, srcY);
          writer.setArgb(tx, ty, argb);
        }
      }
      
      setImage.invoke(context, transformed);
      System.out.println("Transform applied: " + newWidth + "x" + newHeight);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void undo(OperationContext context) {
    try {
      Method setImage = context.getClass().getMethod("setImage", Image.class);
      if (originalImage != null) {
        setImage.invoke(context, originalImage);
        System.out.println("Undo transform operation");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void redo(OperationContext context) {
    try {
      Method getImage = context.getClass().getMethod("getImage");
      Method setImage = context.getClass().getMethod("setImage", Image.class);
      
      Image src = (Image) getImage.invoke(context);
      if (src == null) return;
      
      // For now, just resize the image to 200% as a simple transform
      int newWidth = (int) (src.getWidth() * 2);
      int newHeight = (int) (src.getHeight() * 2);
      
      PixelReader reader = src.getPixelReader();
      WritableImage transformed = new WritableImage(newWidth, newHeight);
      PixelWriter writer = transformed.getPixelWriter();
      
      // Simple nearest neighbor scaling
      for (int ty = 0; ty < newHeight; ty++) {
        for (int tx = 0; tx < newWidth; tx++) {
          int srcX = (int) (tx * src.getWidth() / newWidth);
          int srcY = (int) (ty * src.getHeight() / newHeight);
          int argb = reader.getArgb(srcX, srcY);
          writer.setArgb(tx, ty, argb);
        }
      }
      
      setImage.invoke(context, transformed);
      System.out.println("Redo transform operation");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
