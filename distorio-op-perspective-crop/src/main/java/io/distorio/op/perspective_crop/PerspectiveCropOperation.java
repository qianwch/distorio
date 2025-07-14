package io.distorio.op.perspective_crop;

import io.distorio.operation.api.ImageOperation;
import java.util.Optional;

public class PerspectiveCropOperation implements ImageOperation {

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
    // Print selection bounds
    try {
      java.lang.reflect.Method getSelectionX = context.getClass().getMethod("getSelectionX");
      java.lang.reflect.Method getSelectionY = context.getClass().getMethod("getSelectionY");
      java.lang.reflect.Method getSelectionWidth = context.getClass()
        .getMethod("getSelectionWidth");
      java.lang.reflect.Method getSelectionHeight = context.getClass()
        .getMethod("getSelectionHeight");
      double x = (double) getSelectionX.invoke(context);
      double y = (double) getSelectionY.invoke(context);
      double w = (double) getSelectionWidth.invoke(context);
      double h = (double) getSelectionHeight.invoke(context);
      System.out.println(
        "Perspective crop selection: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
    } catch (Exception e) {
      System.out.println("PerspectiveCropOperation: Unable to get selection from context");
    }
  }

  @Override
  public void undo(OperationContext context) {
  }

  @Override
  public void redo(OperationContext context) {
  }
}
