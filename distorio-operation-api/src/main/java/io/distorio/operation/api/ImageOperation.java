package io.distorio.operation.api;

import java.util.Optional;
import javafx.scene.image.Image;

public interface ImageOperation {

  /**
   * @return metadata for dynamic registration (name, icon, hotkey, etc.)
   */
  OperationMetadata getMetadata();

  /**
   * Called to prepare the operation (e.g., user selection, drag handles). Returns true if
   * preparation is complete and operation can proceed.
   */
  boolean prepare(OperationContext context);

  /**
   * Called to preview the operation (e.g., show effect before confirmation).
   */
  void preview(OperationContext context);

  /**
   * Apply the operation to the image.
   */
  void apply(OperationContext context);

  /**
   * Undo the operation.
   */
  void undo(OperationContext context);

  /**
   * Redo the operation.
   */
  void redo(OperationContext context);

  /**
   * Metadata for an operation.
   */
  interface OperationMetadata {

    String getId();

    String getDisplayName();

    Optional<String> getIconPath();

    Optional<String> getHotkey();

    Optional<String> getMenuPath();
  }

  /**
   * Context for operation execution (image, selection, etc.).
   */
  interface OperationContext {
    /**
     * Get the current image. Expected type: javafx.scene.image.Image
     */
    Image getImage();
    /**
     * Set the current image. Expected type: javafx.scene.image.Image
     */
    void setImage(Image image);
  }
}
