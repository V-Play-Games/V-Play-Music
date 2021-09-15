package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;

public class JoinCommand extends AbstractBotCommand {
    public JoinCommand() {
        super("join", "Joins the audio channel you are currently connected in");
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
        e.send("Connected to " + e.getMember().getVoiceState().getChannel().getAsMention()).queue();
    }
}
