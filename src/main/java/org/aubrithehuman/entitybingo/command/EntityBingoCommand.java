package org.aubrithehuman.entitybingo.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class EntityBingoCommand implements CommandExecutor, TabCompleter {

    private final List<String> options = List.of(new String[] {
            "scoreboard"
    });

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("scoreboard")) {

            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1) {
            return options;
        }
        return null;
    }
}
