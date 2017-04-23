package wang.blaise.objectpicking;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

abstract class TouchableObject {
    AABB3 preBox;
    private float[] matrix = new float[16];

    float size = 1.5f;

    AABB3 getCurrBox() {
        return preBox.setToTransformedBox(matrix);
    }

    void changeOnTouch(boolean flag) {
        if (flag) {
            size = 3f;
        } else {
            size = 1.5f;
        }
    }

    void copyM() {
        System.arraycopy(MatrixState.getMMatrix(), 0, matrix, 0, 16);
    }
}
