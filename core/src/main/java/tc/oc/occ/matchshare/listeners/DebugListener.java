package tc.oc.occ.matchshare.listeners;

import static net.kyori.adventure.text.Component.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.util.Audience;

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

      Component message = text()
          .append(text("Sportsmanship regex test", NamedTextColor.GOLD))
          .append(text(": ", NamedTextColor.WHITE))
          .append(text("(", NamedTextColor.GRAY))
          .append(text(input, NamedTextColor.AQUA))
          .append(text(")", NamedTextColor.GRAY))
          .append(
              isGG
                  ? text(" matches!", NamedTextColor.GREEN)
                  : text(" does not match!", NamedTextColor.RED))
          .build();

      Audience.get(player).sendMessage(message);
      event.setCancelled(true);
    }
  }
}
