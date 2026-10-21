package tc.oc.occ.matchshare.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.integration.Integration;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.event.PlayerVanishEvent;
import tc.oc.pgm.blitz.BlitzMatchModule;
import tc.oc.pgm.events.PlayerJoinPartyEvent;
import tc.oc.pgm.teams.Team;

public class PlayerMetaListener extends ShareListener {

  public static final String OBS_KEY = "isObserving";
  public static final String PLAY_KEY = "isPlaying";
  public static final String MAP_KEY = "map";
  public static final String NICK_KEY = "isNicked";
  public static final String NICK_NAME_KEY = "nickName";
  public static final String BLITZ_KEY = "isBlitz";
  public static final String PARTY_KEY = "partyName";
  public static final String PARTY_COLOR_KEY = "partyColor";
  public static final String TEAM_MAX_KEY = "teamMax";
  public static final String VANISHED_KEY = "isVanished";

  private static final String[] ALL_KEYS = {
    OBS_KEY,
    PLAY_KEY,
    MAP_KEY,
    NICK_KEY,
    NICK_NAME_KEY,
    BLITZ_KEY,
    PARTY_KEY,
    PARTY_COLOR_KEY,
    TEAM_MAX_KEY,
    VANISHED_KEY
  };

  public PlayerMetaListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoinServer(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    String nickname = Integration.getNick(player);
    if (nickname != null) {
      player.setMetadata(NICK_KEY, new FixedMetadataValue(plugin, true));
      player.setMetadata(NICK_NAME_KEY, new FixedMetadataValue(plugin, nickname));
    } else {
      player.removeMetadata(NICK_KEY, plugin);
      player.removeMetadata(NICK_NAME_KEY, plugin);
    }

    setVanished(player, Integration.isVanished(player));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuitServer(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    for (String key : ALL_KEYS) {
      player.removeMetadata(key, plugin);
    }
  }

  @EventHandler
  public void onToggleVanish(PlayerVanishEvent event) {
    setVanished(event.getPlayer().getBukkit(), event.isVanished());
  }

  private void setVanished(Player player, boolean vanished) {
    if (vanished) {
      player.setMetadata(VANISHED_KEY, new FixedMetadataValue(plugin, true));
    } else {
      player.removeMetadata(VANISHED_KEY, plugin);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinPartyEvent event) {
    setMeta(
        event.getPlayer().getBukkit(),
        event.getNewParty(),
        event.getMatch().getMap().getName(),
        event.getMatch().isRunning(),
        event.getMatch().getModule(BlitzMatchModule.class) != null);
  }

  private void setMeta(Player player, Party party, String map, boolean running, boolean blitz) {
    boolean participating = party instanceof Competitor;

    if (party instanceof Team team) {
      player.setMetadata(TEAM_MAX_KEY, new FixedMetadataValue(plugin, team.getMaxPlayers()));
    } else {
      player.removeMetadata(TEAM_MAX_KEY, plugin);
    }

    player.setMetadata(MAP_KEY, new FixedMetadataValue(plugin, map));
    player.setMetadata(participating ? PLAY_KEY : OBS_KEY, new FixedMetadataValue(plugin, true));
    player.removeMetadata(participating ? OBS_KEY : PLAY_KEY, plugin);
    player.setMetadata(BLITZ_KEY, new FixedMetadataValue(plugin, blitz));
    player.setMetadata(PARTY_KEY, new FixedMetadataValue(plugin, party.getNameLegacy()));
    player.setMetadata(
        PARTY_COLOR_KEY, new FixedMetadataValue(plugin, party.getColor().name()));
  }
}
