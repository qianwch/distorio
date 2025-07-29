module io.distorio.op.crop {
  requires io.distorio.operation.api;
  requires java.base;
  requires javafx.graphics;
  requires javafx.base;

  exports io.distorio.op.crop;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.crop.CropOperationProvider;
}
