# WhisperChain Developer API

WhisperChain provides an API that allows other plugins to interact with the secure messaging system. This guide explains how to use the WhisperChain API in your own plugins.

## Table of Contents

- [Getting Started](#getting-started)
- [API Methods](#api-methods)
- [Event Listening](#event-listening)
- [Code Examples](#code-examples)
- [Best Practices](#best-practices)

## Getting Started

### Adding WhisperChain as a Dependency

First, add WhisperChain as a dependency in your plugin.yml:

```yaml
depend: [WhisperChain]
```

Or as a soft dependency if your plugin can function without WhisperChain:

```yaml
softdepend: [WhisperChain]
```

### Maven Dependency

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Amineos</groupId>
        <artifactId>WhisperChain</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle Dependency

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Amineos:WhisperChain:1.0.0'
}
```

### Accessing the API

```java
import me.threefour.whisperchain.api.WhisperChainAPI;

public class YourPlugin extends JavaPlugin {
    private WhisperChainAPI whisperChainAPI;
    
    @Override
    public void onEnable() {
        // Check if WhisperChain is installed
        if (Bukkit.getPluginManager().getPlugin("WhisperChain") != null) {
            whisperChainAPI = WhisperChainAPI.getInstance();
            getLogger().info("Successfully hooked into WhisperChain!");
        } else {
            getLogger().warning("WhisperChain not found! Some features will be disabled.");
        }
    }
}
```

## API Methods

### Sending Encrypted Messages

```java
/**
 * Sends an encrypted message from one player to another
 * @param sender The player sending the message
 * @param recipient The player receiving the message
 * @param message The message content
 * @return true if the message was sent successfully
 */
boolean sendEncryptedMessage(Player sender, Player recipient, String message);

/**
 * Sends an encrypted message to multiple recipients
 * @param sender The player sending the message
 * @param recipients List of players to receive the message
 * @param message The message content
 * @return true if the message was sent successfully
 */
boolean sendGroupMessage(Player sender, List<Player> recipients, String message);
```

### Managing Chat Sessions

```java
/**
 * Enables private chat mode for a player
 * @param player The player to enable private chat for
 * @param target The target player who will receive all messages
 * @return true if successful
 */
boolean enablePrivateChatMode(Player player, Player target);

/**
 * Disables private chat mode for a player
 * @param player The player to disable private chat for
 * @return true if successful
 */
boolean disablePrivateChatMode(Player player);

/**
 * Checks if a player is in private chat mode
 * @param player The player to check
 * @return true if the player is in private chat mode
 */
boolean isInPrivateChatMode(Player player);
```

### Encryption Utilities

```java
/**
 * Encrypts a string using a player's encryption key
 * @param text The text to encrypt
 * @param player The player whose key to use
 * @return The encrypted text as a Base64 string
 */
String encrypt(String text, Player player);

/**
 * Decrypts a string using a player's encryption key
 * @param encryptedText The encrypted text (Base64 encoded)
 * @param player The player whose key to use
 * @return The decrypted text
 */
String decrypt(String encryptedText, Player player);
```

### Chat History

```java
/**
 * Gets the chat history between two players
 * @param player1 The first player
 * @param player2 The second player
 * @return List of message objects containing sender, timestamp, and content
 */
List<WhisperMessage> getChatHistory(Player player1, Player player2);

/**
 * Clears the chat history between two players
 * @param player1 The first player
 * @param player2 The second player
 * @return true if successful
 */
boolean clearChatHistory(Player player1, Player player2);
```

## Event Listening

WhisperChain provides several events you can listen to in your plugin:

### EncryptedMessageSentEvent

Fired when an encrypted message is sent.

```java
@EventHandler
public void onEncryptedMessageSent(EncryptedMessageSentEvent event) {
    Player sender = event.getSender();
    Player recipient = event.getRecipient();
    String message = event.getMessage();
    
    // You can also cancel the event
    if (message.contains("forbidden word")) {
        event.setCancelled(true);
        sender.sendMessage("Your message contained forbidden content.");
    }
}
```

### PrivateChatModeEvent

Fired when a player enters or exits private chat mode.

```java
@EventHandler
public void onPrivateChatMode(PrivateChatModeEvent event) {
    Player player = event.getPlayer();
    boolean enabled = event.isEnabled();
    Player target = event.getTarget(); // May be null if mode is being disabled
    
    if (enabled) {
        getLogger().info(player.getName() + " entered private chat with " + target.getName());
    } else {
        getLogger().info(player.getName() + " exited private chat mode");
    }
}
```

## Code Examples

### Example: Custom Command That Sends Encrypted Messages

```java
@Override
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("This command can only be used by players");
        return true;
    }
    
    if (args.length < 2) {
        sender.sendMessage("Usage: /secretmsg <player> <message>");
        return true;
    }
    
    Player player = (Player) sender;
    Player target = Bukkit.getPlayer(args[0]);
    
    if (target == null) {
        player.sendMessage("Player not found");
        return true;
    }
    
    // Build the message from remaining args
    StringBuilder message = new StringBuilder();
    for (int i = 1; i < args.length; i++) {
        message.append(args[i]).append(" ");
    }
    
    // Send encrypted message using WhisperChain API
    boolean success = whisperChainAPI.sendEncryptedMessage(player, target, message.toString().trim());
    
    if (success) {
        player.sendMessage("Secret message sent!");
    } else {
        player.sendMessage("Failed to send secret message");
    }
    
    return true;
}
```

### Example: Listening for Encrypted Messages

```java
@EventHandler
public void onEncryptedMessage(EncryptedMessageSentEvent event) {
    // Log encrypted messages for specific players (e.g., for moderation)
    if (event.getSender().hasPermission("yourplugin.monitored")) {
        getLogger().info("Monitored player " + event.getSender().getName() + 
                         " sent a private message to " + event.getRecipient().getName());
    }
    
    // Add rewards for private communication
    if (random.nextDouble() < 0.05) { // 5% chance
        event.getSender().sendMessage("You received a bonus for secure communication!");
        // Give some reward...
    }
}
```

## Best Practices

1. **Always check if WhisperChain is available** before attempting to use the API.
2. **Do not store decrypted messages** permanently as this defeats the purpose of end-to-end encryption.
3. **Handle exceptions** that may occur when calling encryption/decryption methods.
4. **Respect user privacy** - only intercept or access encrypted messages when absolutely necessary.
5. **Test thoroughly** with different versions of WhisperChain.

## Need Help?

If you encounter any issues or have questions about the API, please reach out:

- [GitHub Issues](https://github.com/Amineos/WhisperChain/issues)
- [Discord Community](https://discord.gg/yourdiscord)

---

This API documentation is for WhisperChain v1.0.0 and may change in future versions. 