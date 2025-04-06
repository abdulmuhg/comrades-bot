package main.java.com.abdulmuhg.bot;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

public class TrackInfo {
    private final AudioTrack track;
    private final Member requester;

    public TrackInfo(AudioTrack track, Member requester) {
        this.track = track;
        this.requester = requester;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Member getRequester() {
        return requester;
    }
}