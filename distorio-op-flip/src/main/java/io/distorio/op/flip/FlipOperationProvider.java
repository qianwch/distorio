package io.distorio.op.flip;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.ImageOperationProvider;

public class FlipOperationProvider implements ImageOperationProvider {

  @Override
  public ImageOperation create() {
    return new FlipOperation();
  }
}
