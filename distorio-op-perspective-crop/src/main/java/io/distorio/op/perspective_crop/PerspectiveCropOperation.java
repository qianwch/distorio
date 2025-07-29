package io.distorio.op.perspective_crop;

import io.distorio.operation.api.ImageOperation;
import java.lang.reflect.Method;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

public class PerspectiveCropOperation implements ImageOperation {

  private Image originalImage; // Store original image for undo

  @Override
  public OperationMetadata getMetadata() {
    return new OperationMetadata() {
      @Override
      public String getId() {
        return "perspective_crop";
      }

      @Override
      public String getDisplayName() {
        return "Perspective Crop";
      }

      @Override
      public Optional<String> getIconPath() {
        return Optional.of("perspective_crop.svg");
      }

      @Override
      public Optional<String> getHotkey() {
        return Optional.of("Ctrl+P");
      }

      @Override
      public Optional<String> getMenuPath() {
        return Optional.of("Tools/Perspective Crop");
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
    // TODO: Show preview of perspective crop
  }

  @Override
  public void apply(OperationContext context) {
    try {
      Method getImage = context.getClass().getMethod("getImage");
      Method setImage = context.getClass().getMethod("setImage", Image.class);
      Method getSelectionX = context.getClass().getMethod("getSelectionX");
      Method getSelectionY = context.getClass().getMethod("getSelectionY");
      Method getSelectionWidth = context.getClass().getMethod("getSelectionWidth");
      Method getSelectionHeight = context.getClass().getMethod("getSelectionHeight");
      
      Image src = (Image) getImage.invoke(context);
      if (src == null) return;
      
      // Store original image for undo
      originalImage = src;
      
      double x = (double) getSelectionX.invoke(context);
      double y = (double) getSelectionY.invoke(context);
      double w = (double) getSelectionWidth.invoke(context);
      double h = (double) getSelectionHeight.invoke(context);
      
      System.out.println("Perspective crop selection: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
      
      // For now, just perform a regular crop as a placeholder for perspective crop
      if (w > 0 && h > 0) {
        int cropX = (int) x;
        int cropY = (int) y;
        int cropWidth = (int) w;
        int cropHeight = (int) h;
        
        // Ensure crop bounds are within image bounds
        int imgWidth = (int) src.getWidth();
        int imgHeight = (int) src.getHeight();
        
        cropX = Math.max(0, Math.min(cropX, imgWidth - 1));
        cropY = Math.max(0, Math.min(cropY, imgHeight - 1));
        cropWidth = Math.min(cropWidth, imgWidth - cropX);
        cropHeight = Math.min(cropHeight, imgHeight - cropY);
        
        if (cropWidth > 0 && cropHeight > 0) {
          // Perform the crop
          PixelReader reader = src.getPixelReader();
          WritableImage cropped = new WritableImage(cropWidth, cropHeight);
          PixelWriter writer = cropped.getPixelWriter();
          
          for (int cy = 0; cy < cropHeight; cy++) {
            for (int cx = 0; cx < cropWidth; cx++) {
              int argb = reader.getArgb(cropX + cx, cropY + cy);
              writer.setArgb(cx, cy, argb);
            }
          }
          
          setImage.invoke(context, cropped);
          System.out.println("Perspective crop applied: " + cropWidth + "x" + cropHeight);
        }
      }
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
        System.out.println("Undo perspective crop operation");
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
      Method getSelectionX = context.getClass().getMethod("getSelectionX");
      Method getSelectionY = context.getClass().getMethod("getSelectionY");
      Method getSelectionWidth = context.getClass().getMethod("getSelectionWidth");
      Method getSelectionHeight = context.getClass().getMethod("getSelectionHeight");
      
      Image src = (Image) getImage.invoke(context);
      if (src == null) return;
      
      double x = (double) getSelectionX.invoke(context);
      double y = (double) getSelectionY.invoke(context);
      double w = (double) getSelectionWidth.invoke(context);
      double h = (double) getSelectionHeight.invoke(context);
      
      // For now, just perform a regular crop as a placeholder for perspective crop
      if (w > 0 && h > 0) {
        int cropX = (int) x;
        int cropY = (int) y;
        int cropWidth = (int) w;
        int cropHeight = (int) h;
        
        // Ensure crop bounds are within image bounds
        int imgWidth = (int) src.getWidth();
        int imgHeight = (int) src.getHeight();
        
        cropX = Math.max(0, Math.min(cropX, imgWidth - 1));
        cropY = Math.max(0, Math.min(cropY, imgHeight - 1));
        cropWidth = Math.min(cropWidth, imgWidth - cropX);
        cropHeight = Math.min(cropHeight, imgHeight - cropY);
        
        if (cropWidth > 0 && cropHeight > 0) {
          // Perform the crop
          PixelReader reader = src.getPixelReader();
          WritableImage cropped = new WritableImage(cropWidth, cropHeight);
          PixelWriter writer = cropped.getPixelWriter();
          
          for (int cy = 0; cy < cropHeight; cy++) {
            for (int cx = 0; cx < cropWidth; cx++) {
              int argb = reader.getArgb(cropX + cx, cropY + cy);
              writer.setArgb(cx, cy, argb);
            }
          }
          
          setImage.invoke(context, cropped);
          System.out.println("Redo perspective crop operation");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
