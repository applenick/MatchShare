package tc.oc.occ.matchshare.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.events.PlayerJoinPartyEvent;

public class PlayerMetaListener extends ShareListener {

  public static final String OBS_KEY = "isObserving";
  public static final String PLAY_KEY = "isPlaying";
  public static final String MAP_KEY = "map";

  public PlayerMetaListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinPartyEvent event) {
    setMeta(
        event.getPlayer().getBukkit(),
        event.getNewParty() instanceof Competitor,
        event.getMatch().getMap().getName(),
        event.getMatch().isRunning());
  }

  private void setMeta(Player player, boolean participating, String map, boolean running) {
    player.setMetadata(MAP_KEY, new FixedMetadataValue(plugin, map));
    player.setMetadata(participating ? PLAY_KEY : OBS_KEY, new FixedMetadataValue(plugin, true));
    player.removeMetadata(participating ? OBS_KEY : PLAY_KEY, plugin);
  }
}
