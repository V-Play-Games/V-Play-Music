package net.vplaygames.TheChaosTrilogy.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.vplaygames.TheChaosTrilogy.commands.AbstractBotCommand;
import net.vplaygames.TheChaosTrilogy.core.Bot;
import net.vplaygames.TheChaosTrilogy.core.CommandReceivedEvent;
import net.vplaygames.TheChaosTrilogy.core.Util;
import net.vplaygames.TheChaosTrilogy.player.PlayerManager;

public class LeaveCommand extends AbstractBotCommand {
    public LeaveCommand() {
        super("leave", "Leaves the current VC", "disconnect", "dc");
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
        if (e.getArgs().size() == 2 && e.getArg(1).equals("all") && e.getAuthor().getIdLong() == Bot.BOT_OWNER) {
            e.getJDA()
                .getGuilds()
                .stream()
                .map(Guild::getAudioManager)
                .forEach(AudioManager::closeAudioConnection);
        }
        VoiceChannel vc = e.getSelfMember().getVoiceState().getChannel();
        if (vc == null) {
            e.send("Leave? Leave what?").queue();
            return;
        }
        if (Util.getListeningMembers(vc).size() != 0) {
            e.send("No. I am vibin' with my people in 'ere, I can't leave 'em alone like that :(").queue();
            return;
        }
        PlayerManager.getInstance().getMusicManager(e.getGuild()).disconnect();
        e.send("Left " + vc.getAsMention()).queue();
    }
}
