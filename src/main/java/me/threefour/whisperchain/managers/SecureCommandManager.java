package me.threefour.whisperchain.managers;

import me.threefour.whisperchain.WhisperChain;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Manages secure commands that don't get logged in the console
 */
public class SecureCommandManager implements Listener {

    private final WhisperChain plugin;
    private final Map<String, SecureCommand> commandMap = new HashMap<>();
    private static final List<String> SENSITIVE_COMMANDS = Arrays.asList("whisper", "w", "msg", "tell");
    private final Logger originalLogger;
    private final Set<String> commandsToFilter = new HashSet<>();
    
    public SecureCommandManager(WhisperChain plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        // Add all commands to filter
        commandsToFilter.addAll(SENSITIVE_COMMANDS);
        
        // Set up log filtering
        this.originalLogger = plugin.getServer().getLogger();
        setupLogFilter();
        
        // Also filter other plugins' loggers to ensure complete privacy
        for (Plugin otherPlugin : Bukkit.getPluginManager().getPlugins()) {
            if (otherPlugin.getLogger() != null && otherPlugin != plugin) {
                otherPlugin.getLogger().setFilter(new CommandFilter());
            }
        }
    }
    
    /**
     * Sets up a filter for server logs to hide sensitive commands
     */
    private void setupLogFilter() {
        // Get the existing filter if any
        Filter existingFilter = originalLogger.getFilter();
        
        // Create a new command filter
        CommandFilter commandFilter = new CommandFilter();
        
        // If there's already a filter, chain them
        if (existingFilter != null) {
            originalLogger.setFilter(record -> {
                if (!commandFilter.isLoggable(record)) {
                    return false;
                }
                return existingFilter.isLoggable(record);
            });
        } else {
            originalLogger.setFilter(commandFilter);
        }
    }
    
    /**
     * Filter that blocks logging of sensitive commands
     */
    private class CommandFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord record) {
            String message = record.getMessage();
            if (message == null) return true;
            
            // Check for command patterns in logs
            String lowercaseMsg = message.toLowerCase();
            
            // Check for patterns like "player issued server command: /msg ..."
            if (lowercaseMsg.contains("issued server command:")) {
                for (String cmd : commandsToFilter) {
                    if (lowercaseMsg.contains("/" + cmd + " ")) {
                        return false; // Filter out this log
                    }
                }
            }
            
            return true;
        }
    }
    
    /**
     * Register a secure command that won't be logged in the console
     */
    public void registerSecureCommand(String name, String description, String usage, 
                               List<String> aliases, CommandExecutor executor, 
                               TabCompleter tabCompleter) {
        // Create the secure command
        SecureCommand command = new SecureCommand(name, description, usage, aliases, executor, tabCompleter);
        
        // Add to our map
        commandMap.put(name.toLowerCase(), command);
        
        // Also add all aliases
        for (String alias : aliases) {
            commandMap.put(alias.toLowerCase(), command);
            commandsToFilter.add(alias.toLowerCase());
        }
        
        // Register with bukkit's command map so tab completion works
        createBukkitCommand(name, description, usage, aliases, executor, tabCompleter);
    }
    
    /**
     * Creates a Bukkit command to handle tab completion
     */
    private void createBukkitCommand(String name, String description, String usage, 
                               List<String> aliases, CommandExecutor executor, 
                               TabCompleter tabCompleter) {
        Command command = new Command(name) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                // Don't actually execute via this path - let our event handler do it
                return true;
            }
            
            @Override
            public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
                if (tabCompleter != null) {
                    return tabCompleter.onTabComplete(sender, this, alias, args);
                }
                return super.tabComplete(sender, alias, args);
            }
        };
        
        command.setDescription(description);
        command.setUsage(usage);
        command.setAliases(aliases);
        
        plugin.getServer().getCommandMap().register(plugin.getName(), command);
    }
    
    /**
     * Check if a command is registered as secure
     */
    public boolean isSecureCommand(String name) {
        return commandMap.containsKey(name.toLowerCase());
    }
    
    /**
     * Get a secure command by name
     */
    public SecureCommand getCommand(String name) {
        return commandMap.get(name.toLowerCase());
    }
    
    /**
     * Intercept player commands before they're logged
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        
        String message = event.getMessage();
        if (message.length() <= 1) {
            return;
        }
        
        // Extract command name without splitting arguments yet
        String command = message.substring(1).split(" ", 2)[0].toLowerCase();
        
        if (isSecureCommand(command)) {
            // Cancel the original command event to prevent logging
            event.setCancelled(true);
            
            // Execute our secure command with a delay to avoid race conditions
            SecureCommand secureCommand = getCommand(command);
            Player player = event.getPlayer();
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Get the full original command without the leading slash
                    String fullCommand = message.substring(1);
                    
                    // Parse the arguments correctly, preserving spaces properly
                    String[] parts = fullCommand.split(" ", 2);
                    String[] args = parts.length > 1 ? parseArguments(parts[1]) : new String[0];
                    
                    // Execute the command
                    secureCommand.execute(player, command, args);
                }
            }.runTask(plugin);
        }
    }
    
    /**
     * Parse command arguments properly, respecting quotes
     */
    private String[] parseArguments(String argLine) {
        List<String> args = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < argLine.length(); i++) {
            char c = argLine.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString());
                    currentArg = new StringBuilder();
                }
            } else {
                currentArg.append(c);
            }
        }
        
        if (currentArg.length() > 0) {
            args.add(currentArg.toString());
        }
        
        return args.toArray(new String[0]);
    }
    
    /**
     * Intercept server commands to allow console to use secure commands
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(ServerCommandEvent event) {
        String message = event.getCommand();
        if (message.length() == 0) {
            return;
        }
        
        // Extract command name without splitting arguments yet
        String command = message.split(" ", 2)[0].toLowerCase();
        
        if (isSecureCommand(command)) {
            // Cancel the original command event to prevent logging
            event.setCancelled(true);
            
            // Execute our secure command with a delay to avoid race conditions
            SecureCommand secureCommand = getCommand(command);
            CommandSender sender = event.getSender();
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Parse the arguments correctly
                    String[] parts = message.split(" ", 2);
                    String[] args = parts.length > 1 ? parseArguments(parts[1]) : new String[0];
                    
                    // Execute the command
                    secureCommand.execute(sender, command, args);
                    
                    // Send a generic message to console without revealing the command content
                    plugin.getLogger().info("Console executed a secure command: " + command);
                }
            }.runTask(plugin);
        }
    }
    
    /**
     * Class representing a secure command
     */
    public static class SecureCommand {
        private final String name;
        private final String description;
        private final String usage;
        private final List<String> aliases;
        private final CommandExecutor executor;
        private final TabCompleter tabCompleter;
        
        public SecureCommand(String name, String description, String usage, 
                        List<String> aliases, CommandExecutor executor, 
                        TabCompleter tabCompleter) {
            this.name = name;
            this.description = description;
            this.usage = usage;
            this.aliases = aliases;
            this.executor = executor;
            this.tabCompleter = tabCompleter;
        }
        
        public boolean execute(CommandSender sender, String label, String[] args) {
            // Create a command that can be passed to the executor
            Command command = new Command(name) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return true;
                }
            };
            command.setDescription(description);
            command.setUsage(usage);
            command.setAliases(aliases);
            
            // Execute the command
            return executor.onCommand(sender, command, label, args);
        }
        
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            if (tabCompleter == null) {
                return Collections.emptyList();
            }
            
            // Create a command for tab completion
            Command command = new Command(name) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return true;
                }
            };
            command.setDescription(description);
            command.setUsage(usage);
            command.setAliases(aliases);
            
            return tabCompleter.onTabComplete(sender, command, alias, args);
        }
    }
} 