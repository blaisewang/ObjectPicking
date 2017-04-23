package wang.blaise.objectpicking;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

class IntersectingUtil {
    static float[] calculateABPosition
            (
                    float x,
                    float y,
                    float w,
                    float h,
                    float left,
                    float top,
                    float near,
                    float far
            ) {
        float x0 = x - w / 2;
        float y0 = h / 2 - y;
        float xNear = 2 * x0 * left / w;
        float yNear = 2 * y0 * top / h;
        float ratio = far / near;
        float xFar = ratio * xNear;
        float yFar = ratio * yNear;

        float az = -near;
        float bz = -far;
        float[] A = MatrixState.fromPointToPreviousPoint(new float[]{xNear, yNear, az});
        float[] B = MatrixState.fromPointToPreviousPoint(new float[]{xFar, yFar, bz});

        return new float[]{A[0], A[1], A[2], B[0], B[1], B[2]};
    }
}
