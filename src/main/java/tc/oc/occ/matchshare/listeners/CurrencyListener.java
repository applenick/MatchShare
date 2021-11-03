package tc.oc.occ.matchshare.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import tc.oc.occ.dispense.events.DisplayFakeItemsEvent;
import tc.oc.occ.dispense.events.currency.CurrencyType;
import tc.oc.occ.dispense.events.currency.GroupEarnCurrencyEvent;
import tc.oc.occ.dispense.events.currency.PlayerEarnCurrencyEvent;
import tc.oc.occ.dispense.events.objectives.PGMCoreLeakEvent;
import tc.oc.occ.dispense.events.objectives.PGMFlagCaptureEvent;
import tc.oc.occ.dispense.events.objectives.PGMMonumentDestroyEvent;
import tc.oc.occ.dispense.events.objectives.PGMScoreEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolCaptureEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolDestroyEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolTouchEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerDeathEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerVoteEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.occ.matchshare.utils.NMSHacks;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;

public class CurrencyListener extends ShareListener {

  private Cache<UUID, String> mapVotes;

  public CurrencyListener(MatchShare plugin) {
    super(plugin);
    this.mapVotes = CacheBuilder.newBuilder().build();
  }

  @EventHandler
  public void onKill(PGMPlayerDeathEvent event) {
    if (!event.isTeamKill()
        && !event.isSuicide()
        && !event.isSelfKill()
        && event.getKiller() != null) {
      callNewEvent(new PlayerEarnCurrencyEvent(event.getKiller(), CurrencyType.KILL));
    }
  }

  @EventHandler
  public void onWoolCapture(PGMWoolCaptureEvent event) {
    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.WOOL_CAPTURE));
  }

  @EventHandler
  public void onWoolTouch(PGMWoolTouchEvent event) {
    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.WOOL_TOUCH));
  }

  @EventHandler
  public void onCoreLeak(PGMCoreLeakEvent event) {
    callNewEvent(new GroupEarnCurrencyEvent(event.getPlayers(), CurrencyType.CORE));
  }

  @EventHandler
  public void onMonumentDestroy(PGMMonumentDestroyEvent event) {
    callNewEvent(new GroupEarnCurrencyEvent(event.getPlayers(), CurrencyType.MONUMENT));
  }

  @EventHandler
  public void onFlagCapture(PGMFlagCaptureEvent event) {
    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.FLAG));
  }

  @EventHandler
  public void onScorePoints(PGMScoreEvent event) {
    callNewEvent(
        new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.SCORE, true, event.getScore()));
  }

  @EventHandler
  public void onWoolDestroy(PGMWoolDestroyEvent event) {
    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.WOOL_DESTROY, true));
  }

  @EventHandler
  public void onMapVote(PGMPlayerVoteEvent event) {
    if (mapVotes.getIfPresent(event.getPlayer().getUniqueId()) == null) {
      callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.MAP_VOTE, true));
      mapVotes.put(event.getPlayer().getUniqueId(), event.getMapName());
    }
  }

  @EventHandler
  public void onMatchPhaseChange(MatchPhaseChangeEvent event) {
    this.mapVotes.invalidateAll();
  }

  @EventHandler
  public void onDisplayEffect(DisplayFakeItemsEvent event) {
    NMSHacks.showFakeItems(
        plugin,
        event.getPlayer(),
        event.getLocation(),
        new ItemStack(event.getMaterial()),
        event.getAmount(),
        event.getDelay());
  }
}
