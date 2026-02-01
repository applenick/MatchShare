package tc.oc.occ.matchshare.listeners;

import static tc.oc.occ.matchshare.util.PlatformUtils.PLATFORM_UTILS;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerBlockBreakEvent;
import tc.oc.occ.matchshare.MatchShare;

public class BlockListener extends ShareListener {

  public BlockListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();

    if (block == null || block.getType() == Material.AIR) return;
    if (player == null || !player.isOnline() || !isParticipating(player)) return;
    if (PLATFORM_UTILS.getBlockStrength(block) < 0.5) return;

    callNewEvent(new PGMPlayerBlockBreakEvent(player, block));
  }
}
