package io.distorio.op.perspective_crop;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.ImageOperationProvider;

public class PerspectiveCropOperationProvider implements ImageOperationProvider {

  @Override
  public ImageOperation create() {
    return new PerspectiveCropOperation();
  }
}
