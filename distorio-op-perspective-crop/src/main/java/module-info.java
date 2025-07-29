module io.distorio.op.perspective_crop {
  requires io.distorio.operation.api;
  requires java.base;
  requires javafx.graphics;
  requires javafx.base;

  exports io.distorio.op.perspective_crop;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.perspective_crop.PerspectiveCropOperationProvider;
}
