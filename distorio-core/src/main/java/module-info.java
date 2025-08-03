module io.distorio.core {
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    requires java.prefs;
    requires java.logging;
    requires io.distorio.operation.api;
    requires opencv; // Automatic module - name may be unstable

    exports io.distorio.core;
} 