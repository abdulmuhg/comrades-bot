# Discord Music Bot

A Java-based Discord bot that allows you to play music in your Discord server voice channels.

## Features

- Play music from YouTube, SoundCloud, and other sources
- Queue system for multiple tracks
- Skip, stop, and view queue commands
- Simple and easy-to-use command system
- Built with JDA (Java Discord API) and LavaPlayer

## Commands

- `!play <song name or URL>` - Play a song or add it to the queue
- `!skip` - Skip the current song
- `!stop` - Stop playback and clear the queue
- `!queue` - View the current queue
- `!help` - Show this help message

## Requirements

- Java 11 or higher
- Gradle 7.0 or higher (for building)
- A Discord Bot Token

## Setup and Installation

### Create a Discord Bot

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications)
2. Click "New Application" and give it a name
3. Go to the "Bot" tab and click "Add Bot"
4. Under "Privileged Gateway Intents," enable:
   - SERVER MEMBERS INTENT
   - MESSAGE CONTENT INTENT
   - PRESENCE INTENT
5. Copy your bot token (you'll need this later)

### Clone and Build the Bot

```bash
# Clone the repository
git clone https://github.com/abdulmuhg/comrades-bot.git
cd comrades-bot

# Build the project
./gradlew shadowJar
```

### Configure the Bot

Create a file called `config.properties` in `src/main/resources/` with the following content:

```properties
bot.token=YOUR_BOT_TOKEN_HERE
```

Replace `YOUR_BOT_TOKEN_HERE` with your actual Discord bot token.

### Run the Bot

```bash
java -jar build/libs/discord-music-bot.jar
```

### Invite the Bot to Your Server

1. Go to the [Discord Developer Portal](https://discord.com/developers/applications)
2. Select your application
3. Go to the "OAuth2" → "URL Generator" tab
4. Select the following scopes:
   - `bot`
   - `applications.commands` (optional, for future slash command support)
5. Select the following bot permissions:
   - Send Messages
   - Connect
   - Speak
   - Use Voice Activity
   - Read Message History
6. Copy the generated URL
7. Open the URL in your browser and select your server

## Usage

1. Join a voice channel in your Discord server
2. Use `!play <song name or URL>` to play music
3. Use other commands as needed

## Building from Source

If you want to modify the bot and build it yourself:

```bash
git clone https://github.com/abdulmuhg/comrades-bot.git
cd comrades-bot
./gradlew shadowJar
```

The built JAR file will be in `build/libs/`.

## Troubleshooting

### Bot can't play YouTube videos

This is usually due to YouTube's API changes. Try:
- Updating LavaPlayer to the latest version
- Using direct audio file URLs or non-YouTube sources
- Check your network configuration

### Bot can't find config file

Ensure your `config.properties` file is in the correct location and has the right permissions.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)
- [LavaPlayer](https://github.com/sedmelluq/lavaplayer)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
