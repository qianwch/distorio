package io.distorio.op.transform;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.ImageOperationProvider;

public class TransformOperationProvider implements ImageOperationProvider {

  @Override
  public ImageOperation create() {
    return new TransformOperation();
  }
}
