package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.GuildAudioManager;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class VolumeCommand extends AbstractBotCommand {
    public VolumeCommand() {
        super("volume", "Shows info on currently playing track if any");
        addOption(OptionType.INTEGER, "volume", "An integer in the range of 1 - 1000", true);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, Util.toInt(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e, (int) slash.getOption("volume").getAsLong());
    }

    public void execute(CommandReceivedEvent e, int volume) {
        GuildAudioManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        if (manager.getPlayingTrack() == null) {
            e.send("There's nothing playin' in 'ere. Party's over. Let's have an after party whaddaya think?").queue();
            return;
        }
//        manager.setVolume(volume);
        e.send("Operation Not Supported.").queue();
    }
}
