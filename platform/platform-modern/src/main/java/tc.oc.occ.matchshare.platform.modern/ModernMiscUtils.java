package tc.oc.occ.matchshare.platform.modern;

import static tc.oc.pgm.util.platform.Supports.Variant.PAPER;

import java.time.Duration;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import tc.oc.occ.matchshare.util.MiscUtils;
import tc.oc.pgm.util.platform.Supports;

@Supports(value = PAPER, minVersion = "1.21.6")
public class ModernMiscUtils implements MiscUtils {
  @Override
  public void sendPacket(Player bukkitPlayer, Object packet) {
    if (bukkitPlayer.isOnline()) {
      var nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
      nmsPlayer.connection.send((Packet<?>) packet);
    }
  }

  @Override
  public void scheduleEntityDestroy(
      Plugin plugin, UUID viewerUuid, Duration delay, int[] entityIds) {
    plugin
        .getServer()
        .getScheduler()
        .runTaskLater(
            plugin,
            () -> {
              final Player viewer = plugin.getServer().getPlayer(viewerUuid);
              if (viewer != null) {
                sendPacket(viewer, new ClientboundRemoveEntitiesPacket(entityIds));
              }
            },
            delay.getSeconds() * 20);
  }

  @Override
  public void showFakeItems(
      Plugin plugin,
      Player viewer,
      Location location,
      ItemStack item,
      int count,
      Duration duration) {
    if (count <= 0) return;

    final ServerPlayer nmsPlayer = ((CraftPlayer) viewer).getHandle();
    final int[] entityIds = new int[count];

    for (int i = 0; i < count; i++) {
      final ItemEntity entity = new ItemEntity(
          nmsPlayer.level(),
          location.getX(),
          location.getY(),
          location.getZ(),
          CraftItemStack.asNMSCopy(item));

      entity.setDeltaMovement(
          MiscUtils.randomEntityVelocity(),
          MiscUtils.randomEntityVelocity(),
          MiscUtils.randomEntityVelocity());

      sendPacket(
          viewer,
          new ClientboundAddEntityPacket(
              entity,
              2,
              new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
      sendPacket(
          viewer,
          new ClientboundSetEntityDataPacket(
              entity.getId(), entity.getEntityData().packAll()));

      entityIds[i] = entity.getId();
    }

    scheduleEntityDestroy(plugin, viewer.getUniqueId(), duration, entityIds);
  }

  @Override
  public float getBlockStrength(Block block) {
    return ((CraftBlockType<?>) block).getHardness();
  }
}
