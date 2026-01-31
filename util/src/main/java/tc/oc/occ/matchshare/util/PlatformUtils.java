package tc.oc.occ.matchshare.util;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface PlatformUtils {
  PlatformUtils PLATFORM_UTILS = Platform.get(PlatformUtils.class);

  static double randomEntityVelocity() {
    return new Random().nextDouble() - 0.5D;
  }

  void dummy();

  void sendPacket(Player bukkitPlayer, Object packet);

  void scheduleEntityDestroy(Plugin plugin, UUID viewerUuid, Duration delay, int[] entityIds);

  void showFakeItems(
      Plugin plugin,
      Player viewer,
      Location location,
      ItemStack item,
      int count,
      Duration duration);

  float getBlockStrength(Block block);
}
