module io.distorio.op.transform {
  requires io.distorio.operation.api;
  requires java.base;
  requires javafx.graphics;
  requires javafx.base;

  exports io.distorio.op.transform;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.transform.TransformOperationProvider;
}
