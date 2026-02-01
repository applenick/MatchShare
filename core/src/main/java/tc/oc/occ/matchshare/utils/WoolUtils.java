package tc.oc.occ.matchshare.utils;

import static tc.oc.pgm.util.material.ColorUtils.COLOR_UTILS;

import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.ParticipantState;
import tc.oc.pgm.goals.Goal;
import tc.oc.pgm.goals.GoalMatchModule;
import tc.oc.pgm.goals.ShowOption;
import tc.oc.pgm.util.material.MaterialData;
import tc.oc.pgm.util.material.MaterialMatcher;
import tc.oc.pgm.wool.MonumentWool;

public class WoolUtils {
  public static final MaterialMatcher WOOL = MaterialMatcher.of(m -> m.name().endsWith("WOOL"));

  public static boolean isHoldingWool(Player target) {
    ParticipantState player = PGM.get().getMatchManager().getParticipantState(target.getUniqueId());
    boolean holdingWool = false;
    if (player != null) {
      Competitor team = player.getParty();
      PlayerInventory inv = target.getInventory();
      holdingWool = Stream.of(inv.getContents())
          .filter(item -> item != null && WOOL.matches(item.getType()))
          .anyMatch(item -> WoolUtils.isEnemyWool(item, team));
    }
    return holdingWool;
  }

  public static boolean isEnemyWool(ItemStack stack, Competitor team) {
    if (stack == null || !WOOL.matches(stack.getType())) {
      return false;
    }
    GoalMatchModule gmm = team.getMatch().getModule(GoalMatchModule.class);

    if (gmm != null) {
      for (Goal<?> goal : gmm.getGoals()) {
        if (goal instanceof MonumentWool wool) {
          if (wool.hasShowOption(ShowOption.STATS)
              && !wool.isPlaced()
              && COLOR_UTILS.isColor(MaterialData.item(stack), wool.getDyeColor())) {
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
