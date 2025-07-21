package io.distorio.op.crop;

import io.distorio.operation.api.ImageOperation;
import java.lang.reflect.Method;
import java.util.Optional;

public class CropOperation implements ImageOperation {

  @Override
  public OperationMetadata getMetadata() {
    return new OperationMetadata() {
      @Override
      public String getId() {
        return "crop";
      }

      @Override
      public String getDisplayName() {
        return "Crop";
      }

      @Override
      public Optional<String> getIconPath() {
        return Optional.of("crop.svg");
      }

      @Override
      public Optional<String> getHotkey() {
        return Optional.of("Ctrl+K");
      }

      @Override
      public Optional<String> getMenuPath() {
        return Optional.of("Tools/Crop");
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
    // Print selection bounds
    try {
      Method getSelectionX = context.getClass().getMethod("getSelectionX");
      Method getSelectionY = context.getClass().getMethod("getSelectionY");
      Method getSelectionWidth = context.getClass()
        .getMethod("getSelectionWidth");
      Method getSelectionHeight = context.getClass()
        .getMethod("getSelectionHeight");
      double x = (double) getSelectionX.invoke(context);
      double y = (double) getSelectionY.invoke(context);
      double w = (double) getSelectionWidth.invoke(context);
      double h = (double) getSelectionHeight.invoke(context);
      System.out.println("Crop selection: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
    } catch (Exception e) {
      System.out.println("CropOperation: Unable to get selection from context");
    }
  }

  @Override
  public void undo(OperationContext context) {
  }

  @Override
  public void redo(OperationContext context) {
  }
}
