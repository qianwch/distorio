package io.distorio.op.flip;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.ImageOperationProvider;

public class FlipLeftOperationProvider implements ImageOperationProvider {
    @Override
    public ImageOperation create() {
        return new FlipOperation(FlipOperation.Direction.LEFT);
    }
} 