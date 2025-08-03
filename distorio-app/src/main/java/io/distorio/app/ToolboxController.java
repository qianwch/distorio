package io.distorio.app;

import java.util.ArrayList;
import java.util.List;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.OperationRegistry;
import io.distorio.ui.common.IconUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ToolboxController {
    private final VBox toolbox = new VBox();
    private final List<Button> toolboxButtons = new ArrayList<>();
    private final List<ImageOperation> toolboxOperations = new ArrayList<>();
    private MainWindow.IconMode iconMode = MainWindow.IconMode.ICON_TEXT;
    private Button handButton;
    private boolean handMode = false;

    public ToolboxController() {
        buildToolbox();
    }

    public VBox getToolbox() {
        return toolbox;
    }

    public void setIconMode(MainWindow.IconMode mode) {
        this.iconMode = mode;
        buildToolbox();
    }

    public void setHandMode(boolean hand) {
        this.handMode = hand;
        // Optionally update hand button state
    }

    public void buildToolbox() {
        toolboxButtons.clear();
        toolboxOperations.clear();
        toolbox.getChildren().clear();
        toolbox.setAlignment(Pos.TOP_CENTER);
        toolbox.setPrefWidth(100);
        // Add hand button (special case - not an operation)
        if (iconMode == MainWindow.IconMode.ICON_ONLY) {
            handButton = new Button(null, IconUtil.icon("META-INF/icons/hand.svg", 20));
            handButton.getStyleClass().add("toolbox-button-icon-only");
        } else {
            handButton = new Button("Hand", IconUtil.icon("META-INF/icons/hand.svg", 20));
            handButton.getStyleClass().add("toolbox-button");
        }
        toolbox.getChildren().add(handButton);
        // Add operation buttons
        List<OperationRegistry.OperationWithProvider> operationsWithProviders = OperationRegistry.loadAllOperationsWithProviders();
        for (OperationRegistry.OperationWithProvider opWithProvider : operationsWithProviders) {
            Button btn = OperationButtonFactory.createButton(opWithProvider, iconMode);
            toolboxButtons.add(btn);
            toolboxOperations.add(opWithProvider.getOperation());
            toolbox.getChildren().add(btn);
        }
        toolbox.setSpacing(4);
    }

    public Button getHandButton() {
        return handButton;
    }

    public List<Button> getToolboxButtons() {
        return toolboxButtons;
    }

    public List<ImageOperation> getToolboxOperations() {
        return toolboxOperations;
    }
} 