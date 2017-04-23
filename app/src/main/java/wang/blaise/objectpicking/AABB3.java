package wang.blaise.objectpicking;

import android.opengl.Matrix;

class AABB3 {
    private Vector3f min;
    private Vector3f max;

    AABB3(float[] vertices) {
        min = new Vector3f();
        max = new Vector3f();
        empty();
        for (int i = 0; i < vertices.length; i += 3) {
            this.add(vertices[i], vertices[i + 1], vertices[i + 2]);
        }
    }

    private void empty() {
        min.x = min.y = min.z = Float.POSITIVE_INFINITY;
        max.x = max.y = max.z = Float.NEGATIVE_INFINITY;
    }

    private void add(float x, float y, float z) {
        if (x < min.x) {
            min.x = x;
        }
        if (x > max.x) {
            max.x = x;
        }
        if (y < min.y) {
            min.y = y;
        }
        if (y > max.y) {
            max.y = y;
        }
        if (z < min.z) {
            min.z = z;
        }
        if (z > max.z) {
            max.z = z;
        }
    }

    private Vector3f[] getAllCorners() {
        Vector3f[] result = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            result[i] = getCorner(i);
        }
        return result;
    }

    private Vector3f getCorner(int i) {
        if (i < 0 || i > 7) {
            return null;
        }
        return new Vector3f(
                ((i & 1) == 0) ? max.x : min.x,
                ((i & 2) == 0) ? max.y : min.y,
                ((i & 4) == 0) ? max.z : min.z
        );
    }

    AABB3 setToTransformedBox(float[] m) {
        Vector3f[] allCorners = this.getAllCorners();
        float[] transformedCorners = new float[24];
        float[] tempResult = new float[4];
        int count = 0;
        for (Vector3f aVa : allCorners) {
            float[] point = new float[]{aVa.x, aVa.y, aVa.z, 1};
            Matrix.multiplyMV(tempResult, 0, m, 0, point, 0);
            transformedCorners[count++] = tempResult[0];
            transformedCorners[count++] = tempResult[1];
            transformedCorners[count++] = tempResult[2];
        }

        return new AABB3(transformedCorners);
    }

    float rayIntersect(
            Vector3f rayStart,
            Vector3f rayDir
    ) {
        final float kNoIntersection = Float.POSITIVE_INFINITY;
        boolean inside = true;
        float xt;
        if (rayStart.x < min.x) {
            xt = min.x - rayStart.x;
            if (xt > rayDir.x) {
                return kNoIntersection;
            }
            xt /= rayDir.x;
            inside = false;
        } else if (rayStart.x > max.x) {
            xt = max.x - rayStart.x;
            if (xt < rayDir.x) {
                return kNoIntersection;
            }
            xt /= rayDir.x;
            inside = false;
        } else {
            xt = -1.0f;
        }

        float yt;
        if (rayStart.y < min.y) {
            yt = min.y - rayStart.y;
            if (yt > rayDir.y) {
                return kNoIntersection;
            }
            yt /= rayDir.y;
            inside = false;
        } else if (rayStart.y > max.y) {
            yt = max.y - rayStart.y;
            if (yt < rayDir.y) {
                return kNoIntersection;
            }
            yt /= rayDir.y;
            inside = false;
        } else {
            yt = -1.0f;
        }

        float zt;
        if (rayStart.z < min.z) {
            zt = min.z - rayStart.z;
            if (zt > rayDir.z) {
                return kNoIntersection;
            }
            zt /= rayDir.z;
            inside = false;
        } else if (rayStart.z > max.z) {
            zt = max.z - rayStart.z;
            if (zt < rayDir.z) {
                return kNoIntersection;
            }
            zt /= rayDir.z;
            inside = false;
        } else {
            zt = -1.0f;
        }

        if (inside) {
            return 0.0f;
        }

        int which = 0;
        float t = xt;
        if (yt > t) {
            which = 1;
            t = yt;
        }
        if (zt > t) {
            which = 2;
            t = zt;
        }
        switch (which) {
            case 0: {
                float y = rayStart.y + rayDir.y * t;
                if (y < min.y || y > max.y) {
                    return kNoIntersection;
                }
                float z = rayStart.z + rayDir.z * t;
                if (z < min.z || z > max.z) {
                    return kNoIntersection;
                }
            }
            break;
            case 1: {
                float x = rayStart.x + rayDir.x * t;
                if (x < min.x || x > max.x) {
                    return kNoIntersection;
                }
                float z = rayStart.z + rayDir.z * t;
                if (z < min.z || z > max.z) {
                    return kNoIntersection;
                }
            }
            break;
            case 2: {
                float x = rayStart.x + rayDir.x * t;
                if (x < min.x || x > max.x) {
                    return kNoIntersection;
                }
                float y = rayStart.y + rayDir.y * t;
                if (y < min.y || y > max.y) {
                    return kNoIntersection;
                }
            }
            break;
        }
        return t;
    }
}

