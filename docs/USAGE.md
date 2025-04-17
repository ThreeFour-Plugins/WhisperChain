# WhisperChain Usage Guide

This guide covers all the features and usage patterns for WhisperChain, the end-to-end encrypted messaging plugin for Minecraft.

## Table of Contents

- [Basic Usage](#basic-usage)
- [Chat Syntax](#chat-syntax)
- [Multiple Recipients](#multiple-recipients)
- [Private Chat Mode](#private-chat-mode)
- [Chat History](#chat-history)
- [Advanced Features](#advanced-features)
- [Troubleshooting](#troubleshooting)

## Basic Usage

WhisperChain allows you to send private, encrypted messages to other players on the server using a simple chat syntax.

### Sending a Private Message

To send a private message to another player, simply type:

```
@PlayerName Your secret message goes here
```

For example:
```
@Steve Hey, want to go mining with me later?
```

Only the player named "Steve" would see this message. The message is encrypted with Steve's unique key, ensuring that only Steve can read it.

## Chat Syntax

Here's the complete syntax for sending messages:

| Format | Description | Example |
|--------|-------------|---------|
| `@PlayerName message` | Basic private message | `@Alex How are you?` |
| `@Player1,Player2 message` | Message to multiple players | `@Alex,Steve Meeting at coordinates 100,100` |

## Multiple Recipients

You can send a message to multiple players at once by separating their names with commas:

```
@Player1,Player2,Player3 This message goes to all three players
```

Each recipient receives the message with their own unique encryption, maintaining end-to-end privacy for everyone involved.

## Private Chat Mode

If you're having a longer conversation with someone, it can be tedious to type `@PlayerName` before every message. WhisperChain offers a private chat mode where all your messages automatically go to a specific player.

### Entering Private Chat Mode

Private chat mode is activated automatically after sending a private message to someone. When in private chat mode, all your messages will be sent only to that player until you exit the mode.

### Exiting Private Chat Mode

To exit private chat mode and return to global chat, simply type:

```
.exit
```

## Chat History

WhisperChain keeps a record of your encrypted conversations with each player. Your chat history is only visible to you and the player you were chatting with.

## Advanced Features

### Message Encryption

All messages sent through WhisperChain are encrypted with AES-256 encryption, one of the strongest encryption standards available. Each player on the server has their own unique encryption key that is generated automatically.

### Privacy from Server Admins

WhisperChain is designed to provide true end-to-end encryption, which means:

- Messages don't appear in server logs
- Server administrators cannot read your private messages
- Even if someone has access to the server database, they cannot decrypt your messages

## Troubleshooting

### Common Issues

**Issue**: Player doesn't receive my message.
**Solution**: Make sure you've spelled their name correctly and that they are online.

**Issue**: Accidentally sent a message to global chat that was meant to be private.
**Solution**: Always make sure to include the `@` symbol before the player's name.

**Issue**: Can't exit private chat mode.
**Solution**: Type `.exit` in chat to return to global chat mode.

### Getting Help

If you encounter any issues not covered in this guide, reach out to a server administrator or refer to the [GitHub repository](https://github.com/Amineos/WhisperChain) for more information. 