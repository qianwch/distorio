package io.distorio.app;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import io.distorio.ui.common.I18n;

public class StatusBarController {
    private final HBox statusBar = new HBox();
    private final Slider zoomSlider = new Slider(5, 600, 100);
    private final Label zoomPercentLabel = new Label();
    private double zoom = 1.0;

    public StatusBarController() {
        statusBar.getChildren().clear();
        statusBar.getChildren().add(new Label(I18n.get("status.zoom")));
        statusBar.getChildren().add(zoomSlider);
        statusBar.getChildren().add(zoomPercentLabel);
        statusBar.setSpacing(8);
        updateZoomPercentLabel();
        zoomSlider.setValue(100);
        zoomSlider.valueProperty().addListener((obs, oldV, newV) -> {
            setZoom(newV.doubleValue() / 100.0);
        });
    }

    public HBox getStatusBar() {
        return statusBar;
    }

    public Slider getZoomSlider() {
        return zoomSlider;
    }

    public Label getZoomPercentLabel() {
        return zoomPercentLabel;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double z) {
        zoom = Math.max(0.05, Math.min(z, 6.0));
        updateZoomPercentLabel();
    }

    public void updateZoomPercentLabel() {
        int percent = (int) Math.round(zoom * 100);
        zoomPercentLabel.setText(percent + "%");
    }
} 