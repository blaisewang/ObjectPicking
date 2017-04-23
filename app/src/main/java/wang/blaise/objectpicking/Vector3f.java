package wang.blaise.objectpicking;

/**
 * ObjectPicking
 * Created by Blaise on 2015/8/3.
 */

class Vector3f {
    float x;
    float y;
    float z;

    Vector3f() {
    }

    Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vector3f minus(Vector3f vector) {
        return new Vector3f(this.x - vector.x, this.y - vector.y, this.z - vector.z);
    }
}
