package io.distorio.op.flip;

import io.distorio.operation.api.ImageOperation;
import java.util.Optional;

public class FlipOperation implements ImageOperation {

  private boolean flipHorizontal = true; // Default to horizontal flip

  public FlipOperation() {
  }

  public FlipOperation(boolean horizontal) {
    this.flipHorizontal = horizontal;
  }

  @Override
  public OperationMetadata getMetadata() {
    return new OperationMetadata() {
      @Override
      public String getId() {
        return "flip";
      }

      @Override
      public String getDisplayName() {
        return flipHorizontal ? "Flip Horizontal" : "Flip Vertical";
      }

      @Override
      public Optional<String> getIconPath() {
        return Optional.of(flipHorizontal ? "META-INF/icons/flip_left.svg" : "META-INF/icons/flip_right.svg");
      }

      @Override
      public Optional<String> getHotkey() {
        return Optional.of(flipHorizontal ? "Ctrl+H" : "Ctrl+V");
      }

      @Override
      public Optional<String> getMenuPath() {
        return Optional.of("Tools/" + (flipHorizontal ? "Flip Horizontal" : "Flip Vertical"));
      }
    };
  }

  @Override
  public boolean prepare(OperationContext context) {
    // Always ready for flip operations
    return true;
  }

  @Override
  public void preview(OperationContext context) {
    // TODO: Show preview of flipped image
  }

  @Override
  public void apply(OperationContext context) {
    // TODO: Actually flip the image
    System.out.println("Flip operation applied: " + (flipHorizontal ? "Horizontal" : "Vertical"));
  }

  @Override
  public void undo(OperationContext context) {
    // TODO: Undo the flip operation
    System.out.println("Undo flip operation: " + (flipHorizontal ? "Horizontal" : "Vertical"));
  }

  @Override
  public void redo(OperationContext context) {
    // TODO: Redo the flip operation
    System.out.println("Redo flip operation: " + (flipHorizontal ? "Horizontal" : "Vertical"));
  }
}
