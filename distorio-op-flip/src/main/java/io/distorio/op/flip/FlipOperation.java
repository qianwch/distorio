package io.distorio.op.flip;

import io.distorio.operation.api.ImageOperation;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

public class FlipOperation implements ImageOperation {

  public enum Direction { LEFT, RIGHT }
  private final Direction direction;
  private Image originalImage; // Store original image for undo

  public FlipOperation(Direction direction) {
    this.direction = direction;
  }

  @Override
  public OperationMetadata getMetadata() {
    return new OperationMetadata() {
      @Override
      public String getId() {
        return direction == Direction.LEFT ? "flip_left" : "flip_right";
      }

      @Override
      public String getDisplayName() {
        return direction == Direction.LEFT ? "Flip Left" : "Flip Right";
      }

      @Override
      public Optional<String> getIconPath() {
        return Optional.of(direction == Direction.LEFT ? "META-INF/icons/flip_left.svg" : "META-INF/icons/flip_right.svg");
      }

      @Override
      public Optional<String> getHotkey() {
        return Optional.of(direction == Direction.LEFT ? "Ctrl+L" : "Ctrl+R");
      }

      @Override
      public Optional<String> getMenuPath() {
        return Optional.of("Tools/" + (direction == Direction.LEFT ? "Flip Left" : "Flip Right"));
      }
    };
  }

  @Override
  public boolean prepare(OperationContext context) {
    return true;
  }

  @Override
  public void preview(OperationContext context) {
  }

  @Override
  public void apply(OperationContext context) {
    try {
      java.lang.reflect.Method getImage = context.getClass().getMethod("getImage");
      java.lang.reflect.Method setImage = context.getClass().getMethod("setImage", Image.class);
      Image src = (Image) getImage.invoke(context);
      if (src == null) return;
      
      // Store original image for undo
      originalImage = src;
      
      int width = (int) src.getWidth();
      int height = (int) src.getHeight();
      PixelReader reader = src.getPixelReader();
      WritableImage rotated = new WritableImage(height, width); // Note swapped dimensions
      PixelWriter writer = rotated.getPixelWriter();
      if (direction == Direction.LEFT) {
        // 90° counterclockwise: (x, y) -> (y, width-1-x)
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            writer.setArgb(y, width - 1 - x, reader.getArgb(x, y));
          }
        }
      } else {
        // 90° clockwise: (x, y) -> (height-1-y, x)
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            writer.setArgb(height - 1 - y, x, reader.getArgb(x, y));
          }
        }
      }
      setImage.invoke(context, rotated);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void undo(OperationContext context) {
    try {
      java.lang.reflect.Method setImage = context.getClass().getMethod("setImage", Image.class);
      if (originalImage != null) {
        setImage.invoke(context, originalImage);
        System.out.println("Undo flip operation: " + (direction == Direction.LEFT ? "Left" : "Right"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void redo(OperationContext context) {
    try {
      java.lang.reflect.Method getImage = context.getClass().getMethod("getImage");
      java.lang.reflect.Method setImage = context.getClass().getMethod("setImage", Image.class);
      Image src = (Image) getImage.invoke(context);
      if (src == null) return;
      
      int width = (int) src.getWidth();
      int height = (int) src.getHeight();
      PixelReader reader = src.getPixelReader();
      WritableImage rotated = new WritableImage(height, width); // Note swapped dimensions
      PixelWriter writer = rotated.getPixelWriter();
      if (direction == Direction.LEFT) {
        // 90° counterclockwise: (x, y) -> (y, width-1-x)
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            writer.setArgb(y, width - 1 - x, reader.getArgb(x, y));
          }
        }
      } else {
        // 90° clockwise: (x, y) -> (height-1-y, x)
        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            writer.setArgb(height - 1 - y, x, reader.getArgb(x, y));
          }
        }
      }
      setImage.invoke(context, rotated);
      System.out.println("Redo flip operation: " + (direction == Direction.LEFT ? "Left" : "Right"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
