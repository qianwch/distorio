package io.distorio.op.flip;

import io.distorio.operation.api.ImageOperation;
import io.distorio.operation.api.ImageOperationProvider;

public class FlipRightOperationProvider implements ImageOperationProvider {
    @Override
    public ImageOperation create() {
        return new FlipOperation(FlipOperation.Direction.RIGHT);
    }
} 