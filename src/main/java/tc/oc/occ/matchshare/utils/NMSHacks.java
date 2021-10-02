package tc.oc.occ.matchshare.utils;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Took some stuff from
 * https://github.com/OvercastNetwork/ProjectAres/blob/master/Util/bukkit/src/main/java/tc/oc/commons/bukkit/util/NMSHacks.java
 * for fancy item drops
 */
public class NMSHacks {

  private static final Random random = new Random();

  public static void sendPacket(Player bukkitPlayer, Object packet) {
    if (bukkitPlayer.isOnline()) {
      EntityPlayer nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
      nmsPlayer.playerConnection.sendPacket((Packet) packet);
    }
  }

  private static double randomEntityVelocity() {
    return random.nextDouble();
  }

  public static void showFakeItems(
      Plugin plugin,
      Player viewer,
      Location location,
      ItemStack item,
      int count,
      Duration duration) {
    if (count <= 0) return;

    final EntityPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
    final int[] entityIds = new int[count];

    for (int i = 0; i < count; i++) {
      final EntityItem entity =
          new EntityItem(
              nmsPlayer.getWorld(),
              location.getX(),
              location.getY(),
              location.getZ(),
              CraftItemStack.asNMSCopy(item));

      entity.motX = randomEntityVelocity();
      entity.motY = randomEntityVelocity();
      entity.motZ = randomEntityVelocity();

      sendPacket(viewer, new PacketPlayOutSpawnEntity(entity, 2));
      sendPacket(
          viewer, new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));

      entityIds[i] = entity.getId();
    }

    scheduleEntityDestroy(plugin, viewer.getUniqueId(), duration, entityIds);
  }

  private static void scheduleEntityDestroy(
      Plugin plugin, UUID viewerUuid, Duration delay, int[] entityIds) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskLater(
            plugin,
            () -> {
              final Player viewer = plugin.getServer().getPlayer(viewerUuid);
              if (viewer != null) {
                sendPacket(viewer, new PacketPlayOutEntityDestroy(entityIds));
              }
            },
            delay.getSeconds() * 20);
  }
}
