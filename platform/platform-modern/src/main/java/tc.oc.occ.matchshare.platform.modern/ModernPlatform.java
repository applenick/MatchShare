package tc.oc.occ.matchshare.platform.modern;

import static tc.oc.occ.matchshare.util.Supports.Priority.HIGHEST;
import static tc.oc.occ.matchshare.util.Supports.Variant.PAPER;

import org.bukkit.plugin.Plugin;
import tc.oc.occ.matchshare.util.Platform;
import tc.oc.occ.matchshare.util.Supports;

@Supports(value = PAPER, minVersion = "1.21.10", priority = HIGHEST)
public class ModernPlatform implements Platform.Manifest {
  @Override
  public void onEnable(Plugin plugin) {}
}
