package wang.blaise.objectpicking;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

public class MySurfaceView extends GLSurfaceView {
    private float mPreviousY;
    private float mPreviousX;

    private float cameraX = 0f;
    private float cameraY = 0f;
    private float cameraZ = 60f;

    private float targetX = 0f;
    private float targetY = 0f;
    private float targetZ = 0f;
    private float angleDegreeElevation = 30f;
    private float angleDegreeAzimuth = 180f;
    private float left;
    private float top;
    private float near;
    private float far;

    private ArrayList<TouchableObject> touchableObjectList = new ArrayList<>();

    public MySurfaceView(Context context) {
        super(context);

        setEGLConfigChooser(new EGLConfigChooser() {
            @Override
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int[] attrList = new int[]{
                        EGL10.EGL_RENDERABLE_TYPE, 4,
                        EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
                        EGL10.EGL_BUFFER_SIZE, 16,
                        EGL10.EGL_RED_SIZE, 8,
                        EGL10.EGL_GREEN_SIZE, 8,
                        EGL10.EGL_BLUE_SIZE, 8,
                        EGL10.EGL_DEPTH_SIZE, 16,
                        EGL10.EGL_SAMPLE_BUFFERS, 1,
                        EGL10.EGL_SAMPLES, 4,
                        EGL10.EGL_NONE
                };
                EGLConfig[] configOut = new EGLConfig[1];
                int[] configNumOut = new int[1];
                egl.eglChooseConfig(display, attrList, configOut, 1, configNumOut);
                return configOut[0];
            }
        });

        this.setEGLContextClientVersion(2);
        SceneRenderer mSceneRenderer = new SceneRenderer();
        setRenderer(mSceneRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float[] AB = IntersectingUtil.calculateABPosition
                        (
                                x,
                                y,
                                MainActivity.screenWidth,
                                MainActivity.screenHeight,
                                left,
                                top,
                                near,
                                far
                        );
                Vector3f start = new Vector3f(AB[0], AB[1], AB[2]);
                Vector3f end = new Vector3f(AB[3], AB[4], AB[5]);
                Vector3f dir = end.minus(start);

                int checkedIndex;
                int tempIndex = -1;
                float minTime = 1;
                for (int i = 0; i < touchableObjectList.size(); i++) {
                    AABB3 box = touchableObjectList.get(i).getCurrBox();
                    float t = box.rayIntersect(start, dir);
                    if (t <= minTime) {
                        minTime = t;
                        tempIndex = i;
                    }
                }
                checkedIndex = tempIndex;
                changeObj(checkedIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;
                float dx = x - mPreviousX;

                if (Math.abs(dx) < 7f && Math.abs(dy) < 7f) {
                    break;
                }
                float TOUCH_SCALE_FACTOR = 180.0f / 320;
                angleDegreeAzimuth += dx * TOUCH_SCALE_FACTOR;
                angleDegreeElevation += dy * TOUCH_SCALE_FACTOR;

                angleDegreeElevation = Math.max(angleDegreeElevation, 5);
                angleDegreeElevation = Math.min(angleDegreeElevation, 90);

                setCameraPosition();
                break;
        }
        mPreviousY = y;
        mPreviousX = x;

        return true;
    }

    private void setCameraPosition() {
        double angleRadiusElevation = Math.toRadians(angleDegreeElevation);
        double angleRadiusAzimuth = Math.toRadians(angleDegreeAzimuth);
        float currentSightDistance = 100f;
        cameraX = (float) (targetX - currentSightDistance * Math.cos(angleRadiusElevation) * Math.sin(angleRadiusAzimuth));
        cameraY = (float) (targetY + currentSightDistance * Math.sin(angleRadiusElevation));
        cameraZ = (float) (targetZ - currentSightDistance * Math.cos(angleRadiusElevation) * Math.cos(angleRadiusAzimuth));
    }

    private void changeObj(int index) {
        if (index != -1) {
            for (int i = 0; i < touchableObjectList.size(); i++) {
                if (i == index) {
                    touchableObjectList.get(i).changeOnTouch(true);
                } else {
                    touchableObjectList.get(i).changeOnTouch(false);
                }
            }
        } else {
            for (TouchableObject aTouchableObjectList : touchableObjectList) {
                aTouchableObjectList.changeOnTouch(false);
            }
        }
    }

    private class SceneRenderer implements Renderer {
        int edgeCount;
        LoadedObjectVertexNormalAverage obj;

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            MatrixState.setInitStack();

            obj = LoadUtil.loadFromFileVertexOnlyAverage(
                    MySurfaceView.this.getResources(), MySurfaceView.this);
            edgeCount = LoadUtil.edgeCount();
            touchableObjectList.add(obj);
        }

        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera(cameraX, cameraY, cameraZ, targetX, targetY, targetZ);

            MatrixState.pushMatrix();
            MatrixState.translate();
            MatrixState.scale(obj.size, obj.size, obj.size);
            MatrixState.rotate();
            if (obj != null) {
                obj.drawSelf(edgeCount);
            }
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            float right;
            left = right = ratio;
            float bottom;
            top = bottom = 1;
            near = 2;
            far = 500;
            MatrixState.setProjectFrustum(-left, right, -bottom, top, near, far);
            setCameraPosition();
        }
    }
}
