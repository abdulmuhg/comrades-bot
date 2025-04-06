package main.java.com.abdulmuhg.bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class CommandManager extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private static final String PREFIX = "!";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore messages from bots
        if (event.getAuthor().isBot())
            return;

        // Check if the message starts with our prefix
        if (!event.getMessage().getContentRaw().startsWith(PREFIX))
            return;

        // Parse the command and arguments
        String[] split = event.getMessage().getContentRaw().substring(PREFIX.length()).trim().split("\\s+");
        String command = split[0].toLowerCase();
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        TextChannel channel = event.getChannel().asTextChannel();

        // Handle different commands
        switch (command) {
            case "play":
                handlePlayCommand(channel, event.getMember(), args);
                break;
            case "skip":
                handleSkipCommand(channel, event.getMember());
                break;
            case "stop":
                handleStopCommand(channel, event.getMember());
                break;
            case "queue":
                handleQueueCommand(channel, event.getGuild());
                break;
            case "help":
                handleHelpCommand(channel);
                break;
            default:
                channel.sendMessage("Unknown command. Use !help for a list of commands.").queue();
                break;
        }
    }

    private void handlePlayCommand(TextChannel channel, Member member, String[] args) {
        if (args.length == 0) {
            channel.sendMessage("Please provide a song URL or search terms.").queue();
            return;
        }

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        VoiceChannel voiceChannel = voiceState.getChannel().asVoiceChannel();
        AudioManager audioManager = channel.getGuild().getAudioManager();

        try {
            if (!audioManager.isConnected()) {
                audioManager.openAudioConnection(voiceChannel);
                channel.sendMessage("Joining voice channel: " + voiceChannel.getName()).queue();
            }
        } catch (InsufficientPermissionException e) {
            channel.sendMessage("I don't have permission to join your voice channel.").queue();
            return;
        }

        String searchTerm = String.join(" ", args);

        // If it doesn't look like a URL, prefix it with "ytsearch:"
        if (!searchTerm.startsWith("http")) {
            searchTerm = "ytsearch:" + searchTerm;
        }

        PlayerManager.getInstance().loadAndPlay(channel, searchTerm, member);
    }

    private void handleSkipCommand(TextChannel channel, Member member) {
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        PlayerManager.getInstance().skipTrack(channel);
        channel.sendMessage("Skipped to the next track.").queue();
    }

    private void handleStopCommand(TextChannel channel, Member member) {
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        PlayerManager.getInstance().stopAndClear(channel.getGuild());
        channel.getGuild().getAudioManager().closeAudioConnection();
        channel.sendMessage("Playback stopped and queue cleared.").queue();
    }

    private void handleQueueCommand(TextChannel channel, Guild guild) {
        PlayerManager.getInstance().sendQueueInformation(channel, guild);
    }

    private void handleHelpCommand(TextChannel channel) {
        StringBuilder helpMessage = new StringBuilder("**Discord Music Bot Commands:**\n");
        helpMessage.append("`!play <song name or URL>` - Play a song or add it to the queue\n");
        helpMessage.append("`!skip` - Skip the current song\n");
        helpMessage.append("`!stop` - Stop playback and clear the queue\n");
        helpMessage.append("`!queue` - View the current queue\n");
        helpMessage.append("`!help` - Show this help message\n");

        channel.sendMessage(helpMessage.toString()).queue();
    }
}