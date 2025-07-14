module io.distorio.op.crop {
  requires io.distorio.operation.api;
  requires java.base;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.crop.CropOperationProvider;
}
