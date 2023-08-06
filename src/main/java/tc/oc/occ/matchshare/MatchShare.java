package tc.oc.occ.matchshare;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.occ.matchshare.listeners.BlockListener;
import tc.oc.occ.matchshare.listeners.CurrencyListener;
import tc.oc.occ.matchshare.listeners.DebugListener;
import tc.oc.occ.matchshare.listeners.MapListener;
import tc.oc.occ.matchshare.listeners.ObjectiveListener;
import tc.oc.occ.matchshare.listeners.ObserverListener;
import tc.oc.occ.matchshare.listeners.PlayerCombatListener;
import tc.oc.occ.matchshare.listeners.PlayerMetaListener;
import tc.oc.occ.matchshare.listeners.SportsmanshipListener;
import tc.oc.occ.matchshare.listeners.StatsListener;
import tc.oc.occ.matchshare.listeners.TimeTrackerListener;
import tc.oc.occ.matchshare.listeners.VoteListener;
import tc.oc.occ.matchshare.listeners.WoolDestructionListener;
import tc.oc.occ.matchshare.tracker.MatchTimeTracker;

public class MatchShare extends JavaPlugin implements Listener {

  private MatchTimeTracker timeTracker;

  @Override
  public void onEnable() {
    this.timeTracker = new MatchTimeTracker();

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
  }

  public MatchTimeTracker getTimeTracker() {
    return timeTracker;
  }

  private void registerEvents(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }
}
