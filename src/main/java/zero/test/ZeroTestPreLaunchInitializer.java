package zero.test;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import zero.test.ZeroTestAddon;
public class ZeroTestPreLaunchInitializer implements PreLaunchEntrypoint {
    /**
     * Runs the PreLaunch entrypoint to register BTW-Addon.
     * Don't initialize anything else here, use
     * the method Initialize() in the Addon.
     */
    @Override
    public void onPreLaunch() {
        ZeroTestAddon.getInstance();
    }
}
