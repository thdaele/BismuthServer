package si.bismuth.discord;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.commons.lang3.StringUtils;
import si.bismuth.MCServer;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class DCBot extends ListenerAdapter {
	final public static long BismuthID = 635252849571266580L;
	final public static long ChannelID = 635254222916419590L;
	final public static String channelURL = String.format("https://discordapp.com/channels/%d/%d/", BismuthID, ChannelID);
	final public static String PREFIX = ";";
	final public JDA jda;

	public DCBot(String token) throws LoginException {
		this.jda = new JDABuilder(AccountType.BOT)
				.setToken(token)
				.addEventListeners(this)
				.build();
		this.sendToDiscord("Server started!");
	}

	public void sendToDiscord(String message) {
		try {
			MCServer.bot.jda.getTextChannelById(DCBot.ChannelID).sendMessage(message).queue();
		} catch (Exception ignored) {
			// noop
		}
	}

	public void sendDeathmessage(ITextComponent component) {
		this.sendToDiscord("**\uD83D\uDD71 " + component.getUnformattedText() + "**");
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot() || event.getGuild().getIdLong() != BismuthID) {
			return;
		}

		// TODO: handle DC2MC images
		final MessageChannel channel = event.getChannel();
		final Member member = event.getMember();
		final Message message = event.getMessage();
		final String content = message.getContentDisplay();
		if (channel.getIdLong() == ChannelID && !content.startsWith(PREFIX)) {
			final String name = event.getMember().getEffectiveName();
			final ITextComponent symbol = new TextComponentString("\u24B9");
			final HoverEvent hoverText = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(member.getUser().getAsTag()));
			final ClickEvent clickText = new ClickEvent(ClickEvent.Action.OPEN_URL, channelURL);
			symbol.getStyle().setColor(TextFormatting.BLUE).setHoverEvent(hoverText).setClickEvent(clickText);
			final ITextComponent text = new TextComponentTranslation("chat.type.text", name, content);
			MCServer.server.addScheduledTask(() -> MCServer.server.getPlayerList().sendMessage(new TextComponentString("").appendSibling(symbol).appendSibling(text)));
			return;
		}

		final String[] args = content.split(" ");
		if (args.length == 0) {
			return;
		}

		final String command = args[0];
		if (this.isCommand(command, new String[]{"list", "players", "online"})) {
			MCServer.server.addScheduledTask(() -> {
				final String[] players = MCServer.server.getOnlinePlayerNames();
				String title;
				if (players.length == 0) {
					title = "No players online!";
				} else {
					title = players.length + " player" + (players.length > 1 ? "s" : "") + " online:";
				}

				final MessageEmbed.AuthorInfo author = new MessageEmbed.AuthorInfo("BismuthBot", null, "https://i.imgur.com/a2w3DjI.png", null);
				final MessageEmbed embed = new MessageEmbed(null, title, StringUtils.join(players, "\n").replaceAll("_", "\\\\_"), EmbedType.RICH, null, 0x8665BD, null, null, author, null, null, null, null);
				channel.sendMessage(embed).queue();
			});

			return;
		}

		/*if (this.isCommand(command, new String[]{"s", "score", "scores", "scoreboard"})) {
			MCServer.server.addScheduledTask(() -> ScoreboardHelper.setSidebarScoreboard(args));
			return;
		}*/
	}

	private boolean isCommand(String text, String[] command) {
		return Arrays.stream(command).anyMatch(s -> text.startsWith(PREFIX + s));
	}
}
