# Distorio Demo Guide

This guide demonstrates the key features of the Distorio image editor.

## Getting Started

### Launch the Application

```bash
cd distorio-app
mvn exec:java -Dexec.mainClass="io.distorio.app.DistorioApp"
```

## Feature Demonstrations

### 1. Image Loading

- **File Dialog**: Click "Open" in the toolbar or use File → Open
- **Drag & Drop**: Drag an image file onto the application window
- **Clipboard**: Copy an image and use Edit → Paste

### 2. Image Navigation

- **Zoom**: Use the zoom slider in the status bar (5% - 600%)
- **Mouse Wheel**: Scroll to zoom in/out
- **Panning**:
  - Use arrow keys to scroll
  - Switch to "Hand" tool in the toolbox and drag to pan
  - Use the scroll bars

### 3. Selection Tools

- **Arrow Tool**: Default selection mode
- **Selection Rectangle**: Click and drag to create a selection
- **Visual Feedback**: Selection is highlighted with a dashed border

### 4. Image Operations

#### Crop Operation

1. Select the "Crop" tool from the toolbox
2. Draw a selection rectangle around the area to crop
3. Click "Apply" or press Enter
4. The selection bounds will be printed to the console

#### Flip Operations

1. **Horizontal Flip**: Click "Flip Horizontal" in the toolbox
2. **Vertical Flip**: Click "Flip Vertical" in the toolbox
3. The operation will be applied and logged to the console

#### Transform Operation

1. Select the "Transform" tool from the toolbox
2. Draw a selection rectangle
3. Click "Apply" to see the selection bounds

#### Perspective Crop

1. Select "Perspective Crop" from the toolbox
2. Draw a selection rectangle
3. Click "Apply" to see the selection bounds

### 5. Undo/Redo

- **Undo**: Click the "Undo" button in the toolbar or use Edit → Undo
- **Redo**: Click the "Redo" button in the toolbar or use Edit → Redo
- **History**: All operations are tracked in the operation history

### 6. User Interface Features

#### Menu Bar

- **File**: Open, Save, Save As, Close
- **Edit**: Copy, Paste, Undo, Redo
- **Tools**: Access to all available operations
- **View**:
  - Toggle between icon-only and icon+text button modes
  - Language switching (English/Chinese)
- **Help**: About dialog

#### Toolbar

- Quick access to common operations
- Dynamic button text based on current language
- Icon-only mode support

#### Toolbox

- Plugin-loaded operations appear automatically
- Special flip operations (horizontal/vertical)
- Tool selection with visual feedback

#### Status Bar

- Zoom slider with percentage display
- Operation feedback and status messages

### 7. Internationalization

- **Language Switching**: View → Language → English/Chinese
- **Dynamic UI**: All text updates immediately
- **Resource Bundles**: Easy to add new languages

### 8. Plugin System Demo

#### Viewing Loaded Plugins

The application automatically discovers and loads all available operation plugins:

- Flip operations (horizontal/vertical)
- Crop operation
- Transform operation
- Perspective crop operation

#### Console Output

When operations are applied, you'll see output like:

```
Crop selection: x=100.0, y=50.0, w=200.0, h=150.0
Flip operation applied: Horizontal
Transform selection: x=75.0, y=25.0, w=300.0, h=200.0
Perspective crop selection: x=50.0, y=30.0, w=250.0, h=180.0
```

## Advanced Features

### Custom Plugin Development

1. Follow the plugin creation guide in README.md
2. Add your plugin module to the parent POM
3. Rebuild the project
4. Your plugin will appear in the toolbox automatically

### Keyboard Shortcuts

- **Ctrl+O**: Open file
- **Ctrl+S**: Save
- **Ctrl+Z**: Undo
- **Ctrl+Y**: Redo
- **Ctrl+C**: Copy
- **Ctrl+V**: Paste

### Performance Notes

- Large images may take time to load and display
- Zoom operations are optimized for smooth interaction
- Selection operations provide real-time visual feedback

## Troubleshooting

### Common Issues

1. **Application won't start**: Check Java version (requires Java 17+)
2. **Plugins not loading**: Ensure all modules are built with `mvn clean install`
3. **Missing icons**: Verify icon files are in the correct location
4. **Language not switching**: Check resource bundle files

### Debug Information

- Check console output for operation logs
- Verify plugin discovery in the toolbox
- Monitor memory usage with large images

## Next Steps

The current implementation provides a solid foundation for a full-featured image editor. Future
development can focus on:

1. **Real Image Processing**: Implement actual image manipulation operations
2. **More Tools**: Add drawing, text, and filter operations
3. **File Format Support**: Add support for more image formats
4. **Performance Optimization**: Improve handling of large images
5. **Advanced UI**: Add more sophisticated selection tools and previews

This demo showcases the extensible architecture and modern JavaFX-based UI that makes Distorio a
powerful platform for image editing applications. 
