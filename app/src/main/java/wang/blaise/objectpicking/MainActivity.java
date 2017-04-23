package wang.blaise.objectpicking;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private MySurfaceView myGLSurfaceView;
    static float screenWidth;
    static float screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        myGLSurfaceView = new MySurfaceView(this);
        setContentView(myGLSurfaceView);
        myGLSurfaceView.requestFocus();
        myGLSurfaceView.setFocusableInTouchMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
    }
}
