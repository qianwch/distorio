module io.distorio.op.flip {
  requires io.distorio.operation.api;
  requires java.base;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.flip.FlipOperationProvider;
}
