package wang.blaise.objectpicking;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Blaise on 2015/8/3.
 */
public class LoadedObjectVertexNormalAverage extends TouchableObject {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int muColorHandle;//顶点颜色

    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer vertexArrayBuffer;//一次顶点坐标数据缓冲
    ShortBuffer edgeIndexBuffer;//边线索引数据缓冲
    int vCount = 0;

    //黑色
    int faceBlack[] = {1, 0, 0};
    //蓝色
    int edgeBlue[] = {45, 183, 222};
    //存储转换后的颜色数组
    float cFaceBlack[] = new float[4];
    float cEdgeBlue[] = new float[4];

    public LoadedObjectVertexNormalAverage(MySurfaceView mv, float[] vertices,
                                           float[] vertexArray, short[] edgeIndices) {
        //初始化顶点坐标与着色数据
        initVertexData(vertices);
        //初始化shader
        initShader(mv);
        //初始化边线索引
        initEdgeIndices(edgeIndices);
        //初始化边线
        initEdge(vertexArray);
        //初始化包围盒
        preBox = new AABB3(vertices);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices) {
        //顶点坐标数据的初始化================begin============================
        vCount = vertices.length / 3;

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
    }

    //初始化shader
    public void initShader(MySurfaceView mv) {
        //转换颜色
        for (int i = 0; i < 3; i++) {
            cFaceBlack[i] = (float) faceBlack[i] / 255;
            cEdgeBlue[i] = (float) edgeBlue[i] / 255;
        }
        cFaceBlack[3] = cEdgeBlue[3] = 1.0f;

        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        muColorHandle = GLES20.glGetUniformLocation(mProgram, "aColor");
    }

    public void initEdgeIndices(short[] lineIndices) {
        ByteBuffer lib = ByteBuffer.allocateDirect(lineIndices.length * 2);
        // (对应顺序的坐标数 * 2)short是2字节
        lib.order(ByteOrder.nativeOrder());
        edgeIndexBuffer = lib.asShortBuffer();
        // 为绘制列表初始化字节缓冲
        edgeIndexBuffer.put(lineIndices);
        edgeIndexBuffer.position(0);
    }

    public void initEdge(float[] vertexArray) {
        ByteBuffer ebb = ByteBuffer.allocateDirect(vertexArray.length * 4);
        ebb.order(ByteOrder.nativeOrder());//设置字节顺序
        vertexArrayBuffer = ebb.asFloatBuffer();//转换为Float型缓冲
        vertexArrayBuffer.put(vertexArray);//向缓冲区中放入顶点坐标数据
        vertexArrayBuffer.position(0);//设置缓冲区起始位置
    }

    public void drawSelf(int edgeCount) {
        copyM();//复制变换矩阵
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                MatrixState.getFinalMatrix(), 0);
        //传入颜色数据
        GLES20.glUniform4fv(muColorHandle, 1, cFaceBlack, 0);
        //将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        //启用顶点位置
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        //启用深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // 设置线宽
        GLES20.glLineWidth(6f);
        //绘制加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        //关闭offset fill
        GLES20.glDisable(GLES20.GL_POLYGON_OFFSET_FILL);

        GLES20.glUniform4fv(muColorHandle, 1, cEdgeBlue, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, vertexArrayBuffer);

        GLES20.glDrawElements(GLES20.GL_LINES, edgeCount,
                GLES20.GL_UNSIGNED_SHORT, edgeIndexBuffer);

        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);
        GLES20.glPolygonOffset(1.0f, 10.0f);

        GLES20.glDisableVertexAttribArray(maPositionHandle);
    }
}
