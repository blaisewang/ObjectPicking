package wang.blaise.objectpicking;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

class LoadedObjectVertexNormalAverage extends TouchableObject {
    private int mProgram;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private int muColorHandle;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer vertexArrayBuffer;
    private ShortBuffer edgeIndexBuffer;
    private int vCount = 0;

    private int faceBlack[] = {1, 0, 0};
    private int edgeBlue[] = {45, 183, 222};
    private float cFaceBlack[] = new float[4];
    private float cEdgeBlue[] = new float[4];

    LoadedObjectVertexNormalAverage
            (MySurfaceView mySurfaceView,
             float[] vertices,
             float[] vertexArray,
             short[] edgeIndices) {
        initVertexData(vertices);
        initShader(mySurfaceView);
        initEdgeIndices(edgeIndices);
        initEdge(vertexArray);
        preBox = new AABB3(vertices);
    }

    private void initVertexData(float[] vertices) {
        vCount = vertices.length / 3;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void initShader(MySurfaceView mySurfaceView) {
        for (int i = 0; i < 3; i++) {
            cFaceBlack[i] = (float) faceBlack[i] / 255;
            cEdgeBlue[i] = (float) edgeBlue[i] / 255;
        }
        cFaceBlack[3] = cEdgeBlue[3] = 1.0f;

        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mySurfaceView.getResources());
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mySurfaceView.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        muColorHandle = GLES20.glGetUniformLocation(mProgram, "aColor");
    }

    private void initEdgeIndices(short[] lineIndices) {
        ByteBuffer lib = ByteBuffer.allocateDirect(lineIndices.length * 2);
        lib.order(ByteOrder.nativeOrder());
        edgeIndexBuffer = lib.asShortBuffer();
        edgeIndexBuffer.put(lineIndices);
        edgeIndexBuffer.position(0);
    }

    private void initEdge(float[] vertexArray) {
        ByteBuffer ebb = ByteBuffer.allocateDirect(vertexArray.length * 4);
        ebb.order(ByteOrder.nativeOrder());
        vertexArrayBuffer = ebb.asFloatBuffer();
        vertexArrayBuffer.put(vertexArray);
        vertexArrayBuffer.position(0);
    }

    void drawSelf(int edgeCount) {
        copyM();
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                MatrixState.getFinalMatrix(), 0);
        GLES20.glUniform4fv(muColorHandle, 1, cFaceBlack, 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glLineWidth(6f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        GLES20.glDisable(GLES20.GL_POLYGON_OFFSET_FILL);
        GLES20.glUniform4fv(muColorHandle, 1, cEdgeBlue, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, vertexArrayBuffer);
        GLES20.glDrawElements(GLES20.GL_LINES, edgeCount,
                GLES20.GL_UNSIGNED_SHORT, edgeIndexBuffer);

        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);
        GLES20.glPolygonOffset(1.0f, 10.0f);
        GLES20.glDisableVertexAttribArray(maPositionHandle);
    }
}
