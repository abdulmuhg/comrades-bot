package main.java.com.abdulmuhg.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildMusicManager musicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
            return musicManager;
        });
    }

    public void loadAndPlay(TextChannel channel, String trackUrl, Member requester) {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                TrackInfo trackInfo = new TrackInfo(track, requester);
                channel.sendMessage("Adding to queue: " + track.getInfo().title).queue();
                musicManager.scheduler.queue(trackInfo);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (trackUrl.startsWith("ytsearch:")) {
                    // If this is a search, just load the first result
                    AudioTrack firstTrack = playlist.getTracks().get(0);
                    TrackInfo trackInfo = new TrackInfo(firstTrack, requester);
                    channel.sendMessage("Adding to queue: " + firstTrack.getInfo().title).queue();
                    musicManager.scheduler.queue(trackInfo);
                } else {
                    // Otherwise add the entire playlist
                    channel.sendMessage("Adding playlist to queue: " + playlist.getName() + " ("
                            + playlist.getTracks().size() + " tracks)").queue();

                    for (AudioTrack track : playlist.getTracks()) {
                        TrackInfo trackInfo = new TrackInfo(track, requester);
                        musicManager.scheduler.queue(trackInfo);
                    }
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found for: " + trackUrl.replace("ytsearch:", "")).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                channel.sendMessage("Could not play: " + e.getMessage()).queue();
            }
        });
    }

    public void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildMusicManager(channel.getGuild());
        musicManager.scheduler.nextTrack();
    }

    public void stopAndClear(Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.scheduler.clearQueue();
        musicManager.player.stopTrack();
    }

    public void sendQueueInformation(TextChannel channel, Guild guild) {
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        musicManager.scheduler.printQueue(channel);
    }
}
