package main.java.com.abdulmuhg.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);
    private final AudioPlayer player;
    private final BlockingQueue<TrackInfo> queue;
    private TrackInfo currentTrack;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(TrackInfo trackInfo) {
        if (!player.startTrack(trackInfo.getTrack(), true)) {
            queue.offer(trackInfo);
        } else {
            currentTrack = trackInfo;
        }
    }

    public void nextTrack() {
        TrackInfo nextTrack = queue.poll();

        if (nextTrack != null) {
            player.startTrack(nextTrack.getTrack(), false);
            currentTrack = nextTrack;
        } else {
            player.stopTrack();
            currentTrack = null;
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public void printQueue(TextChannel channel) {
        StringBuilder queueMessage = new StringBuilder("**Current Queue:**\n");

        // Add current track
        if (player.getPlayingTrack() != null && currentTrack != null) {
            AudioTrack track = player.getPlayingTrack();
            queueMessage.append("**Now Playing:** ")
                    .append(track.getInfo().title)
                    .append(" [")
                    .append(formatDuration(track.getPosition()))
                    .append("/")
                    .append(formatDuration(track.getDuration()))
                    .append("] requested by ")
                    .append(currentTrack.getRequester().getEffectiveName())
                    .append("\n\n");
        } else {
            queueMessage.append("**Now Playing:** Nothing\n\n");
        }

        // Add queued tracks
        if (queue.isEmpty()) {
            queueMessage.append("No tracks in queue.");
        } else {
            List<TrackInfo> trackInfoList = new ArrayList<>(queue);

            for (int i = 0; i < trackInfoList.size() && i < 10; i++) {
                TrackInfo trackInfo = trackInfoList.get(i);
                AudioTrack track = trackInfo.getTrack();

                queueMessage.append(i + 1)
                        .append(". ")
                        .append(track.getInfo().title)
                        .append(" [")
                        .append(formatDuration(track.getDuration()))
                        .append("] requested by ")
                        .append(trackInfo.getRequester().getEffectiveName())
                        .append("\n");
            }

            if (trackInfoList.size() > 10) {
                queueMessage.append("\n**And ")
                        .append(trackInfoList.size() - 10)
                        .append(" more tracks...**");
            }
        }

        channel.sendMessage(queueMessage.toString()).queue();
    }

    private String formatDuration(long duration) {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%02d:%02d", minutes, seconds % 60);
        }
    }
}