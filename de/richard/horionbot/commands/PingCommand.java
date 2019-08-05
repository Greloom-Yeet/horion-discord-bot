package de.richard.horionbot.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PingCommand extends Command
{
    @Override
    public void onCommand(MessageReceivedEvent e, String[] args)
    {
        Long ping = e.getJDA().getPing();
        Color color = Color.RED;
        if(ping < 100) color = Color.ORANGE;
        if(ping < 20) color = Color.GREEN;
        e.getTextChannel().sendMessage(new EmbedBuilder().setDescription("**Ping:** " + ping + " ms").setColor(color).build()).queue();
    }

    @Override
    public java.util.List<String> getAliases()
    {
        return Arrays.asList(".ping");
    }

    @Override
    public String getDescription()
    {
        return "Display the bots ping!";
    }

    @Override
    public String getName()
    {
        return "Ping-Command";
    }

    @Override
    public String getIcon()
    {
        return "https://files.catbox.moe/tau093.png";
    }

    @Override
    public List<String> getUsageInstructions()
    {
        return Arrays.asList(".ping - Display the bots ping!");
    }
}