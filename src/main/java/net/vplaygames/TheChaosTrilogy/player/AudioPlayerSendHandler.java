package net.vplaygames.TheChaosTrilogy.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {
    AudioPlayer player;
    ByteBuffer buffer;
    MutableAudioFrame frame;

    public AudioPlayerSendHandler(AudioPlayer player) {
        this.player = player;
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        return this.player.provide(frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return (ByteBuffer) this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
