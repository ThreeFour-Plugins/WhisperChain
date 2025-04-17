package me.threefour.whisperchain;

import me.threefour.whisperchain.listeners.ChatListener;
import me.threefour.whisperchain.managers.ChatManager;
import me.threefour.whisperchain.utils.Encryption;
import org.bukkit.plugin.java.JavaPlugin;

public final class WhisperChain extends JavaPlugin {

    private ChatManager chatManager;
    private Encryption encryption;
    private ChatListener chatListener;

    @Override
    public void onEnable() {
        // Initialize managers
        this.encryption = new Encryption();
        this.chatManager = new ChatManager(this);
        
        // Initialize and register listeners
        this.chatListener = new ChatListener(this);
        getServer().getPluginManager().registerEvents(this.chatListener, this);
        
        getLogger().info("WhisperChain has been enabled! Use @player to send an encrypted message.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WhisperChain has been disabled!");
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public Encryption getEncryption() {
        return encryption;
    }
    
    public ChatListener getChatListener() {
        return chatListener;
    }
}
