package wang.blaise.objectpicking;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

class MatrixState {
    private static float[] mProjectionMatrix = new float[16];
    private static float[] mVMatrix = new float[16];
    private static float[] currentMatrix;

    private static float[][] mStack = new float[10][16];
    private static int stackTop = -1;

    static void setInitStack() {
        currentMatrix = new float[16];
        Matrix.setRotateM(currentMatrix, 0, 0, 1, 0, 0);
    }

    static void pushMatrix() {
        stackTop++;
        System.arraycopy(currentMatrix, 0, mStack[stackTop], 0, 16);
    }

    static void popMatrix() {
        System.arraycopy(mStack[stackTop], 0, currentMatrix, 0, 16);
        stackTop--;
    }

    static void translate() {
        Matrix.translateM(currentMatrix, 0, 0f, 0f, 10f);
    }

    static void rotate() {
        Matrix.rotateM(currentMatrix, 0, (float) 180, 0f, 1.0f, 0f);
    }

    static void scale(float x, float y, float z) {
        Matrix.scaleM(currentMatrix, 0, x, y, z);
    }


    static void setCamera(
            float cameraX,
            float cameraY,
            float cameraZ,
            float targetX,
            float targetY,
            float targetZ
    ) {
        Matrix.setLookAtM(mVMatrix, 0, cameraX, cameraY, cameraZ, targetX, targetY, targetZ, 0f, 1f, 0f);

        float[] cameraLocation = new float[3];
        cameraLocation[0] = cameraX;
        cameraLocation[1] = cameraY;
        cameraLocation[2] = cameraZ;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(3 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer cameraFB = byteBuffer.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    private static float[] getInvertMvMatrix() {
        float[] invertMatrix = new float[16];
        Matrix.invertM(invertMatrix, 0, mVMatrix, 0);
        return invertMatrix;
    }

    static float[] fromPointToPreviousPoint(float[] p) {
        float[] invertM = getInvertMvMatrix();
        float[] previousPoint = new float[4];
        Matrix.multiplyMV(previousPoint, 0, invertM, 0, new float[]{p[0], p[1], p[2], 1}, 0);
        return new float[]{previousPoint[0], previousPoint[1], previousPoint[2]};
    }

    static void setProjectFrustum
            (
                    float left,
                    float right,
                    float bottom,
                    float top,
                    float near,
                    float far
            ) {
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    static float[] getFinalMatrix() {
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currentMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    static float[] getMMatrix() {
        return currentMatrix;
    }
}
