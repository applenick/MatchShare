package tc.oc.occ.matchshare.listeners;

import dev.pgm.community.assistance.PlayerHelpRequest;
import dev.pgm.community.assistance.Report;
import dev.pgm.community.events.PlayerHelpRequestEvent;
import dev.pgm.community.events.PlayerReportEvent;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import tc.oc.occ.dispense.events.assistance.CommunityAssistEvent;
import tc.oc.occ.dispense.events.assistance.CommunityReportEvent;
import tc.oc.occ.matchshare.MatchShare;

public class AssistanceListener extends ShareListener {

  public AssistanceListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onPlayerAssist(PlayerHelpRequestEvent event) {
    PlayerHelpRequest report = event.getRequest();

    Player sender = getPlayer(report.getSenderId());
    String reason = report.getReason();
    String server = report.getServer();

    if (sender == null) return;

    callNewEvent(new CommunityAssistEvent(sender, reason, server));
  }

  @EventHandler
  public void onPlayerReport(PlayerReportEvent event) {
    Report report = event.getReport();

    Player sender = getPlayer(report.getSenderId());
    Player target = getPlayer(report.getTargetId());
    String reason = report.getReason();
    String server = report.getServer();

    if (sender == null || target == null) return;

    callNewEvent(new CommunityReportEvent(sender, target, reason, server));
  }

  private Player getPlayer(UUID playerId) {
    return Bukkit.getPlayer(playerId);
  }
}
