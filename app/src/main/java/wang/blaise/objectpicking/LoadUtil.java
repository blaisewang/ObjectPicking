package wang.blaise.objectpicking;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

class LoadUtil {
    private static int count;

    static LoadedObjectVertexNormalAverage loadFromFileVertexOnlyAverage(Resources r, MySurfaceView mv) {
        LoadedObjectVertexNormalAverage loadedObjectVertexNormalAverage = null;
        ArrayList<Float> alv = new ArrayList<>();
        ArrayList<Float> alvResult = new ArrayList<>();
        ArrayList<Short> indices = new ArrayList<>();

        try {
            InputStream in = r.getAssets().open("building.obj");
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps;

            while ((temps = br.readLine()) != null) {
                String[] tempString = temps.split("[ ]+");
                if (tempString[0].trim().equals("v")) {
                    alv.add(Float.parseFloat(tempString[1]));
                    alv.add(Float.parseFloat(tempString[2]));
                    alv.add(Float.parseFloat(tempString[3]));
                } else if (tempString[0].trim().equals("l")) {
                    indices.add(Short.parseShort(tempString[1]));
                    indices.add(Short.parseShort(tempString[2]));
                } else if (tempString[0].trim().equals("f")) {
                    int[] index = new int[3];
                    index[0] = Integer.parseInt(tempString[1].split("/")[0]) - 1;
                    float x0 = alv.get(3 * index[0]);
                    float y0 = alv.get(3 * index[0] + 1);
                    float z0 = alv.get(3 * index[0] + 2);
                    alvResult.add(x0);
                    alvResult.add(y0);
                    alvResult.add(z0);
                    index[1] = Integer.parseInt(tempString[2].split("/")[0]) - 1;
                    float x1 = alv.get(3 * index[1]);
                    float y1 = alv.get(3 * index[1] + 1);
                    float z1 = alv.get(3 * index[1] + 2);
                    alvResult.add(x1);
                    alvResult.add(y1);
                    alvResult.add(z1);
                    index[2] = Integer.parseInt(tempString[3].split("/")[0]) - 1;
                    float x2 = alv.get(3 * index[2]);
                    float y2 = alv.get(3 * index[2] + 1);
                    float z2 = alv.get(3 * index[2] + 2);
                    alvResult.add(x2);
                    alvResult.add(y2);
                    alvResult.add(z2);
                }
            }

            int size = alvResult.size();
            float[] vertexArray = new float[alv.size()];
            short[] indicesArray = new short[indices.size()];
            float[] vXYZ = new float[size];
            count = indices.size();

            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }

            for (int i = 0; i < alv.size(); i++) {
                vertexArray[i] = alv.get(i);
            }

            for (int i = 0; i < indices.size(); i++) {
                indicesArray[i] = (short) (indices.get(i) - 1);
            }

            loadedObjectVertexNormalAverage = new LoadedObjectVertexNormalAverage(mv, vXYZ, vertexArray, indicesArray);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return loadedObjectVertexNormalAverage;
    }

    static int edgeCount() {
        return count;
    }
}
