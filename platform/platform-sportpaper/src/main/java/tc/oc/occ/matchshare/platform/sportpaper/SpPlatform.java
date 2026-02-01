package tc.oc.occ.matchshare.platform.sportpaper;

import static tc.oc.occ.matchshare.util.Supports.Priority.HIGHEST;
import static tc.oc.occ.matchshare.util.Supports.Variant.SPORTPAPER;

import org.bukkit.plugin.Plugin;
import tc.oc.occ.matchshare.util.Platform;
import tc.oc.occ.matchshare.util.Supports;

@Supports(value = SPORTPAPER, priority = HIGHEST)
public class SpPlatform implements Platform.Manifest {
  @Override
  public void onEnable(Plugin plugin) {}
}
