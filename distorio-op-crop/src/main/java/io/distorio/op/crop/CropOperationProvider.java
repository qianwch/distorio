package io.distorio.op.crop;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.ImageOperationProvider;

public class CropOperationProvider implements ImageOperationProvider {

  @Override
  public ImageOperation create() {
    return new CropOperation();
  }
}
