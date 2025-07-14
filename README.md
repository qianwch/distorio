# Distorio - Lightweight JavaFX Image Editor

A modern, extensible image editor built with JavaFX and Java 17, featuring a plugin-based
architecture for easy operation extension.

## Features

### Core Functionality

- **Image Loading**: Open images via file dialog, clipboard, or drag-and-drop
- **Image Viewing**: High-quality image display with zoom (5%-600%) and panning
- **Multi-language Support**: Dynamic language switching (English/Chinese)
- **Undo/Redo**: Complete operation history with undo/redo support
- **Selection Tools**: Interactive selection with visual feedback

### User Interface

- **System-integrated Menu Bar**: Native menu integration
- **Toolbar**: Quick access to common operations
- **Left Toolbox**: Plugin-extensible operation panel
- **Status Bar**: Zoom slider and operation feedback
- **Flexible Button Modes**: Switch between icon-only and icon+text modes

### Built-in Operations

- **Transform**: Scale, rotate, and perspective transformations
- **Flip**: Horizontal and vertical image flipping
- **Crop**: Rectangular cropping with selection
- **Perspective Crop**: Advanced perspective correction

### Technical Features

- **Plugin Architecture**: ServiceLoader-based operation discovery
- **Modular Design**: Multi-module Maven project structure
- **Extensible**: Easy to add new operations via plugins
- **Modern Java**: Built with Java 17 and JavaFX

## Project Structure

```
distorio-parent/
├── distorio-app/                 # Main application
├── distorio-operation-api/       # Operation abstraction layer
├── distorio-ui-common/           # Shared UI components and resources
├── distorio-op-flip/             # Flip operation plugin
├── distorio-op-crop/             # Crop operation plugin
├── distorio-op-transform/        # Transform operation plugin
└── distorio-op-perspective-crop/ # Perspective crop plugin
```

## Building and Running

### Prerequisites

- Java 17 or later
- Maven 3.6+

### Build

```bash
mvn clean install
```

### Run

```bash
cd distorio-app
mvn exec:java -Dexec.mainClass="io.distorio.app.DistorioApp"
```

## Architecture

### Plugin System

The application uses Java's ServiceLoader mechanism for plugin discovery:

1. **Operation Interface**: `ImageOperation` defines the contract for all operations
2. **Provider Pattern**: `ImageOperationProvider` creates operation instances
3. **Registry**: `OperationRegistry` discovers and loads all available operations
4. **Dynamic Loading**: Operations are loaded at runtime without code changes

### Operation Lifecycle

Each operation follows a consistent lifecycle:

1. **Metadata**: Operation provides display name, icon, hotkey, etc.
2. **Preparation**: User interaction for operation setup (e.g., selection)
3. **Preview**: Real-time preview of operation effect
4. **Apply**: Execute the operation on the image
5. **Undo/Redo**: Support for operation history

### UI Architecture

- **MainWindow**: Primary application window with layout management
- **OverlayHelper**: Handles interactive overlays for operation preparation
- **AppImageContext**: Manages image state and selection data
- **OperationHistory**: Tracks undo/redo stack

## Creating Custom Operations

### 1. Create Plugin Module

```bash
mvn archetype:generate -DgroupId=io.distorio -DartifactId=distorio-op-myoperation \
    -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

### 2. Configure POM

```xml
<parent>
    <groupId>io.distorio</groupId>
    <artifactId>distorio-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
</parent>
<artifactId>distorio-op-myoperation</artifactId>
<dependencies>
    <dependency>
        <groupId>io.distorio</groupId>
        <artifactId>distorio-operation-api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 3. Implement Operation

```java
public class MyOperation implements ImageOperation {
    @Override
    public OperationMetadata getMetadata() {
        return new OperationMetadata() {
            @Override
            public String getId() { return "myoperation"; }
            @Override
            public String getDisplayName() { return "My Operation"; }
            @Override
            public Optional<String> getIconPath() { 
                return Optional.of("icons/myicon.png"); 
            }
        };
    }
    
    @Override
    public boolean prepare(OperationContext context) {
        // Setup operation
        return true;
    }
    
    @Override
    public void apply(OperationContext context) {
        // Execute operation
    }
    
    // Implement other methods...
}
```

### 4. Register Provider

Create `src/main/resources/META-INF/services/io.distorio.operation.api.ImageOperationProvider`:

```
io.distorio.op.myoperation.MyOperationProvider
```

### 5. Add to Parent POM

```xml
<modules>
    <module>distorio-op-myoperation</module>
</modules>
```

## Internationalization

The application supports multiple languages through resource bundles:

- **English**: `messages_en.properties`
- **Chinese**: `messages_zh.properties`

### Adding New Languages

1. Create `messages_xx.properties` in `distorio-ui-common/src/main/resources/i18n/`
2. Add language option to View menu
3. Update `I18n` utility class

## Development Roadmap

### Planned Features

- [ ] Real image processing operations (actual flipping, cropping, etc.)
- [ ] More selection tools (elliptical, freehand)
- [ ] Filters and effects
- [ ] Layer support
- [ ] Export to multiple formats
- [ ] Keyboard shortcuts
- [ ] Custom themes

### Technical Improvements

- [ ] Performance optimization for large images
- [ ] Memory management improvements
- [ ] Unit test coverage
- [ ] Documentation generation
- [ ] CI/CD pipeline

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- JavaFX team for the excellent UI framework
- Maven community for the build system
- All contributors and testers 
