package tc.oc.occ.matchshare.listeners;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import tc.oc.occ.dispense.events.objectives.PGMCoreLeakEvent;
import tc.oc.occ.dispense.events.objectives.PGMFlagCaptureEvent;
import tc.oc.occ.dispense.events.objectives.PGMFlagDropEvent;
import tc.oc.occ.dispense.events.objectives.PGMFlagPickupEvent;
import tc.oc.occ.dispense.events.objectives.PGMMonumentDestroyEvent;
import tc.oc.occ.dispense.events.objectives.PGMScoreEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolCaptureEvent;
import tc.oc.occ.dispense.events.objectives.PGMWoolTouchEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.core.CoreLeakEvent;
import tc.oc.pgm.destroyable.DestroyableDestroyedEvent;
import tc.oc.pgm.flag.event.FlagCaptureEvent;
import tc.oc.pgm.flag.event.FlagPickupEvent;
import tc.oc.pgm.flag.event.FlagStateChangeEvent;
import tc.oc.pgm.flag.state.Dropped;
import tc.oc.pgm.goals.events.GoalTouchEvent;
import tc.oc.pgm.score.PlayerScoreEvent;
import tc.oc.pgm.wool.MonumentWool;
import tc.oc.pgm.wool.PlayerWoolPlaceEvent;

public class ObjectiveListener extends ShareListener {

  public ObjectiveListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onCoreLeak(CoreLeakEvent event) {
    List<Player> leakers = Lists.newArrayList();
    event
        .getCore()
        .getContributions()
        .forEach(
            leaker -> {
              if (leaker.getPlayerState() != null
                  && leaker.getPlayerState().getPlayer().isPresent()) {
                leakers.add(leaker.getPlayerState().getPlayer().get().getBukkit());
              }
            });
    callNewEvent(new PGMCoreLeakEvent(leakers));
  }

  @EventHandler
  public void onWoolTouch(GoalTouchEvent event) {
    // TODO: If we want to track other objective touches, rename method
    if (event.getPlayer() != null && event.getPlayer().getPlayer().isPresent()) {
      if (event.getGoal() instanceof MonumentWool) {
        if (event.isFirstForPlayer()) {
          callNewEvent(new PGMWoolTouchEvent(event.getPlayer().getPlayer().get().getBukkit()));
        }
      }
    }
  }

  @EventHandler
  public void onWoolCapture(PlayerWoolPlaceEvent event) {
    if (event.getPlayer() != null && event.getPlayer().getPlayer().isPresent()) {
      callNewEvent(new PGMWoolCaptureEvent(event.getPlayer().getPlayer().get().getBukkit()));
    }
  }

  @EventHandler
  public void onFlagCapture(FlagCaptureEvent event) {
    if (event.getCarrier() != null) {
      callNewEvent(new PGMFlagCaptureEvent(event.getCarrier().getBukkit()));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFlagPickup(FlagPickupEvent event) {
    if (!event.isCancelled() && event.getCarrier() != null) {
      callNewEvent(new PGMFlagPickupEvent(event.getCarrier().getBukkit()));

      // Start tracking flag time for player
      plugin.getFlagTracker().startTracking(event.getCarrier().getId());
    }
  }

  @EventHandler
  public void onFlagDrop(FlagStateChangeEvent event) {
    if (event.getNewState() instanceof Dropped) {
      Dropped dropped = (Dropped) event.getNewState();
      if (dropped.getDropper() != null) {
        callNewEvent(new PGMFlagDropEvent(dropped.getDropper().getBukkit()));
      }
    }
  }

  @EventHandler
  public void onMonumentDestroy(DestroyableDestroyedEvent event) {
    List<Player> destroyers = Lists.newArrayList();
    event
        .getDestroyable()
        .getContributions()
        .forEach(
            destroyer -> {
              if (destroyer.getPlayerState() != null
                  && destroyer.getPlayerState().getPlayer().isPresent()) {
                destroyers.add(destroyer.getPlayerState().getPlayer().get().getBukkit());
              }
            });

    if (!destroyers.isEmpty()) {
      callNewEvent(new PGMMonumentDestroyEvent(destroyers));
    }
  }

  @EventHandler
  public void onPlayerScore(PlayerScoreEvent event) {
    if (event.getPlayer() != null && event.getScore() > 0) {
      callNewEvent(new PGMScoreEvent(event.getPlayer().getBukkit(), (int) event.getScore()));
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onMatchEnd(MatchFinishEvent event) {
    plugin.getFlagTracker().reset();
  }
}
