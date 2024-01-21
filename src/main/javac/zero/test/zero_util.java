package zero.test;

#include "feature_flags.h"
#include "util.h"

public class ZeroUtil {
#if ENABLE_MODERN_PUBLISH_COMMAND
    public static int lan_port = 0;
#endif
}