package tc.oc.occ.matchshare.listeners;

import org.bukkit.event.EventHandler;
import tc.oc.occ.dewdrop.events.MatchLinkShareEvent;
import tc.oc.occ.dispense.events.match.PGMMatchShareLinkEvent;
import tc.oc.occ.matchshare.MatchShare;

public class WebsiteListener extends ShareListener {

  public WebsiteListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onMatchShareLink(MatchLinkShareEvent event) {
    callNewEvent(new PGMMatchShareLinkEvent(event.getMatchLink()));
  }
}
