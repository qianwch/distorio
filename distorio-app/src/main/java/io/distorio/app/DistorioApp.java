package io.distorio.app;

import io.distorio.core.OpenCVUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class DistorioApp extends Application {

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Distorio Image Editor");
    new MainWindow(primaryStage);
    primaryStage.show();
  }

  public static void main(String[] args) {
    // Preload OpenCV to avoid first-time loading delays
    System.out.println("Preloading OpenCV library...");
    OpenCVUtils.preloadOpenCV();
    System.out.println("OpenCV preloading completed.");
    
    launch(args);
  }
}
