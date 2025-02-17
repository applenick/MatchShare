package tc.oc.occ.matchshare.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerArrowLandEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerDamageEvent;
import tc.oc.occ.dispense.events.players.PGMPlayerDeathEvent;
import tc.oc.occ.matchshare.MatchShare;
import tc.oc.pgm.api.player.event.MatchPlayerDeathEvent;

public class PlayerCombatListener extends ShareListener {

  public PlayerCombatListener(MatchShare plugin) {
    super(plugin);
  }

  @EventHandler
  public void onKill(MatchPlayerDeathEvent event) {
    Player dead = event.getPlayer().getBukkit();
    Player killer = null;
    Player assister = null;

    if (event.getKiller() != null && event.getKiller().getPlayer().isPresent()) {
      killer = event.getKiller().getPlayer().get().getBukkit();
    }

    if (event.getAssister() != null && event.getAssister().getPlayer().isPresent()) {
      assister = event.getAssister().getPlayer().get().getBukkit();
    }

    callNewEvent(
        new PGMPlayerDeathEvent(
            dead, killer, assister, event.isSelfKill(), event.isTeamKill(), event.isSuicide()));
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onArrowImpact(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!(event.getDamager() instanceof Arrow)) return;
    Arrow arrow = (Arrow) event.getDamager();

    if (arrow.getShooter() instanceof Player) {
      Player shooter = (Player) arrow.getShooter();
      if (isParticipating(shooter) && event.getEntity() != shooter) {
        callNewEvent(new PGMPlayerArrowLandEvent(shooter));
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onDamageDealt(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (!(event.getDamager() instanceof Player)) return;

    Player attacker = (Player) event.getDamager();
    Player receiver = (Player) event.getEntity();

    if (isParticipating(attacker) && attacker != receiver) {
      double damage = Math.min(event.getDamage(), 20);
      callNewEvent(new PGMPlayerDamageEvent(attacker, receiver, damage));
    }
  }
}
