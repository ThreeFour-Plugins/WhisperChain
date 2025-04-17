# WhisperChain Configuration Guide

WhisperChain works out of the box with no configuration, but you can customize various aspects of the plugin to better suit your server's needs.

## Table of Contents

- [Configuration File](#configuration-file)
- [Message Styling](#message-styling)
- [Encryption Settings](#encryption-settings)
- [Chat History](#chat-history)
- [Advanced Options](#advanced-options)

## Configuration File

After you first run the plugin, a configuration file will be generated at `plugins/WhisperChain/config.yml`. You can edit this file and then use `/whisperchain reload` to apply your changes.

## Message Styling

You can customize how messages appear to both senders and recipients:

```yaml
messages:
  # Format for outgoing messages (what you see when sending)
  outgoing-format: "&5[&dWhisper&5] &7To &d{recipient}&7: &f{message}"
  
  # Format for incoming messages (what recipients see)
  incoming-format: "&5[&dWhisper&5] &7From &d{sender}&7: &f{message}"
  
  # Format for group messages
  group-format: "&5[&dWhisper&5] &7From &d{sender}&7 to &d{recipients}&7: &f{message}"
  
  # Colors
  primary-color: "&d"     # Light purple
  secondary-color: "&5"   # Dark purple
  text-color: "&f"        # White
  accent-color: "&7"      # Gray
```

## Encryption Settings

Adjust the security level and encryption behavior:

```yaml
encryption:
  # Key size in bits (128, 192, or 256)
  key-size: 256
  
  # How long to keep encryption keys in memory after a player logs out (in minutes)
  # Set to -1 to keep forever
  key-expiry: 60
  
  # Whether to encrypt chat history stored on disk
  encrypt-storage: true
```

## Chat History

Control how chat history is stored and managed:

```yaml
chat-history:
  # Whether to save chat history
  enabled: true
  
  # Maximum number of messages to store per conversation
  max-messages: 100
  
  # How long to keep chat history (in days)
  # Set to -1 to keep forever
  retention-days: 30
  
  # Whether players can clear their chat history
  allow-clearing: true
```

## Advanced Options

Fine-tune the plugin's behavior:

```yaml
advanced:
  # Debug mode (adds extra logging)
  debug: false
  
  # Whether to use the @ symbol as the prefix for messages
  use-at-symbol: true
  
  # Alternative prefix to use if not using @ symbol
  custom-prefix: "!"
  
  # Command to exit private chat mode
  exit-command: ".exit"
  
  # Whether to allow messaging offline players (messages delivered when they log in)
  message-offline-players: false
  
  # Whether to use metrics/analytics
  use-metrics: true
  
  # Whether to check for updates
  check-updates: true
```

## Example Configuration

Here's a complete example configuration:

```yaml
messages:
  outgoing-format: "&5[&dWhisper&5] &7To &d{recipient}&7: &f{message}"
  incoming-format: "&5[&dWhisper&5] &7From &d{sender}&7: &f{message}"
  group-format: "&5[&dWhisper&5] &7From &d{sender}&7 to &d{recipients}&7: &f{message}"
  primary-color: "&d"
  secondary-color: "&5"
  text-color: "&f"
  accent-color: "&7"

encryption:
  key-size: 256
  key-expiry: 60
  encrypt-storage: true

chat-history:
  enabled: true
  max-messages: 100
  retention-days: 30
  allow-clearing: true

advanced:
  debug: false
  use-at-symbol: true
  custom-prefix: "!"
  exit-command: ".exit"
  message-offline-players: false
  use-metrics: true
  check-updates: true
```

## Need More Help?

If you encounter any issues with configuration or have questions, please refer to:

- [Our Discord server](https://discord.gg/yourdiscord)
- [GitHub Issues](https://github.com/Amineos/WhisperChain/issues) 