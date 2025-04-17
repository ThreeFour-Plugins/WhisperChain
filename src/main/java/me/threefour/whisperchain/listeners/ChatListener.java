package me.threefour.whisperchain.listeners;

import me.threefour.whisperchain.WhisperChain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final WhisperChain plugin;
    private final Map<UUID, Boolean> inWhisperMode = new HashMap<>();
    private final Map<UUID, UUID> replyTarget = new HashMap<>();
    private final Pattern mentionPattern = Pattern.compile("@([\\w]+)");
    private final Pattern privateMessagePattern = Pattern.compile("@([\\w,]+)\\s+(.+)");
    
    public ChatListener(WhisperChain plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Generate encryption key for the player if doesn't exist
        plugin.getEncryption().getPlayerKey(player.getUniqueId());
        
        // Set default state
        inWhisperMode.put(player.getUniqueId(), false);
        
        // Send welcome message with instructions
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome to WhisperChain!" + 
                        ChatColor.GRAY + " To send an encrypted message, use: " + 
                        ChatColor.WHITE + "@playername Your secret message");
        player.sendMessage(ChatColor.GRAY + "You can message multiple players with: " + 
                        ChatColor.WHITE + "@player1,player2,player3 Your secret message");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        
        // Clean up temporary chat state
        inWhisperMode.remove(playerUUID);
        replyTarget.remove(playerUUID);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String message = event.getMessage();
        
        // Check if player is in whisper mode (everything goes to last whispered player)
        if (inWhisperMode.getOrDefault(playerUUID, false)) {
            event.setCancelled(true);
            
            // Get the reply target
            UUID targetUUID = replyTarget.get(playerUUID);
            if (targetUUID != null) {
                Player targetPlayer = plugin.getServer().getPlayer(targetUUID);
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    // Send the whisper
                    plugin.getChatManager().sendWhisper(player, Collections.singletonList(targetPlayer), message);
                } else {
                    player.sendMessage(ChatColor.RED + "Your private chat partner is offline. Whisper mode disabled.");
                    setWhisperMode(playerUUID, false);
                }
            } else {
                player.sendMessage(ChatColor.RED + "No private chat partner set. Whisper mode disabled.");
                setWhisperMode(playerUUID, false);
            }
            return;
        }
        
        // Check for @player message pattern
        Matcher matcher = privateMessagePattern.matcher(message);
        if (matcher.matches()) {
            // This is a private message
            event.setCancelled(true);
            
            // Get recipients from the @ mention
            String recipientNames = matcher.group(1);
            String privateMessage = matcher.group(2);
            
            List<Player> recipients = new ArrayList<>();
            if (recipientNames.contains(",")) {
                // Multiple recipients
                String[] names = recipientNames.split(",");
                for (String name : names) {
                    Player recipient = Bukkit.getPlayer(name.trim());
                    if (recipient != null && recipient.isOnline() && !recipients.contains(recipient)) {
                        recipients.add(recipient);
                    }
                }
            } else {
                // Single recipient
                Player recipient = Bukkit.getPlayer(recipientNames.trim());
                if (recipient != null && recipient.isOnline()) {
                    recipients.add(recipient);
                }
            }
            
            if (recipients.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No valid recipients found.");
                return;
            }
            
            // Send encrypted message to all recipients
            plugin.getChatManager().sendWhisper(player, recipients, privateMessage);
            
            // Set reply target if only one recipient
            if (recipients.size() == 1) {
                setReplyTarget(playerUUID, recipients.get(0).getUniqueId());
            }
            
            return;
        }
        
        // Check for any @mentions in regular chat
        matcher = mentionPattern.matcher(message);
        if (matcher.find()) {
            // Let normal chat go through, but notify the player they can send private messages
            player.sendMessage(ChatColor.GRAY + "Tip: To send a private encrypted message, use: " + 
                            ChatColor.WHITE + "@playername Your secret message");
        }
    }
    
    /**
     * Toggle chat mode between global and whisper for a specific player
     * @param targetName The name of the player to start whispering with
     * @param player The player toggling the mode
     */
    public void toggleChatMode(String targetName, Player player) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or offline: " + targetName);
            return;
        }
        
        UUID playerUUID = player.getUniqueId();
        boolean currentMode = inWhisperMode.getOrDefault(playerUUID, false);
        
        if (!currentMode) {
            // Enable whisper mode and set target
            setWhisperMode(playerUUID, true);
            setReplyTarget(playerUUID, target.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now in private chat mode with " + 
                            ChatColor.YELLOW + target.getName() + ChatColor.GREEN + 
                            ". All your messages will be encrypted and sent only to them.");
            player.sendMessage(ChatColor.GRAY + "Type " + ChatColor.WHITE + ".exit" + 
                            ChatColor.GRAY + " to return to global chat.");
            
            // Notify the target player
            target.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + 
                            " has started a private encrypted chat session with you.");
        } else {
            // Disable whisper mode
            setWhisperMode(playerUUID, false);
            player.sendMessage(ChatColor.GREEN + "Exited private chat mode. Your messages will now be sent to global chat.");
        }
    }
    
    /**
     * Set whether a player is in whisper mode (their chat goes to the last whispered player)
     * @param playerUUID The UUID of the player
     * @param inMode Whether the player should be in whisper mode
     */
    public void setWhisperMode(UUID playerUUID, boolean inMode) {
        inWhisperMode.put(playerUUID, inMode);
    }
    
    /**
     * Set the reply target for a player
     * @param playerUUID The UUID of the player
     * @param targetUUID The UUID of the target
     */
    public void setReplyTarget(UUID playerUUID, UUID targetUUID) {
        replyTarget.put(playerUUID, targetUUID);
    }
    
    /**
     * Check if a player is in whisper mode
     * @param playerUUID The UUID of the player
     * @return True if the player is in whisper mode, false otherwise
     */
    public boolean isInWhisperMode(UUID playerUUID) {
        return inWhisperMode.getOrDefault(playerUUID, false);
    }
    
    /**
     * Get the reply target for a player
     * @param playerUUID The UUID of the player
     * @return The UUID of the reply target, or null if none
     */
    public UUID getReplyTarget(UUID playerUUID) {
        return replyTarget.get(playerUUID);
    }
} 