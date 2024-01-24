package zero.test;
// Block piston reactions
public class ZeroUtil {
    public static int lan_port = 0;
    // Range: (-180.0,180.0]
    public static float angle_diff(float angle, float value) {
        if ((angle -= value) > 180.0f) {
            return angle - 360.0f;
        }
        if (angle <= -180.0f) {
            return angle + 360.0f;
        }
        return angle;
    }
    // Range: [0.0,180.0]
    public static float angle_diff_abs(float angle, float value) {
        if ((angle -= value) < 0.0f) {
            angle = -angle;
        }
        if (angle > 180.0f) {
            return 360.0f - angle;
        }
        return angle;
    }
    // Range: [-180.0,180.0)
    public static float angle_rotate_180(float angle) {
        if ((angle += -180.0f) < -180.0f) {
            return angle + 360.0f;
        }
        return angle;
    }
}
