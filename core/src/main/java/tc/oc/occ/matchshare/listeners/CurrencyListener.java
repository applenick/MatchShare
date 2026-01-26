package tc.oc.occ.matchshare.listeners;

import static tc.oc.occ.matchshare.util.MiscUtils.MISC_UTILS;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import tc.oc.occ.dispense.events.DisplayFakeItemsEvent;
import tc.oc.occ.dispense.events.battlepass.PlayerCompleteMissionEvent;
import tc.oc.occ.dispense.events.currency.CurrencyType;
import tc.oc.occ.dispense.events.currency.GroupEarnCurrencyEvent;
import tc.oc.occ.dispense.events.currency.PlayerEarnCurrencyEvent;
import tc.oc.occ.dispense.events.objectives.PGMCoreLeakEvent;
import tc.oc.occ.dispense.events.objectives.PGMFlagCaptureEvent;
import tc.oc.occ.dispense.events.objectives.PGMFlagPickupEvent;
import tc.oc.occ.dispense.events.objectives.PGMMonumentDestroyEvent;
import tc.oc.occ.dispense.events.objectives.PGMScoreEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolCaptureEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolDestroyEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolTouchEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerDeathEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerSportsmanshipEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerVoteEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.occ.matchshare.util.WoolUtils;
import tc.oc.pgm.api.match.event.MatchPhaseChangeEvent;

public class CurrencyListener extends ShareListener {

  private final Cache<UUID, String> mapVotes;

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
      CurrencyType type = WoolUtils.isHoldingWool(event.getDead())
          ? CurrencyType.KILL_WOOL_HOLDER
          : CurrencyType.KILL;
      callNewEvent(new PlayerEarnCurrencyEvent(event.getKiller(), type));

      if (event.getAssister() != null) {
        callNewEvent(
            new PlayerEarnCurrencyEvent(event.getAssister(), CurrencyType.KILL_ASSIST, true));
      }
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
    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.FLAG_CAPTURE));
  }

  @EventHandler
  public void onFlagPickup(PGMFlagPickupEvent event) {
    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), CurrencyType.FLAG_PICKUP));
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
  public void onMissionComplete(PlayerCompleteMissionEvent event) {
    callNewEvent(new PlayerEarnCurrencyEvent(
        event.getPlayer(),
        CurrencyType.MISSION_COMPLETION,
        true,
        event.getReward(),
        event.getMissionName()));
  }

  @EventHandler
  public void onGoodSportsmanship(PGMPlayerSportsmanshipEvent event) {
    CurrencyType type = CurrencyType.SPORTSMANSHIP_OBS;

    if (event.isParticipant()) {
      type = CurrencyType.SPORTSMANSHIP_LOSER;
    }

    if (event.isWinner()) {
      type = CurrencyType.SPORTSMANSHIP;
    }

    callNewEvent(new PlayerEarnCurrencyEvent(event.getPlayer(), type, true));
  }

  @EventHandler
  public void onDisplayEffect(DisplayFakeItemsEvent event) {
    MISC_UTILS.showFakeItems(
        plugin,
        event.getPlayer(),
        event.getLocation(),
        new ItemStack(event.getMaterial()),
        event.getAmount(),
        event.getDelay());
  }
}
