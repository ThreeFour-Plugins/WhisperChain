<div align="center">

# 🔐 WhisperChain

<img src="docs/images/logo.png" alt="WhisperChain Logo" width="200"/>

### End-to-End Encrypted Messaging for Minecraft

[![GitHub release](https://img.shields.io/github/v/release/Amineos/WhisperChain?style=flat-square)](https://github.com/Amineos/WhisperChain/releases)
[![Build Status](https://img.shields.io/github/workflow/status/Amineos/WhisperChain/Build?style=flat-square)](https://github.com/Amineos/WhisperChain/actions)
[![License](https://img.shields.io/github/license/Amineos/WhisperChain?style=flat-square)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21%2B-orange?style=flat-square)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.21%2B-lightgrey?style=flat-square)](https://papermc.io/)
[![Discord](https://img.shields.io/badge/Discord-Join%20Us-7289DA?style=flat-square&logo=discord)](https://discord.gg/yourdiscord)

*True privacy in Minecraft chat - no one sees your messages but the intended recipients*

</div>

---

## ✨ Features

- **🔒 End-to-End Encryption** - Messages encrypted with AES-256 for maximum security
- **👥 Private & Group Messaging** - Send to one player or many with the same level of privacy
- **🧠 Intuitive Interface** - Simple @player syntax works right in the chat you already use
- **🔄 Private Chat Mode** - Enable continuous private conversations without typing @ each time
- **📜 Chat History** - Access your previous encrypted conversations with each player
- **👻 Hidden from Logs** - Messages never appear in server logs, ensuring complete privacy
- **⚡ Lightweight** - Minimal performance impact on your server

---

## 📖 Table of Contents

- [How It Works](#-how-it-works)
- [Quick Start](#-quick-start)
- [Usage Examples](#-usage-examples)
- [Installation](#-installation)
- [For Developers](#-for-developers)
- [FAQ](#-frequently-asked-questions)
- [Support](#-support)
- [License](#-license)

---

## 🔍 How It Works

WhisperChain creates a secure, private communication channel using advanced encryption:

1. When you join, a unique encryption key is generated just for you
2. When you message someone, your text is encrypted specifically for the recipient
3. Only the intended recipients can decrypt and read the message
4. The message never appears in logs, console, or to server administrators
5. Your entire conversation history remains encrypted and private

<div align="center">
<img src="docs/images/encryption-diagram.png" alt="Encryption Diagram" width="600"/>
</div>

---

## 🚀 Quick Start

```
@PlayerName Hey, want to go mining with me later?
```

That's it! Your message is now encrypted and sent privately.

For group messages:
```
@Alex,Steve,Maria Let's meet at coordinates x:100 y:64 z:-250
```

---

## 💬 Usage Examples

### Sending Private Messages

Simply use the @ symbol followed by a player's name in normal chat:

```
@PlayerName Your secret message here
```

### Messaging Multiple Players

Separate multiple recipients with commas:

```
@Player1,Player2,Player3 Hello to all of you!
```

### Private Chat Mode

Having a longer conversation? All your messages will go directly to the last person you messaged.

1. Start by sending a message with `@PlayerName`
2. Continue chatting naturally without the @ prefix
3. Type `.exit` when you want to return to global chat

<div align="center">
<img src="docs/images/chat-example.png" alt="Chat Example" width="600"/>
</div>

---

## 📥 Installation

### Requirements

- **Server**: Paper 1.21+ or compatible forks
- **Java**: Version 21 or higher

### Setup Instructions

1. [Download the latest release](https://github.com/Amineos/WhisperChain/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Start sending encrypted messages - no configuration needed!

### Optional Configuration

The plugin works out of the box with no configuration, but you can customize:

- Message colors and formatting
- Encryption strength
- Chat history storage

See the [detailed configuration guide](docs/CONFIGURATION.md) for more information.

---

## 👨‍💻 For Developers

Want to contribute to WhisperChain? We welcome contributions of all kinds!

### Building from Source

```bash
git clone https://github.com/Threefour-Plugins/WhisperChain.git
cd WhisperChain
./gradlew build
```

The built plugin will be available at `build/libs/WhisperChain-{version}.jar`.

### API Documentation

WhisperChain provides an API for other plugins to integrate with encrypted messaging. [See the API documentation](docs/API.md) for details.

Check out our [Contributing Guide](CONTRIBUTING.md) for more information on submitting pull requests.

---

## ❓ Frequently Asked Questions

<details>
<summary><b>Is WhisperChain truly private from server admins?</b></summary>
Yes! Messages are encrypted with each recipient's unique key. Even server administrators with console access cannot read the messages.
</details>

<details>
<summary><b>Can I use WhisperChain with other chat plugins?</b></summary>
WhisperChain is designed to work alongside most chat plugins. It captures private messages before they're processed by other plugins.
</details>

<details>
<summary><b>How secure is the encryption?</b></summary>
WhisperChain uses AES-256 encryption, which is an industry standard for sensitive data and is considered highly secure.
</details>

<details>
<summary><b>Will my messages be saved if I disconnect?</b></summary>
Your message history is stored encrypted on the server, so you can see previous conversations when you return.
</details>

---

## 🔧 Support

Need help? We've got you covered:

- [Detailed Usage Guide](docs/USAGE.md)
- [GitHub Issues](https://github.com/Amineos/WhisperChain/issues)
- [Discord Community](https://discord.gg/yourdiscord)

---

## 📜 License

WhisperChain is open-source software licensed under the [MIT License](LICENSE).

---

<div align="center">

### Made with ❤️ by [Amineos](https://github.com/aminegames125)

</div> 