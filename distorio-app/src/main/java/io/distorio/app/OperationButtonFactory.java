package io.distorio.app;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.OperationRegistry;
import io.distorio.ui.common.IconUtil;
import javafx.scene.control.Button;

public class OperationButtonFactory {

  public static Button createButton(ImageOperation op) {
    return createButton(op, MainWindow.IconMode.ICON_TEXT);
  }

  public static Button createButton(ImageOperation op, MainWindow.IconMode mode) {
    return createButton(op, mode, null);
  }

  public static Button createButton(ImageOperation op, MainWindow.IconMode mode, ClassLoader providerClassLoader) {
    String iconName = op.getMetadata().getIconPath().isPresent()? op.getMetadata().getIconPath().get() : null;
    
    Button btn;

    if (mode == MainWindow.IconMode.ICON_ONLY) {
      if (iconName != null) {
        btn = new Button(null, IconUtil.icon(iconName, 20, providerClassLoader));
        btn.getStyleClass().add("toolbox-button-icon-only");
      } else {
        // Fallback for operations without icons in ICON_ONLY mode
        btn = new Button("?");
        btn.getStyleClass().add("toolbox-button-icon-only");
      }
    } else {
      if (iconName != null) {
        btn = new Button(op.getMetadata().getDisplayName(), IconUtil.icon(iconName, 20, providerClassLoader));
        btn.getStyleClass().add("toolbox-button");
      } else {
        btn = new Button(op.getMetadata().getDisplayName());
        btn.getStyleClass().add("toolbox-button");
      }
      // Ensure text is set properly
      btn.setText(op.getMetadata().getDisplayName());
    }

    return btn;
  }

  /**
   * Create a button for an operation with provider information
   */
  public static Button createButton(OperationRegistry.OperationWithProvider opWithProvider, MainWindow.IconMode mode) {
    return createButton(opWithProvider.getOperation(), mode, opWithProvider.getProviderClassLoader());
  }
}
