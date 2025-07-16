module io.distorio.op.flip {
  requires io.distorio.operation.api;
  requires java.base;
  requires javafx.graphics;
  requires javafx.base;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.flip.FlipLeftOperationProvider,
    io.distorio.op.flip.FlipRightOperationProvider;
}
