package io.distorio.app;

import io.distorio.core.FileService;
import io.distorio.core.ThemeService;
import io.distorio.core.HistoryService;
import io.distorio.core.ImageContext;
import io.distorio.core.ConfigService;
import io.distorio.core.LoggingService;
import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.OperationRegistry;
import io.distorio.ui.common.I18n;
import io.distorio.ui.common.IconUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.Tooltip;
import javax.imageio.ImageIO;

public class MainWindow {

  private final BorderPane root = new BorderPane();
  private final MenuBar menuBar = new MenuBar();
  private final ScrollPane scrollPane = new ScrollPane();
  private final StackPane mainArea = new StackPane();
  private final HistoryService operationHistory = new HistoryService();
  private final ImageContext imageContext = new ImageContext();
  private final ImageView imageView = new ImageView();
  private final Pane overlayPane = new Pane();
  private final Slider zoomSlider = new Slider(5, 600, 100);
  private final Label zoomPercentLabel = new Label();
  private final OverlayHelper overlayHelper;
  private final Stage stage;
  private IconMode iconMode = IconMode.ICON_TEXT;
  private double zoom = 1.0;
  private boolean handMode = false;
  private Button handButton;
  private double selectionStartX, selectionStartY;
  private double selectionEndX, selectionEndY;
  private boolean isSelecting = false;
  private boolean isCentering = false;
  private boolean dirty = false;
  private final StatusBarController statusBarController = new StatusBarController();
  private final ToolboxController toolboxController = new ToolboxController();
  private final ToolbarController toolbarController;

  public MainWindow(Stage stage) {
    this.stage = stage;
    // Detect Mac OS for shortcut key
    boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
    String mod = isMac ? "Meta" : "Ctrl";
    String modSymbol = isMac ? "⌘" : "^";

    // Menu bar
    Menu fileMenu = new Menu(I18n.get("menu.file"));
    MenuItem openItem = new MenuItem(I18n.get("toolbar.open"));
    openItem.setAccelerator(KeyCombination.keyCombination(mod + "+O"));
    MenuItem closeItem = new MenuItem(I18n.get("toolbar.close"));
    closeItem.setAccelerator(KeyCombination.keyCombination(mod + "+W"));
    MenuItem saveItem = new MenuItem(I18n.get("toolbar.save"));
    saveItem.setAccelerator(KeyCombination.keyCombination(mod + "+S"));
    MenuItem saveAsItem = new MenuItem(I18n.get("toolbar.saveas"));
    saveAsItem.setAccelerator(KeyCombination.keyCombination(mod + "+Shift+S"));
    MenuItem exitItem = new MenuItem(I18n.get("menu.exit"));
    fileMenu.getItems().addAll(openItem, closeItem, new SeparatorMenuItem(), saveItem, saveAsItem,
        new SeparatorMenuItem(), exitItem);

    Menu editMenu = new Menu(I18n.get("menu.edit"));
    MenuItem undoItem = new MenuItem(I18n.get("toolbar.undo"));
    undoItem.setAccelerator(KeyCombination.keyCombination(mod + "+Z"));
    MenuItem redoItem = new MenuItem(I18n.get("toolbar.redo"));
    redoItem.setAccelerator(KeyCombination.keyCombination(mod + "+Y"));
    MenuItem copyItem = new MenuItem(I18n.get("toolbar.copy"));
    copyItem.setAccelerator(KeyCombination.keyCombination(mod + "+C"));
    MenuItem pasteItem = new MenuItem(I18n.get("toolbar.paste"));
    pasteItem.setAccelerator(KeyCombination.keyCombination(mod + "+V"));
    editMenu.getItems().addAll(undoItem, redoItem, new SeparatorMenuItem(), copyItem, pasteItem);

    // Replace static tools menu with dynamic
    Menu toolsMenu = buildToolsMenu();

    Menu viewMenu = new Menu(I18n.get("menu.view"));
    RadioMenuItem iconOnly = new RadioMenuItem(I18n.get("menu.icon_only"));
    RadioMenuItem iconText = new RadioMenuItem(I18n.get("menu.icon_text"));
    ToggleGroup iconModeGroup = new ToggleGroup();
    iconOnly.setToggleGroup(iconModeGroup);
    iconText.setToggleGroup(iconModeGroup);
    iconText.setSelected(true);
    viewMenu.getItems().addAll(iconOnly, iconText);
    iconOnly.setOnAction(e -> setIconMode(IconMode.ICON_ONLY));
    iconText.setOnAction(e -> setIconMode(IconMode.ICON_TEXT));

    // Theme switching
    Menu themeMenu = new Menu(I18n.get("menu.theme"));
    RadioMenuItem lightTheme = new RadioMenuItem(I18n.get("menu.theme_light"));
    RadioMenuItem darkTheme = new RadioMenuItem(I18n.get("menu.theme_dark"));
    RadioMenuItem systemTheme = new RadioMenuItem(I18n.get("menu.theme_system"));
    ToggleGroup themeGroup = new ToggleGroup();
    lightTheme.setToggleGroup(themeGroup);
    darkTheme.setToggleGroup(themeGroup);
    systemTheme.setToggleGroup(themeGroup);
    systemTheme.setSelected(true);
    themeMenu.getItems().addAll(lightTheme, darkTheme, systemTheme);
    lightTheme.setOnAction(e -> setTheme(ThemeService.Theme.LIGHT));
    darkTheme.setOnAction(e -> setTheme(ThemeService.Theme.DARK));
    systemTheme.setOnAction(e -> setTheme(ThemeService.Theme.SYSTEM));

    // Language switching
    Menu langMenu = new Menu(I18n.get("menu.language"));
    RadioMenuItem en = new RadioMenuItem(I18n.get("menu.language_english"));
    RadioMenuItem zh = new RadioMenuItem(I18n.get("menu.language_chinese"));
    ToggleGroup langGroup = new ToggleGroup();
    en.setToggleGroup(langGroup);
    zh.setToggleGroup(langGroup);
    en.setSelected(true);
    langMenu.getItems().addAll(en, zh);
    en.setOnAction(e -> setLanguage("en"));
    zh.setOnAction(e -> setLanguage("zh"));

    viewMenu.getItems().add(new SeparatorMenuItem());
    viewMenu.getItems().add(themeMenu);
    viewMenu.getItems().add(langMenu);

    Menu helpMenu = new Menu(I18n.get("menu.help"));
    MenuItem aboutItem = new MenuItem(I18n.get("menu.about"));
    helpMenu.getItems().add(aboutItem);

    menuBar.getMenus().clear();
    menuBar.getMenus().addAll(fileMenu, editMenu, toolsMenu, viewMenu, helpMenu);

    // Add menu item actions
    openItem.setOnAction((ActionEvent e) -> handleOpen(stage));
    closeItem.setOnAction((ActionEvent e) -> handleClose());
    saveItem.setOnAction((ActionEvent e) -> handleSave());
    saveAsItem.setOnAction((ActionEvent e) -> handleSaveAs());
    undoItem.setOnAction((ActionEvent e) -> handleUndo());
    redoItem.setOnAction((ActionEvent e) -> handleRedo());
    copyItem.setOnAction((ActionEvent e) -> handleCopy());
    pasteItem.setOnAction((ActionEvent e) -> handlePaste());
    // handItem is now handled in buildToolsMenu()

    // Toolbar
    toolbarController = new ToolbarController(iconMode, modSymbol);
    toolbarController.setButtonAction("open", e -> handleOpen(stage));
    toolbarController.setButtonAction("close", e -> handleClose());
    toolbarController.setButtonAction("save", e -> handleSave());
    toolbarController.setButtonAction("saveas", e -> handleSaveAs());
    toolbarController.setButtonAction("copy", e -> handleCopy());
    toolbarController.setButtonAction("paste", e -> handlePaste());
    toolbarController.setButtonAction("undo", e -> handleUndo());
    toolbarController.setButtonAction("redo", e -> handleRedo());

    // Toolbox (dynamic)
    toolboxController.buildToolbox();
    // Set action handlers for operation buttons
    List<Button> opButtons = toolboxController.getToolboxButtons();
    List<ImageOperation> opList = toolboxController.getToolboxOperations();
    for (int i = 0; i < opButtons.size(); i++) {
        Button btn = opButtons.get(i);
        ImageOperation op = opList.get(i);
        btn.setOnAction(e -> handleOperation(op));
    }
    // Set action handler for hand button
    toolboxController.getHandButton().setOnAction(e -> setHandMode(!handMode));
    toolboxController.getToolbox().getStyleClass().add("toolbox");
    toolboxController.getToolbox().setAlignment(Pos.TOP_CENTER); // Align buttons to top
    toolboxController.getToolbox().setPrefWidth(100); // Make toolbox narrower

    // Status bar (placeholder)
    statusBarController.getStatusBar().getChildren().clear();
    statusBarController.getStatusBar().getChildren().add(new Label(I18n.get("status.zoom")));
    statusBarController.getStatusBar().getChildren().add(zoomSlider);
    statusBarController.getStatusBar().getChildren().add(zoomPercentLabel);
    statusBarController.getStatusBar().setSpacing(8);
    statusBarController.getStatusBar().getStyleClass().add("status-bar");
    zoomSlider.setValue(100);
    zoomSlider.valueProperty().addListener((obs, oldV, newV) -> {
      setZoom(newV.doubleValue() / 100.0);
    });
    updateZoomPercentLabel();

    // Main area with scroll pane to handle zoom properly
    mainArea.getChildren().clear();

    // Use a simple pane as container for better control
    Pane imageContainer = new Pane();
    imageContainer.getChildren().add(imageView);

    scrollPane.setContent(imageContainer);
    scrollPane.setFitToWidth(false);
    scrollPane.setFitToHeight(false);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setPannable(false); // Start with panning disabled
    mainArea.getChildren().addAll(scrollPane, overlayPane);

    // Add listener to viewport bounds to ensure centering (with debouncing)
    scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
      if (imageView.getImage() != null && oldBounds != null && newBounds != null) {
        // Only center if the viewport actually changed significantly
        if (Math.abs(oldBounds.getWidth() - newBounds.getWidth()) > 1 ||
            Math.abs(oldBounds.getHeight() - newBounds.getHeight()) > 1) {
          // Use a longer delay for viewport changes
          Platform.runLater(() -> {
            Platform.runLater(() -> {
              centerImageInViewport();
            });
          });
        }
      }
    });

    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    imageView.setOnMouseClicked(e -> imageView.requestFocus());

    // Add listener to image view fit properties to ensure centering
    imageView.fitWidthProperty().addListener((obs, oldWidth, newWidth) -> {
      if (imageView.getImage() != null && oldWidth != null && newWidth != null) {
        if (Math.abs(oldWidth.doubleValue() - newWidth.doubleValue()) > 1) {
          Platform.runLater(() -> {
            centerImageInViewport();
          });
        }
      }
    });

    imageView.fitHeightProperty().addListener((obs, oldHeight, newHeight) -> {
      if (imageView.getImage() != null && oldHeight != null && newHeight != null) {
        if (Math.abs(oldHeight.doubleValue() - newHeight.doubleValue()) > 1) {
          Platform.runLater(() -> {
            centerImageInViewport();
          });
        }
      }
    });

    // Add listener to image bounds to ensure centering (with debouncing)
    imageView.boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
      if (imageView.getImage() != null && oldBounds != null && newBounds != null) {
        // Only center if the image bounds actually changed significantly
        if (Math.abs(oldBounds.getWidth() - newBounds.getWidth()) > 1 ||
            Math.abs(oldBounds.getHeight() - newBounds.getHeight()) > 1) {
          // Use a longer delay for image bounds changes
          Platform.runLater(() -> {
            Platform.runLater(() -> {
              centerImageInViewport();
            });
          });
        }
      }
    });

    // Mouse wheel zoom
    scrollPane.setOnScroll(e -> {
      if (e.isControlDown() || e.isMetaDown()) {
        double delta = e.getDeltaY() > 0 ? 1.1 : 0.9;
        double newZoom = Math.max(0.05, Math.min(zoom * delta, 6.0));

        setZoom(newZoom);
        zoomSlider.setValue(zoom * 100);

        e.consume();
      }
    });

    overlayPane.setPickOnBounds(false); // allow mouse events to pass through by default
    overlayPane.setMouseTransparent(true); // overlay is only interactive when needed
    overlayHelper = new OverlayHelper(overlayPane);

    // Selection mouse events
    overlayPane.setOnMousePressed(e -> {
      if (!handMode) {
        selectionStartX = e.getX();
        selectionStartY = e.getY();
        selectionEndX = selectionStartX;
        selectionEndY = selectionStartY;
        isSelecting = true;
        showSelectionRect(selectionStartX, selectionStartY, 0, 0);
      }
    });
    overlayPane.setOnMouseDragged(e -> {
      if (!handMode && isSelecting) {
        selectionEndX = e.getX();
        selectionEndY = e.getY();
        double x = Math.min(selectionStartX, selectionEndX);
        double y = Math.min(selectionStartY, selectionEndY);
        double w = Math.abs(selectionEndX - selectionStartX);
        double h = Math.abs(selectionEndY - selectionStartY);
        showSelectionRect(x, y, w, h);
      }
    });
    overlayPane.setOnMouseReleased(e -> {
      if (!handMode && isSelecting) {
        isSelecting = false;
        double x = Math.min(selectionStartX, selectionEndX);
        double y = Math.min(selectionStartY, selectionEndY);
        double w = Math.abs(selectionEndX - selectionStartX);
        double h = Math.abs(selectionEndY - selectionStartY);
        imageContext.setSelection(x, y, w, h);
        // TODO: notify current operation of selection
      }
    });

    // Layout
    VBox top = new VBox(menuBar, toolbarController.getToolBar());
    root.setTop(top);
    root.setLeft(toolboxController.getToolbox());
    root.setBottom(statusBarController.getStatusBar());
    root.setCenter(mainArea);

    // Scene
    Scene scene = new Scene(root, 1200, 800);
    stage.setScene(scene);

    // Drag-and-drop support for image files
    mainArea.setOnDragOver(event -> {
      if (event.getGestureSource() != mainArea && event.getDragboard().hasFiles()) {
        // Accept only if at least one file is an image
        boolean hasImage = event.getDragboard().getFiles().stream().anyMatch(f -> isImageFile(f));
        if (hasImage) {
          event.acceptTransferModes(TransferMode.COPY);
        }
      }
      event.consume();
    });
    mainArea.setOnDragDropped(event -> {
      var db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles()) {
        for (File file : db.getFiles()) {
          if (isImageFile(file)) {
            try {
              BufferedImage bufferedImage = ImageIO.read(file);
              if (bufferedImage != null) {
                Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                imageContext.setImage(img);
                imageContext.setImageFile(file);
                imageContext.setSelection(0, 0, 0, 0);
                updateImageView();
                clearOverlay();
                success = true;
              } else {
                // handle error: not a supported image
                try {
                  Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle(I18n.get("dialog.open_image_error"));
                  alert.setHeaderText(I18n.get("dialog.unsupported_format"));
                  alert.setContentText(I18n.get("dialog.dropped_file_error"));
                  alert.showAndWait();
                } catch (Exception e) {
                  System.err.println("Warning: Could not show error dialog: " + e.getMessage());
                }
              }
            } catch (Exception ex) {
              ex.printStackTrace();
              try {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(I18n.get("dialog.open_image_error"));
                alert.setHeaderText(I18n.get("dialog.failed_to_open"));
                alert.setContentText(I18n.get("dialog.dropped_file_failed") + "\n" + ex.getMessage());
                alert.showAndWait();
              } catch (Exception e) {
                System.err.println("Warning: Could not show error dialog: " + e.getMessage());
              }
            }
            break;
          }
        }
      }
      event.setDropCompleted(success);
      event.consume();
    });

    // Initialize theme manager
    ThemeService.setScene(scene);

    // Set initial window title
    updateWindowTitle();

    // Prevent app from closing if there are unsaved changes and user cancels
    stage.setOnCloseRequest(event -> {
      if (!confirmDiscardUnsavedChanges()) {
        event.consume(); // Prevent window from closing
      }
    });
  }

  private void setIconMode(IconMode mode) {
    this.iconMode = mode;
    // Update toolbar and toolbox using dynamic methods
    toolbarController.setIconMode(mode);
    toolboxController.buildToolbox();
  }

  private void setTheme(ThemeService.Theme theme) {
    ThemeService.setTheme(theme);
  }

  private void setLanguage(String lang) {
    if (lang.equals("en")) {
      I18n.setLocale(Locale.ENGLISH);
    } else if (lang.equals("zh")) {
      I18n.setLocale(Locale.CHINESE);
    }

    // Rebuild all UI components using dynamic methods
    rebuildAllUI();
  }

  /**
   * Rebuilds all UI components after language change
   */
  private void rebuildAllUI() {
    // Rebuild menu bar
    rebuildMenuBar();

    // Rebuild toolbar
    toolbarController.rebuildToolbar();

    // Rebuild toolbox
    toolboxController.buildToolbox();

    // Update status bar
    statusBarController.getStatusBar().getChildren().clear();
    statusBarController.getStatusBar().getChildren().add(new Label(I18n.get("status.zoom")));
    statusBarController.getStatusBar().getChildren().add(zoomSlider);
    statusBarController.getStatusBar().getChildren().add(zoomPercentLabel);
    statusBarController.getStatusBar().setSpacing(8);
    updateZoomPercentLabel();
  }

  /**
   * Rebuilds the menu bar with current language
   */
  private void rebuildMenuBar() {
    menuBar.getMenus().clear();

    // File menu
    Menu fileMenu = new Menu(I18n.get("menu.file"));
    MenuItem openItem = new MenuItem(I18n.get("toolbar.open"));
    MenuItem closeItem = new MenuItem(I18n.get("toolbar.close"));
    MenuItem saveItem = new MenuItem(I18n.get("toolbar.save"));
    MenuItem saveAsItem = new MenuItem(I18n.get("toolbar.saveas"));
    MenuItem exitItem = new MenuItem(I18n.get("menu.exit"));
    fileMenu.getItems().addAll(openItem, closeItem, new SeparatorMenuItem(), saveItem, saveAsItem,
        new SeparatorMenuItem(), exitItem);

    // Edit menu
    Menu editMenu = new Menu(I18n.get("menu.edit"));
    MenuItem undoItem = new MenuItem(I18n.get("toolbar.undo"));
    MenuItem redoItem = new MenuItem(I18n.get("toolbar.redo"));
    MenuItem copyItem = new MenuItem(I18n.get("toolbar.copy"));
    MenuItem pasteItem = new MenuItem(I18n.get("toolbar.paste"));
    editMenu.getItems().addAll(undoItem, redoItem, new SeparatorMenuItem(), copyItem, pasteItem);

    // Tools menu (dynamic)
    Menu toolsMenu = buildToolsMenu();

    // View menu
    Menu viewMenu = new Menu(I18n.get("menu.view"));
    RadioMenuItem iconOnly = new RadioMenuItem(I18n.get("menu.icon_only"));
    RadioMenuItem iconText = new RadioMenuItem(I18n.get("menu.icon_text"));
    ToggleGroup iconModeGroup = new ToggleGroup();
    iconOnly.setToggleGroup(iconModeGroup);
    iconText.setToggleGroup(iconModeGroup);
    if (iconMode == IconMode.ICON_ONLY) {
      iconOnly.setSelected(true);
    } else {
      iconText.setSelected(true);
    }
    viewMenu.getItems().addAll(iconOnly, iconText);
    iconOnly.setOnAction(e -> setIconMode(IconMode.ICON_ONLY));
    iconText.setOnAction(e -> setIconMode(IconMode.ICON_TEXT));

    // Theme switching
    Menu themeMenu = new Menu(I18n.get("menu.theme"));
    RadioMenuItem lightTheme = new RadioMenuItem(I18n.get("menu.theme_light"));
    RadioMenuItem darkTheme = new RadioMenuItem(I18n.get("menu.theme_dark"));
    RadioMenuItem systemTheme = new RadioMenuItem(I18n.get("menu.theme_system"));
    ToggleGroup themeGroup = new ToggleGroup();
    lightTheme.setToggleGroup(themeGroup);
    darkTheme.setToggleGroup(themeGroup);
    systemTheme.setToggleGroup(themeGroup);
    ThemeService.Theme currentTheme = ThemeService.getCurrentTheme();
    if (currentTheme == ThemeService.Theme.LIGHT) {
      lightTheme.setSelected(true);
    } else if (currentTheme == ThemeService.Theme.DARK) {
      darkTheme.setSelected(true);
    } else {
      systemTheme.setSelected(true);
    }
    themeMenu.getItems().addAll(lightTheme, darkTheme, systemTheme);
    lightTheme.setOnAction(e -> setTheme(ThemeService.Theme.LIGHT));
    darkTheme.setOnAction(e -> setTheme(ThemeService.Theme.DARK));
    systemTheme.setOnAction(e -> setTheme(ThemeService.Theme.SYSTEM));

    // Language switching
    Menu langMenu = new Menu(I18n.get("menu.language"));
    RadioMenuItem en = new RadioMenuItem(I18n.get("menu.language_english"));
    RadioMenuItem zh = new RadioMenuItem(I18n.get("menu.language_chinese"));
    ToggleGroup langGroup = new ToggleGroup();
    en.setToggleGroup(langGroup);
    zh.setToggleGroup(langGroup);
    if (I18n.getCurrentLocale().getLanguage().equals("zh")) {
      zh.setSelected(true);
    } else {
      en.setSelected(true);
    }
    langMenu.getItems().addAll(en, zh);
    en.setOnAction(e -> setLanguage("en"));
    zh.setOnAction(e -> setLanguage("zh"));

    viewMenu.getItems().add(new SeparatorMenuItem());
    viewMenu.getItems().add(themeMenu);
    viewMenu.getItems().add(langMenu);

    // Help menu
    Menu helpMenu = new Menu(I18n.get("menu.help"));
    MenuItem aboutItem = new MenuItem(I18n.get("menu.about"));
    helpMenu.getItems().add(aboutItem);

    menuBar.getMenus().addAll(fileMenu, editMenu, toolsMenu, viewMenu, helpMenu);

    // Reattach menu item actions
    openItem.setOnAction((ActionEvent e) -> handleOpen(null)); // TODO: pass stage reference
    undoItem.setOnAction((ActionEvent e) -> handleUndo());
    redoItem.setOnAction((ActionEvent e) -> handleRedo());
  }


  private void handleOperation(ImageOperation op) {
    // TODO: implement operation preparation, preview, apply, and undo/redo logic
    System.out.println("Operation invoked: " + op.getMetadata().getDisplayName());
    
    // Check if there's an image before applying operation
    if (imageContext.getImage() == null) {
      System.out.println("No image loaded, operation skipped: " + op.getMetadata().getDisplayName());
      return;
    }
    
    // Create a new operation instance to ensure each operation has its own state
    ImageOperation newOp = createNewOperationInstance(op);
    
    boolean ready = newOp.prepare(imageContext);
    if (ready) {
      newOp.preview(imageContext); // For now, just call preview
      newOp.apply(imageContext);
      operationHistory.push(newOp);
      updateImageView(); // Update display after operation
      dirty = true;
      updateWindowTitle();
    } else {
      // TODO: show preparation UI (e.g., selection, drag handles)
    }
  }

  /**
   * Creates a new instance of the given operation to ensure each operation has its own state
   */
  private ImageOperation createNewOperationInstance(ImageOperation originalOp) {
    String opId = originalOp.getMetadata().getId();
    
    // Use the registry to create a new instance using the provider system
    ImageOperation newOp = OperationRegistry.createOperationById(opId);
    
    if (newOp != null) {
      return newOp;
    } else {
      // Fallback: return the original operation if not found in registry
      System.err.println("Warning: Operation not found in registry: " + opId);
      return originalOp;
    }
  }

  private void handleUndo() {
    ImageOperation op = operationHistory.undo();
    if (op != null) {
      op.undo(imageContext);
      updateImageView();
      System.out.println("Undo: " + op.getMetadata().getDisplayName());
    }
  }

  private void handleRedo() {
    ImageOperation op = operationHistory.redo();
    if (op != null) {
      op.redo(imageContext);
      updateImageView();
      System.out.println("Redo: " + op.getMetadata().getDisplayName());
    }
  }

  private boolean confirmDiscardUnsavedChanges() {
    if (!dirty) {
      return true;
    }
    Alert alert = new Alert(
        AlertType.CONFIRMATION);
    alert.setTitle(I18n.get("dialog.unsaved_changes"));
    alert.setHeaderText(I18n.get("dialog.unsaved_changes_header"));
    alert.setContentText(I18n.get("dialog.unsaved_changes_content"));
    ButtonType save = new ButtonType(I18n.get("dialog.save"));
    ButtonType dontSave = new ButtonType(I18n.get("dialog.dont_save"));
    ButtonType cancel = new ButtonType(I18n.get("dialog.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(save, dontSave, cancel);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent()) {
      if (result.get() == save) {
        handleSave();
        return !dirty; // Only proceed if save succeeded
      } else if (result.get() == dontSave) {
        return true;
      }
    }
    return false; // Cancel or closed dialog
  }

  private void handleOpen(Stage stage) {
    if (!confirmDiscardUnsavedChanges()) {
      return;
    }
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(I18n.get("dialog.open_image"));
    fileChooser.getExtensionFilters().addAll(FileService.getImageExtensionFilters());
    File file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      try {
        BufferedImage bufferedImage = ImageIO.read(file);
        if (bufferedImage != null) {
          Image img = SwingFXUtils.toFXImage(bufferedImage, null);
          imageContext.setImage(img);
          imageContext.setImageFile(file);
          // Calculate initial zoom to fit image in viewport
          calculateInitialZoom(img);
          updateImageView();
          updateWindowTitle();
          dirty = false;
          updateWindowTitle();
        } else {
          // handle error: not a supported image
          try {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(I18n.get("dialog.open_image_error"));
            alert.setHeaderText(I18n.get("dialog.unsupported_format"));
            alert.setContentText(I18n.get("dialog.unsupported_format_content"));
            alert.showAndWait();
          } catch (Exception e) {
            System.err.println("Warning: Could not show error dialog: " + e.getMessage());
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        try {
          Alert alert = new Alert(AlertType.ERROR);
          alert.setTitle(I18n.get("dialog.open_image_error"));
          alert.setHeaderText(I18n.get("dialog.failed_to_open"));
          alert.setContentText(I18n.get("dialog.failed_to_open_content") + "\n" + ex.getMessage());
          alert.showAndWait();
        } catch (Exception e) {
          System.err.println("Warning: Could not show error dialog: " + e.getMessage());
        }
      }
    }
  }

  private void calculateInitialZoom(Image img) {
    if (img == null) {
      return;
    }

    // Wait for the layout to be updated to get viewport dimensions
    Platform.runLater(() -> {
      double viewportWidth = scrollPane.getViewportBounds().getWidth();
      double viewportHeight = scrollPane.getViewportBounds().getHeight();

      System.out.println(
          "Initial zoom calculation - Image: " + img.getWidth() + "x" + img.getHeight() +
              ", Viewport: " + viewportWidth + "x" + viewportHeight);

      if (viewportWidth > 0 && viewportHeight > 0) {
        // Calculate zoom to fit image in viewport
        double scaleX = viewportWidth / img.getWidth();
        double scaleY = viewportHeight / img.getHeight();
        double fitZoom = Math.min(scaleX, scaleY);

        // Set zoom to fit, but not smaller than 5%
        zoom = Math.max(0.05, Math.min(fitZoom, 6.0));
        zoomSlider.setValue(zoom * 100);
        updateZoomPercentLabel();

        System.out.println("Calculated zoom: " + zoom + " (fitZoom: " + fitZoom + ")");

        // Update the image view with the new zoom
        updateImageView();

        // Force centering after zoom calculation with additional delay
        Platform.runLater(() -> {
          Platform.runLater(() -> {
            centerImageInViewport();
          });
        });
      }
    });
  }

  private void setZoom(double z) {
    double oldZoom = zoom;
    zoom = Math.max(0.05, Math.min(z, 6.0));

    // Only update the image size, don't call updateImageView()
    Image img = imageView.getImage();
    if (img != null) {
      double imgW = img.getWidth() * zoom;
      double imgH = img.getHeight() * zoom;
      imageView.setFitWidth(imgW);
      imageView.setFitHeight(imgH);
    }

    // After zoom, adjust scroll position to maintain view
    if (oldZoom != zoom) {
      adjustTranslateToMaintainView(0, 0, oldZoom);
    }
    updateZoomPercentLabel();
  }

  private void adjustTranslateToMaintainView(double oldTranslateX, double oldTranslateY,
      double oldZoom) {
    Image img = imageView.getImage();
    if (img == null) {
      return;
    }

    // Get the viewport dimensions
    double viewportWidth = scrollPane.getViewportBounds().getWidth();
    double viewportHeight = scrollPane.getViewportBounds().getHeight();
    double imgWidth = img.getWidth() * zoom;
    double imgHeight = img.getHeight() * zoom;

    // Use hybrid approach: translate for small images, scroll for large images
    if (imgWidth <= viewportWidth && imgHeight <= viewportHeight) {
      // Image fits in viewport - center using translate
      imageView.setTranslateX((viewportWidth - imgWidth) / 2.0);
      imageView.setTranslateY((viewportHeight - imgHeight) / 2.0);
      // Reset scroll values
      scrollPane.setHvalue(0);
      scrollPane.setVvalue(0);
    } else {
      // Image is larger - maintain scroll position and reset translate
      imageView.setTranslateX(0);
      imageView.setTranslateY(0);

      // Keep current scroll position if it's valid
      double currentHValue = scrollPane.getHvalue();
      double currentVValue = scrollPane.getVvalue();

      if (imgWidth > viewportWidth) {
        scrollPane.setHvalue(Math.max(0, Math.min(1, currentHValue)));
      } else {
        scrollPane.setHvalue(0);
      }
      if (imgHeight > viewportHeight) {
        scrollPane.setVvalue(Math.max(0, Math.min(1, currentVValue)));
      } else {
        scrollPane.setVvalue(0);
      }
    }
  }

  private void centerImageInViewport() {
    // Prevent multiple simultaneous centering operations
    if (isCentering) {
      return;
    }
    isCentering = true;

    Platform.runLater(() -> {
      try {
        Image img = imageView.getImage();
        if (img == null) {
          return;
        }

        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double imgWidth = img.getWidth() * zoom;
        double imgHeight = img.getHeight() * zoom;

        // Ensure the container is large enough
        Pane container = (Pane) scrollPane.getContent();
        container.setPrefWidth(Math.max(viewportWidth, imgWidth));
        container.setPrefHeight(Math.max(viewportHeight, imgHeight));

        // Handle each dimension independently for proper centering
        if (imgWidth <= viewportWidth) {
          // Image fits horizontally - center using translate
          imageView.setTranslateX((viewportWidth - imgWidth) / 2.0);
          scrollPane.setHvalue(0);
        } else {
          // Image is wider than viewport - center using scroll
          imageView.setTranslateX(0);
          scrollPane.setHvalue(0.5);
        }

        if (imgHeight <= viewportHeight) {
          // Image fits vertically - center using translate
          imageView.setTranslateY((viewportHeight - imgHeight) / 2.0);
          scrollPane.setVvalue(0);
        } else {
          // Image is taller than viewport - center using scroll
          imageView.setTranslateY(0);
          scrollPane.setVvalue(0.5);
        }
      } finally {
        isCentering = false;
      }
    });
  }

  private void updateImageView() {
    Image img = imageContext.getImage();
    if (img != null) {
      imageView.setImage(img);
      // Reapply the current zoom to the new image
      setZoom(zoom);
      // Reset translate to 0 and center the image
      imageView.setTranslateX(0);
      imageView.setTranslateY(0);
      centerImageInViewport();
    } else {
      imageView.setImage(null);
    }
  }

  public void showOverlay() {
    overlayPane.setVisible(true);
    overlayPane.setMouseTransparent(false);
  }

  public void hideOverlay() {
    overlayPane.setVisible(false);
    overlayPane.setMouseTransparent(true);
  }

  public void showSelectionRect(double x, double y, double w, double h) {
    overlayHelper.showSelectionRect(x, y, w, h);
    showOverlay();
  }

  public void clearOverlay() {
    overlayHelper.clear();
    hideOverlay();
  }

  public void setHandMode(boolean hand) {
    this.handMode = hand;
    if (hand) {
      // Start with open hand cursor
      imageView.setCursor(Cursor.OPEN_HAND);
      // Make overlay transparent to mouse events when in hand mode
      overlayPane.setMouseTransparent(true);
      // Enable ScrollPane panning
      scrollPane.setPannable(true);

      // Add mouse event handlers for cursor changes
      imageView.setOnMousePressed(e -> {
        imageView.setCursor(Cursor.CLOSED_HAND);
      });

      imageView.setOnMouseReleased(e -> {
        imageView.setCursor(Cursor.OPEN_HAND);
      });

      imageView.setOnMouseExited(e -> {
        imageView.setCursor(Cursor.OPEN_HAND);
      });

    } else {
      imageView.setCursor(Cursor.DEFAULT);
      // Restore overlay mouse transparency based on selection state
      overlayPane.setMouseTransparent(!isSelecting);
      // Disable ScrollPane panning when not in hand mode
      scrollPane.setPannable(false);
      // Remove mouse event handlers and restore original clicked handler
      imageView.setOnMousePressed(null);
      imageView.setOnMouseReleased(null);
      imageView.setOnMouseExited(null);
      imageView.setOnMouseClicked(e -> imageView.requestFocus());
      // Don't auto-center when exiting hand mode - keep current position
    }
  }

  public double[] getSelectionBounds() {
    if (!isSelecting && selectionStartX != selectionEndX && selectionStartY != selectionEndY) {
      double x = Math.min(selectionStartX, selectionEndX);
      double y = Math.min(selectionStartY, selectionEndY);
      double w = Math.abs(selectionEndX - selectionStartX);
      double h = Math.abs(selectionEndY - selectionStartY);
      return new double[]{x, y, w, h};
    }
    return null;
  }

  /**
   * Builds the tools menu dynamically from registered operations
   */
  private Menu buildToolsMenu() {
    Menu toolsMenu = new Menu(I18n.get("menu.tools"));

    // Add hand tool (special case - not an operation)
    MenuItem handItem = new MenuItem(I18n.get("toolbox.hand"));
    handItem.setOnAction(e -> setHandMode(!handMode));
    toolsMenu.getItems().add(handItem);

    // Add separator if there are operations
    List<ImageOperation> operations = OperationRegistry.loadAllOperations();
    if (!operations.isEmpty()) {
      toolsMenu.getItems().add(new SeparatorMenuItem());

      // Add operation menu items
      for (ImageOperation op : operations) {
        MenuItem opItem = new MenuItem(op.getMetadata().getDisplayName());
        opItem.setOnAction(e -> handleOperation(op));
        toolsMenu.getItems().add(opItem);
      }
    }

    return toolsMenu;
  }

  private void handleClose() {
    if (!confirmDiscardUnsavedChanges()) {
      return;
    }
    imageContext.setImage(null);
    imageContext.setSelection(0, 0, 0, 0);
    imageContext.setImageFile(null);
    updateImageView();
    // Optionally clear operation history or overlays if needed
    operationHistory.clear();
    clearOverlay();
    updateWindowTitle();
    dirty = false;
    updateWindowTitle();
  }

  private void handleCopy() {
    Image img = imageContext.getImage();
    if (img == null) {
      return;
    }
    WritableImage toCopy;
    if (imageContext.hasSelection()) {
      // Copy selection area
      int x = (int) imageContext.getSelectionX();
      int y = (int) imageContext.getSelectionY();
      int w = (int) imageContext.getSelectionWidth();
      int h = (int) imageContext.getSelectionHeight();
      if (w > 0 && h > 0 && x + w <= img.getWidth() && y + h <= img.getHeight()) {
        // Snapshot the selection
        WritableImage selection = new WritableImage(img.getPixelReader(), x, y, w, h);
        toCopy = selection;
      } else {
        toCopy = img instanceof WritableImage ? (WritableImage) img
            : new WritableImage(img.getPixelReader(), (int) img.getWidth(), (int) img.getHeight());
      }
    } else {
      toCopy = img instanceof WritableImage ? (WritableImage) img
          : new WritableImage(img.getPixelReader(), (int) img.getWidth(), (int) img.getHeight());
    }
    ClipboardContent content = new ClipboardContent();
    content.putImage(toCopy);
    Clipboard.getSystemClipboard().setContent(content);
  }

  private void handlePaste() {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    if (clipboard.hasImage()) {
      Image img = clipboard.getImage();
      if (img != null) {
        imageContext.setImage(img);
        imageContext.setSelection(0, 0, 0, 0);
        imageContext.setImageFile(null); // Do not set to 'Untitled'
        updateImageView();
        clearOverlay();
        dirty = true;
        // Set window title to 'Untitled - Distorio' and mark as modified
        stage.setTitle("*" + I18n.get("app.untitled") + " - " + I18n.get("app.title"));
      }
    }
  }

  private void handleSave() {
    Image img = imageContext.getImage();
    if (img == null) {
      return;
    }
    File file = imageContext.getImageFile();
    if (file == null) {
      // No file path, fallback to Save As
      handleSaveAs();
      return;
    }
    try {
      FileService.saveImageToFile(img, file);
      dirty = false;
      updateWindowTitle();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private boolean handleSaveAs() {
    Image img = imageContext.getImage();
    if (img == null) {
      return false;
    }
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(I18n.get("dialog.save_image"));

    List<FileChooser.ExtensionFilter> filters = FileService.getWriteFileFilters();
    fileChooser.getExtensionFilters().addAll(filters);
    fileChooser.setSelectedExtensionFilter(filters.get(0)); // Default to PNG

    File file = fileChooser.showSaveDialog(null);
    if (file != null) {
      // Determine format and extension from selected filter
      FileChooser.ExtensionFilter selected = fileChooser.getSelectedExtensionFilter();
      String desc = selected.getDescription().toLowerCase();
      String ext;
      if (desc.contains("jpeg") || desc.contains("jpg")) {
        ext = "jpg";
      } else if (desc.contains("png")) {
        ext = "png";
      } else if (desc.contains("bmp")) {
        ext = "bmp";
      } else if (desc.contains("gif")) {
        ext = "gif";
      } else {
        // Fallback: use first extension in filter
        String pat = selected.getExtensions().get(0);
        ext = pat.substring(pat.lastIndexOf('.') + 1);
      }
      // Ensure file has correct extension
      String fileName = file.getName().toLowerCase();
      boolean hasExt = false;
      for (String pat : selected.getExtensions()) {
        String e = pat.replace("*.", "").toLowerCase();
        if (fileName.endsWith("." + e)) {
          hasExt = true;
          break;
        }
      }
      if (!hasExt) {
        file = new File(file.getParent(), file.getName() + "." + ext);
      }
      try {
        FileService.saveImageToFile(img, file);
        imageContext.setImageFile(file);
        updateWindowTitle();
        dirty = false;
        updateWindowTitle();
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    }
    return false; // User cancelled the save dialog
  }


  private void updateWindowTitle() {
    String baseTitle = I18n.get("app.title");
    File file = imageContext.getImageFile();
    String prefix = dirty ? "*" : "";
    if (file != null) {
      stage.setTitle(prefix + file.getName() + " - " + baseTitle);
    } else {
      stage.setTitle(baseTitle);
    }
  }

  private void updateZoomPercentLabel() {
    int percent = (int) Math.round(zoom * 100);
    zoomPercentLabel.setText(percent + "%");
  }

  // Helper method for drag-and-drop file type check
  private boolean isImageFile(File file) {
    return FileService.isImageFile(file);
  }

  public enum IconMode {ICON_ONLY, ICON_TEXT}
}
