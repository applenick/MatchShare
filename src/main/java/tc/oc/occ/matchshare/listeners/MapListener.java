package tc.oc.occ.matchshare.listeners;

import static net.kyori.adventure.text.Component.text;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import tc.oc.occ.dispense.events.map.PGMMapListEvent;
import tc.oc.occ.dispense.events.map.PGMMapLoadEvent;
import tc.oc.occ.dispense.events.map.PGMRefreshMapDataEvent;
import tc.oc.occ.dispense.events.match.MatchStatus;
import tc.oc.occ.dispense.events.match.PGMMatchStatusUpdateEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.Gamemode;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;
import tc.oc.pgm.util.text.TextFormatter;
import tc.oc.pgm.util.text.TextTranslations;

public class MapListener extends ShareListener {

  public MapListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onRefreshMapData(PGMRefreshMapDataEvent event) {
    PGM.get().getDatastore().refreshMapData();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onMapListLoad(PGMMapListEvent event) {
    Set<String> maps = Sets.newHashSet();
    PGM.get().getMapLibrary().getMaps().forEachRemaining(map -> maps.add(map.getName()));
    event.setMaps(maps);
  }

  @EventHandler
  public void onMapLoad(MatchLoadEvent event) {
    MapInfo map = event.getMatch().getMap();
    String name = map.getName();
    String desc = map.getDescription();
    int maxPlayers = map.getMaxPlayers().stream().reduce(0, Integer::sum);
    String gamemode = "";

    TextComponent.Builder gmc = text();
    Collection<Gamemode> gamemodes = map.getGamemodes();
    if (map.getGamemode() != null) {
      gmc.append(map.getGamemode());
    } else if (!gamemodes.isEmpty()) {
      boolean acronyms = gamemodes.size() > 1;
      gmc.append(
          TextFormatter.list(
              gamemodes.stream()
                  .map(gm -> text(acronyms ? gm.getAcronym() : gm.getFullName()))
                  .collect(Collectors.toList()),
              NamedTextColor.AQUA));
    }

    gamemode = TextTranslations.translateLegacy(gmc.build(), Bukkit.getConsoleSender());

    callNewEvent(new PGMMapLoadEvent(name, desc, gamemode, maxPlayers));
  }

  @EventHandler
  public void onMatchStatusChange(MatchPhaseChangeEvent event) {
    callNewEvent(new PGMMatchStatusUpdateEvent(convertPhase(event.getNewPhase())));
  }

  private MatchStatus convertPhase(MatchPhase phase) {
    switch (phase) {
      case FINISHED:
        return MatchStatus.FINISHED;
      case RUNNING:
        return MatchStatus.RUNNING;
      case STARTING:
        return MatchStatus.STARTING;
      default:
        return MatchStatus.IDLE;
    }
  }
}
