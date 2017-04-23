package wang.blaise.objectpicking;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Blaise on 2015/8/3.
 */
public class LoadUtil {
    public static int count;

    //从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
    public static LoadedObjectVertexNormalAverage loadFromFileVertexOnlyAverage(String fname, Resources r, MySurfaceView mv) {
        //加载后物体的引用
        LoadedObjectVertexNormalAverage lo = null;
        //原始顶点坐标列表--直接从obj文件中加载
        ArrayList<Float> alv = new ArrayList<Float>();
        //结果顶点坐标列表--按面组织好
        ArrayList<Float> alvResult = new ArrayList<Float>();
        //边线索引坐标列表
        ArrayList<Short> indice = new ArrayList<Short>();
        //平均前各个索引对应的点的法向量集合Map

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //扫面文件，根据行类型的不同执行不同的处理逻辑
            while ((temps = br.readLine()) != null) {
                //用空格分割行中的各个组成部分
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {//此行为顶点坐标
                    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
                    alv.add(Float.parseFloat(tempsa[1]));
                    alv.add(Float.parseFloat(tempsa[2]));
                    alv.add(Float.parseFloat(tempsa[3]));
                } else if (tempsa[0].trim().equals("l")) {//此行为边线索引
                    indice.add(Short.parseShort(tempsa[1]));
                    indice.add(Short.parseShort(tempsa[2]));
                } else if (tempsa[0].trim().equals("f")) {//此行为三角形面
                    int[] index = new int[3];//三个顶点索引值的数组
                    //计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[0] = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    float x0 = alv.get(3 * index[0]);
                    float y0 = alv.get(3 * index[0] + 1);
                    float z0 = alv.get(3 * index[0] + 2);
                    alvResult.add(x0);
                    alvResult.add(y0);
                    alvResult.add(z0);

                    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[1] = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    float x1 = alv.get(3 * index[1]);
                    float y1 = alv.get(3 * index[1] + 1);
                    float z1 = alv.get(3 * index[1] + 2);
                    alvResult.add(x1);
                    alvResult.add(y1);
                    alvResult.add(z1);

                    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[2] = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
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
            short[] indicesArray = new short[indice.size()];
            float[] vXYZ = new float[size];
            count = indice.size();

            //生成顶点数组
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }

            //生成一次顶点数组
            for (int i = 0; i < alv.size(); i++) {
                vertexArray[i] = alv.get(i);
            }

            //生成边线索数组
            for (int i = 0; i < indice.size(); i++) {
                indicesArray[i] = (short) (indice.get(i) - 1);
            }

            //创建3D物体对象
            lo = new LoadedObjectVertexNormalAverage(mv, vXYZ, vertexArray, indicesArray);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }
        return lo;
    }

    public static int edgeCount() {
        return count;
    }
}
