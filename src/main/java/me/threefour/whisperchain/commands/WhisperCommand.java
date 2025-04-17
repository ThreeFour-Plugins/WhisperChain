package me.threefour.whisperchain.commands;

import me.threefour.whisperchain.WhisperChain;
import me.threefour.whisperchain.listeners.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WhisperCommand implements CommandExecutor, TabCompleter {

    private final WhisperChain plugin;
    
    public WhisperCommand(WhisperChain plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Show help
            showHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                showHelp(player);
                break;
            case "mode":
                toggleWhisperMode(player);
                break;
            case "history":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /whisper history <player>");
                    return true;
                }
                showChatHistory(player, args[1]);
                break;
            case "reply":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /whisper reply <message>");
                    return true;
                }
                replyToLastMessage(player, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                break;
            case "group":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /whisper group <player1,player2,...> <message>");
                    return true;
                }
                sendGroupWhisper(player, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                break;
            default:
                // Process as a normal whisper command
                processWhisperCommand(player, args);
        }
        
        return true;
    }
    
    /**
     * Process a whisper command with recipient(s) and message
     */
    private void processWhisperCommand(Player sender, String[] args) {
        List<Player> recipients = new ArrayList<>();
        String message = null;
        
        // Look for message delimiter
        int messageIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-m")) {
                messageIndex = i + 1;
                break;
            }
        }
        
        // If no explicit delimiter, treat the first arg as recipient and rest as message
        if (messageIndex == -1) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /whisper <player> <message>");
                return;
            }
            
            // First argument is recipient(s)
            String recipientArg = args[0];
            parseRecipients(recipientArg, recipients);
            
            // Rest is message
            message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        } else {
            // Parse all recipients before the -m flag
            for (int i = 0; i < messageIndex - 1; i++) {
                parseRecipients(args[i], recipients);
            }
            
            // Everything after -m is the message
            if (messageIndex < args.length) {
                message = String.join(" ", Arrays.copyOfRange(args, messageIndex, args.length));
            }
        }
        
        // Validate we have both recipients and a message
        if (recipients.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No valid recipients found.");
            return;
        }
        
        if (message == null || message.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Please provide a message to send.");
            return;
        }
        
        // Send the whisper
        plugin.getChatManager().sendWhisper(sender, recipients, message);
        
        // Set reply target if only one recipient
        if (recipients.size() == 1) {
            plugin.getChatListener().setReplyTarget(sender.getUniqueId(), recipients.get(0).getUniqueId());
        }
    }
    
    /**
     * Parse recipients from a comma-separated string or single name
     */
    private void parseRecipients(String input, List<Player> recipients) {
        if (input.contains(",")) {
            // Multiple players separated by commas
            String[] playerNames = input.split(",");
            for (String name : playerNames) {
                Player recipient = Bukkit.getPlayer(name.trim());
                if (recipient != null && !recipients.contains(recipient)) {
                    recipients.add(recipient);
                }
            }
        } else {
            // Single player name
            Player recipient = Bukkit.getPlayer(input.trim());
            if (recipient != null && !recipients.contains(recipient)) {
                recipients.add(recipient);
            }
        }
    }
    
    /**
     * Send a whisper to a group of players
     */
    private void sendGroupWhisper(Player sender, String recipientList, String message) {
        List<Player> recipients = new ArrayList<>();
        parseRecipients(recipientList, recipients);
        
        if (recipients.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No valid recipients found.");
            return;
        }
        
        // Send the whisper
        plugin.getChatManager().sendWhisper(sender, recipients, message);
        
        // Don't set reply target for group messages as it would be ambiguous
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Subcommands
            completions.add("help");
            completions.add("mode");
            completions.add("history");
            completions.add("reply");
            completions.add("group");
            
            // Player names
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("history") || args[0].equalsIgnoreCase("group")) {
                // Player names for history/group
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            } else if (!args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("mode")) {
                // Add -m as a suggestion for the message flag
                completions.add("-m");
            }
        }
        
        return completions.stream()
            .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
            .collect(Collectors.toList());
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "====== " + ChatColor.LIGHT_PURPLE + "WhisperChain Help" + ChatColor.DARK_PURPLE + " ======");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper <player> <message>" + ChatColor.GRAY + " - Send a private message");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper <player1,player2> <message>" + ChatColor.GRAY + " - Send to multiple players");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper group <player1,player2> <message>" + ChatColor.GRAY + " - Send to a group");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper mode" + ChatColor.GRAY + " - Toggle whisper mode (all messages go to last recipient)");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper reply <message>" + ChatColor.GRAY + " - Reply to the last person who messaged you");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper history <player>" + ChatColor.GRAY + " - View chat history with a player");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "/whisper help" + ChatColor.GRAY + " - Show this help message");
    }
    
    private void toggleWhisperMode(Player player) {
        ChatListener listener = plugin.getChatListener();
        
        if (listener != null) {
            boolean currentMode = listener.isInWhisperMode(player.getUniqueId());
            listener.setWhisperMode(player.getUniqueId(), !currentMode);
            
            if (!currentMode) {
                player.sendMessage(ChatColor.GREEN + "Whisper mode enabled. All messages will be sent privately to your last recipient.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Whisper mode disabled. Messages will be sent to public chat.");
            }
        }
    }
    
    private void showChatHistory(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return;
        }
        
        List<String> history = plugin.getChatManager().getChatHistory(player.getUniqueId(), target.getUniqueId());
        
        if (history.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No chat history with " + target.getName());
            return;
        }
        
        player.sendMessage(ChatColor.DARK_PURPLE + "====== " + ChatColor.LIGHT_PURPLE + "Chat with " + target.getName() + ChatColor.DARK_PURPLE + " ======");
        
        // Show last 10 messages
        int startIndex = Math.max(0, history.size() - 10);
        for (int i = startIndex; i < history.size(); i++) {
            player.sendMessage(ChatColor.GRAY + history.get(i));
        }
    }
    
    private void replyToLastMessage(Player player, String message) {
        ChatListener listener = plugin.getChatListener();
        
        if (listener == null) {
            player.sendMessage(ChatColor.RED + "Error: Chat listener not found.");
            return;
        }
        
        UUID targetUUID = listener.getReplyTarget(player.getUniqueId());
        
        if (targetUUID == null) {
            player.sendMessage(ChatColor.RED + "No one to reply to.");
            return;
        }
        
        Player target = Bukkit.getPlayer(targetUUID);
        
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "The player you were talking to is no longer online.");
            return;
        }
        
        plugin.getChatManager().sendWhisper(player, List.of(target), message);
    }
} 