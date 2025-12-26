package tc.oc.occ.matchshare;

import java.util.logging.Level;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.occ.matchshare.listeners.AssistanceListener;
import tc.oc.occ.matchshare.listeners.BlockListener;
import tc.oc.occ.matchshare.listeners.CurrencyListener;
import tc.oc.occ.matchshare.listeners.DebugListener;
import tc.oc.occ.matchshare.listeners.MapListener;
import tc.oc.occ.matchshare.listeners.ObjectiveListener;
import tc.oc.occ.matchshare.listeners.ObserverListener;
import tc.oc.occ.matchshare.listeners.PlayerCombatListener;
import tc.oc.occ.matchshare.listeners.PlayerJoinListener;
import tc.oc.occ.matchshare.listeners.PlayerMetaListener;
import tc.oc.occ.matchshare.listeners.RestartListener;
import tc.oc.occ.matchshare.listeners.SportsmanshipListener;
import tc.oc.occ.matchshare.listeners.StatsListener;
import tc.oc.occ.matchshare.listeners.TimeTrackerListener;
import tc.oc.occ.matchshare.listeners.VoteListener;
import tc.oc.occ.matchshare.listeners.WebsiteListener;
import tc.oc.occ.matchshare.listeners.WoolDestructionListener;
import tc.oc.occ.matchshare.tracker.MatchCapacityTracker;
import tc.oc.occ.matchshare.tracker.MatchTimeTracker;
import tc.oc.occ.matchshare.util.Platform;

public class MatchShare extends JavaPlugin implements Listener {

  private MatchTimeTracker timeTracker;
  private MatchCapacityTracker capacityTracker;

  @Override
  public void onEnable() {
    this.timeTracker = new MatchTimeTracker();
    this.capacityTracker = new MatchCapacityTracker(this);

    try {
      Platform.init();
    } catch (Throwable t) {
      getLogger().log(Level.SEVERE, "Failed to initialize MatchShare platform", t);
      getServer().getPluginManager().disablePlugin(this);
    }

    registerEvents(new DebugListener(this));
    registerEvents(new PlayerMetaListener(this));
    registerEvents(new PlayerCombatListener(this));
    registerEvents(new TimeTrackerListener(this));
    registerEvents(new ObjectiveListener(this));
    registerEvents(new MapListener(this));
    registerEvents(new CurrencyListener(this));
    registerEvents(new ObserverListener(this));
    registerEvents(new WoolDestructionListener(this));
    registerEvents(new VoteListener(this));
    registerEvents(new StatsListener(this));
    registerEvents(new BlockListener(this));
    registerEvents(new SportsmanshipListener(this));
    registerEvents(new RestartListener(this));
    registerEvents(new WebsiteListener(this));
    registerEvents(new PlayerJoinListener(this));
    registerEvents(new AssistanceListener(this));
  }

  public MatchTimeTracker getTimeTracker() {
    return timeTracker;
  }

  private void registerEvents(Listener listener) {
    Platform.MANIFEST.onEnable(this);
    getServer().getPluginManager().registerEvents(listener, this);
  }
}
