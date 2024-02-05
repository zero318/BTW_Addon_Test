package zero.test;
import btw.AddonHandler;
// Block piston reactions
public class ZeroUtil {
    public static final int[] rail_exit_directions = new int[] {
        3, 2,
        5, 4,
        5, 4,
        5, 4,
        3, 2,
        3, 2,
        2, 4,
        2, 5,
        3, 5,
        3, 4
    };
    public static final int[] rail_exit_flat_directions = new int[] {
        2, 0,
        1, 3,
        1, 3,
        1, 3,
        2, 0,
        2, 0,
        0, 3,
        0, 1,
        2, 1,
        2, 3
    };
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
    // Range: [0.0,179.0]
    public static float angle_plane(float angle) {
        if (angle < 0.0f) {
            return angle + 180.0f;
        }
        return angle;
    }
    // Range: [0.0,90.0]
    public static float angle_plane_diff(float angle, float value) {
        if (angle < 0.0f) {
            angle += 180.0f;
        }
        if (value < 0.0f) {
            value += 180.0f;
        }
        if ((angle -= value) < 0.0f) {
            angle = -angle;
        }
        if (angle >= 90.0f) {
            return 180.0f - angle;
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
