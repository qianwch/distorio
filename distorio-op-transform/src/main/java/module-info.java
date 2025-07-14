module io.distorio.op.transform {
  requires io.distorio.operation.api;
  requires java.base;

  provides io.distorio.operation.api.ImageOperationProvider
    with io.distorio.op.transform.TransformOperationProvider;
}
