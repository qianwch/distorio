package io.distorio.app;

import io.distorio.ui.common.I18n;
import io.distorio.ui.common.IconUtil;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import java.util.HashMap;
import java.util.Map;

public class ToolbarController {
    private final ToolBar toolBar = new ToolBar();
    private final Map<String, Button> buttons = new HashMap<>();
    private MainWindow.IconMode iconMode = MainWindow.IconMode.ICON_TEXT;

    public ToolbarController(MainWindow.IconMode iconMode, String modSymbol) {
        this.iconMode = iconMode;
        buildToolbar(modSymbol);
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public Button getButton(String key) {
        return buttons.get(key);
    }

    public void setButtonAction(String key, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = buttons.get(key);
        if (btn != null) {
            btn.setOnAction(handler);
        }
    }

    public void setIconMode(MainWindow.IconMode mode) {
        this.iconMode = mode;
        // Use a default modSymbol for now; MainWindow should call buildToolbar with correct symbol if needed
        buildToolbar("^");
    }

    public void rebuildToolbar() {
        // Use a default modSymbol for now; MainWindow should call buildToolbar with correct symbol if needed
        buildToolbar("^");
    }

    public void buildToolbar(String modSymbol) {
        toolBar.getItems().clear();
        buttons.clear();
        addButton("open", "toolbar.open", "META-INF/icons/file_open.svg", "Open (" + modSymbol + "O)");
        addButton("close", "toolbar.close", "META-INF/icons/file_close.svg", "Close (" + modSymbol + "W)");
        addButton("save", "toolbar.save", "META-INF/icons/file_save.svg", "Save (" + modSymbol + "S)");
        addButton("saveas", "toolbar.saveas", "META-INF/icons/file_save_as.svg", "Save As (" + modSymbol + "⇧S)");
        addButton("copy", "toolbar.copy", "META-INF/icons/copy.svg", "Copy (" + modSymbol + "C)");
        addButton("paste", "toolbar.paste", "META-INF/icons/paste.svg", "Paste (" + modSymbol + "V)");
        addButton("undo", "toolbar.undo", "META-INF/icons/undo.svg", "Undo (" + modSymbol + "Z)");
        addButton("redo", "toolbar.redo", "META-INF/icons/redo.svg", "Redo (" + modSymbol + "Y)");
    }

    private void addButton(String key, String i18nKey, String iconPath, String tooltipText) {
        Button btn;
        if (iconMode == MainWindow.IconMode.ICON_ONLY) {
            btn = new Button(null, IconUtil.icon(iconPath, 20));
            btn.getStyleClass().add("toolbar-button-icon-only");
        } else {
            btn = new Button(I18n.get(i18nKey), IconUtil.icon(iconPath, 20));
            btn.getStyleClass().add("toolbar-button");
        }
        btn.setTooltip(new Tooltip(tooltipText));
        buttons.put(key, btn);
        toolBar.getItems().add(btn);
    }
} 