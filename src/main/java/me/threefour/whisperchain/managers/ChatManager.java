package me.threefour.whisperchain.managers;

import me.threefour.whisperchain.WhisperChain;
import me.threefour.whisperchain.utils.Encryption;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.crypto.SecretKey;
import java.util.*;

public class ChatManager {

    private final WhisperChain plugin;
    private final Map<UUID, List<UUID>> activeChatSessions = new HashMap<>();
    private final Map<UUID, Map<UUID, List<String>>> chatHistory = new HashMap<>();
    
    public ChatManager(WhisperChain plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Send a whisper message to a player or multiple players
     * @param sender The player sending the message
     * @param recipients The players receiving the message
     * @param message The message to send
     */
    public void sendWhisper(Player sender, List<Player> recipients, String message) {
        Encryption encryption = plugin.getEncryption();
        UUID senderUUID = sender.getUniqueId();
        
        // Add all recipients to active chat session if not already present
        if (!activeChatSessions.containsKey(senderUUID)) {
            activeChatSessions.put(senderUUID, new ArrayList<>());
        }
        
        for (Player recipient : recipients) {
            UUID recipientUUID = recipient.getUniqueId();
            
            // Add recipient to sender's active sessions
            if (!activeChatSessions.get(senderUUID).contains(recipientUUID)) {
                activeChatSessions.get(senderUUID).add(recipientUUID);
            }
            
            // Add sender to recipient's active sessions
            if (!activeChatSessions.containsKey(recipientUUID)) {
                activeChatSessions.put(recipientUUID, new ArrayList<>());
            }
            if (!activeChatSessions.get(recipientUUID).contains(senderUUID)) {
                activeChatSessions.get(recipientUUID).add(senderUUID);
            }
            
            // Encrypt the message with recipient's key
            SecretKey recipientKey = encryption.getPlayerKey(recipientUUID);
            String encryptedMessage = encryption.encrypt(message, recipientKey);
            
            // Send the encrypted message
            recipient.sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Whisper" + 
                ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + "From " + ChatColor.LIGHT_PURPLE + 
                sender.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
            
            // Store the message in chat history
            storeMessage(senderUUID, recipientUUID, sender.getName() + ": " + message);
        }
        
        // Confirm to the sender
        String recipientNames = recipients.size() == 1 ? 
            recipients.get(0).getName() : 
            recipients.stream().map(Player::getName).reduce((a, b) -> a + ", " + b).orElse("");
        
        sender.sendMessage(ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Whisper" + 
            ChatColor.DARK_PURPLE + "] " + ChatColor.GRAY + "To " + ChatColor.LIGHT_PURPLE + 
            recipientNames + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
    }
    
    /**
     * Store a message in the chat history
     * @param sender The UUID of the sender
     * @param recipient The UUID of the recipient
     * @param message The message to store
     */
    private void storeMessage(UUID sender, UUID recipient, String message) {
        // Initialize chat history for sender if not exists
        if (!chatHistory.containsKey(sender)) {
            chatHistory.put(sender, new HashMap<>());
        }
        
        // Initialize chat history for recipient in sender's history if not exists
        if (!chatHistory.get(sender).containsKey(recipient)) {
            chatHistory.get(sender).put(recipient, new ArrayList<>());
        }
        
        // Store the message
        chatHistory.get(sender).get(recipient).add(message);
        
        // Do the same for recipient
        if (!chatHistory.containsKey(recipient)) {
            chatHistory.put(recipient, new HashMap<>());
        }
        
        if (!chatHistory.get(recipient).containsKey(sender)) {
            chatHistory.get(recipient).put(sender, new ArrayList<>());
        }
        
        chatHistory.get(recipient).get(sender).add(message);
    }
    
    /**
     * Get the chat history between two players
     * @param player1 The first player
     * @param player2 The second player
     * @return The list of messages exchanged between the players
     */
    public List<String> getChatHistory(UUID player1, UUID player2) {
        if (!chatHistory.containsKey(player1) || !chatHistory.get(player1).containsKey(player2)) {
            return new ArrayList<>();
        }
        return chatHistory.get(player1).get(player2);
    }
    
    /**
     * Get all active chat sessions for a player
     * @param playerUUID The UUID of the player
     * @return A list of player UUIDs representing active chat sessions
     */
    public List<UUID> getActiveSessions(UUID playerUUID) {
        return activeChatSessions.getOrDefault(playerUUID, new ArrayList<>());
    }
    
    /**
     * Get a list of active players in chat sessions
     * @param playerUUID The UUID of the player
     * @return A list of online players who are in active chat sessions with the player
     */
    public List<Player> getActiveSessionPlayers(UUID playerUUID) {
        List<Player> players = new ArrayList<>();
        List<UUID> sessions = getActiveSessions(playerUUID);
        
        for (UUID uuid : sessions) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }
        
        return players;
    }
} 