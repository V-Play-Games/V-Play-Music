package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.GuildAudioManager;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class LoopQueueCommand extends AbstractBotCommand {
    public LoopQueueCommand() {
        super("loopqueue", "Shows info on currently playing track if any", "lq");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e);
    }

    @Override
    public void onSlashCommandRun(SlashCommandEvent slash, CommandReceivedEvent e) {
        execute(e);
    }

    public void execute(CommandReceivedEvent e) {
        if (!Util.canJoinVC(e)) {
            return;
        }
        GuildAudioManager manager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        manager.toggleLoopQueue();
        e.send("Set queue loop to " + manager.isLoopQueue()).queue();
    }
}
