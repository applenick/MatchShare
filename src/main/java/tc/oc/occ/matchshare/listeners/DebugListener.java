package tc.oc.occ.matchshare.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import tc.oc.occ.matchshare.MatchShare;

public class DebugListener extends ShareListener {

  private static final String DEBUG_PERMISSION = "matchshare.debug";

  public DebugListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
    String input = event.getMessage();
    Player player = event.getPlayer();
    if (player == null || input == null) return;

    if (player.hasPermission(DEBUG_PERMISSION) && input.startsWith("/ggtest")) {
      input = input.replace("/ggtest ", "");

      boolean isGG = SportsmanshipListener.isGG(input);

      player.sendMessage(
          ChatColor.GOLD
              + "Sportsmanship regex test"
              + ChatColor.WHITE
              + ": "
              + ChatColor.GRAY
              + "("
              + ChatColor.AQUA
              + input
              + ChatColor.GRAY
              + ")"
              + (isGG ? ChatColor.GREEN + " matches!" : ChatColor.RED + " does not match!"));
      event.setCancelled(true);
    }
  }
}
