package io.distorio.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class DistorioApp extends Application {

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Distorio Image Editor");
    MainWindow mainWindow = new MainWindow(primaryStage);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
