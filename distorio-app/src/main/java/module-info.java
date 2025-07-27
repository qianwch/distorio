module io.distorio.app {
  requires transitive javafx.controls;
  requires javafx.fxml;
  requires java.base;
  requires java.prefs;

  requires transitive io.distorio.operation.api;
  requires io.distorio.core;
  requires io.distorio.ui.common;
  requires java.desktop;
  requires javafx.swing;

  exports io.distorio.app;
}
