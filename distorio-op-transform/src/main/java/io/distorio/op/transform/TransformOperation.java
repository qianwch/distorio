package io.distorio.op.transform;

import io.distorio.operation.api.ImageOperation;
import java.util.Optional;

public class TransformOperation implements ImageOperation {

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
      System.out.println("Transform selection: x=" + x + ", y=" + y + ", w=" + w + ", h=" + h);
    } catch (Exception e) {
      System.out.println("TransformOperation: Unable to get selection from context");
    }
  }

  @Override
  public void undo(OperationContext context) {
  }

  @Override
  public void redo(OperationContext context) {
  }
}
