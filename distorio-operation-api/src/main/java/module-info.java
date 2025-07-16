module io.distorio.operation.api {
  requires java.base;
  requires transitive javafx.graphics;
  exports io.distorio.operation.api;
  uses io.distorio.operation.api.ImageOperationProvider;
}
