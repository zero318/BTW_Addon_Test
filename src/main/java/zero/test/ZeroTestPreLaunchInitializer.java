package zero.test;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import zero.test.ZeroTestAddon;
public class ZeroTestPreLaunchInitializer implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        ZeroTestAddon.getInstance();
    }
}
