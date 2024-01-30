package zero.test;

import btw.AddonHandler;

#include "feature_flags.h"
#include "util.h"

public class ZeroUtil {
#if ENABLE_MODERN_PUBLISH_COMMAND
    public static int lan_port = 0;
#endif

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
    
    
    
#if ENABLE_DEBUG_STRING_JANK

#define PRINT_INTERVAL 10

    public static int print_index = 0;
    public static String print_text = "";
    public static void debug_print(String text) {
        print_text += text;
        if (++print_index == PRINT_INTERVAL) {
            print_index = 0;
            AddonHandler.logMessage(print_text);
            print_text = "";
        } else {
            print_text += "\n";
        }
    }
#endif
}