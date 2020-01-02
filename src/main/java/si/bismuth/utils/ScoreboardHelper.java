package si.bismuth.utils;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import org.apache.commons.lang3.StringUtils;
import si.bismuth.MCServer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHelper {
	final static ServerScoreboard board = (ServerScoreboard) MCServer.server.getWorld(0).getScoreboard();

	public static void setSidebarScoreboard(List<String> args) {
		final ScoreObjective objective = ScoreboardHelper.getObjective(args, board);
		board.setObjectiveInDisplaySlot(1, objective);
	}

	public static ScoreObjective getObjective(List<String> args, Scoreboard board) {
		final Collection<String> objectives = getObjectiveNames();
		return args.size() > 2 ? board.getObjective(getClosestMatch(objectives, args.get(2))) : null;
	}

	private static List<String> getObjectiveNames() {
		final Collection<ScoreObjective> collection = board.getScoreObjectives();
		return collection.stream().filter(o -> !o.getCriteria().isReadOnly()).map(ScoreObjective::getName).collect(Collectors.toList());
	}

	private static String getClosestMatch(Collection<String> collection, String target) {
		int distance = Integer.MAX_VALUE;
		String closest = "";
		for (String s : collection) {
			int currentDistance = StringUtils.getLevenshteinDistance(s, target);
			if (currentDistance < distance) {
				distance = currentDistance;
				closest = s;
			}
		}

		return closest;
	}
}
