package tc.oc.occ.matchshare.utils;

import java.util.stream.Stream;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Wool;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.ParticipantState;
import tc.oc.pgm.goals.Goal;
import tc.oc.pgm.goals.GoalMatchModule;
import tc.oc.pgm.goals.ShowOption;
import tc.oc.pgm.util.material.Materials;
import tc.oc.pgm.wool.MonumentWool;

public class WoolUtils {

  public static boolean isHoldingWool(Player target) {
    ParticipantState player = PGM.get().getMatchManager().getParticipantState(target.getUniqueId());
    boolean holdingWool = false;
    if (player != null) {
      Competitor team = player.getParty();
      PlayerInventory inv = target.getInventory();
      if (inv.contains(Materials.WOOL)) {
        holdingWool =
            Stream.of(inv.getContents())
                .filter(item -> item != null && item.getType() == Materials.WOOL)
                .anyMatch(item -> WoolUtils.isEnemyWool(item, team));
      }
    }
    return holdingWool;
  }

  public static boolean isEnemyWool(ItemStack stack, Competitor team) {
    if (stack == null || stack.getType() != Materials.WOOL) {
      return false;
    }
    DyeColor color = ((Wool) stack.getData()).getColor();
    GoalMatchModule gmm = team.getMatch().getModule(GoalMatchModule.class);

    if (gmm != null) {
      for (Goal goal : gmm.getGoals()) {
        if (goal instanceof MonumentWool) {
          MonumentWool wool = (MonumentWool) goal;
          if (wool.hasShowOption(ShowOption.STATS)
              && !wool.isPlaced()
              && wool.getDyeColor() == color) {
            if (wool.getOwner() == team) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
}
