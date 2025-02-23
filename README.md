<img align="center" alt="statsify-bot-banner" src="https://i.postimg.cc/R0Vm53sF/Untitled-1280-x-319-px.png">

<p align="center">
<img align="center" alt="GitHub issues" src="https://img.shields.io/github/issues/Kkkermit/Statify?style=for-the-badge"> 
<img align="center" alt="GitHub license" src="https://img.shields.io/github/license/Kkkermit/Statify?style=for-the-badge">
<img align="center" alt="GitHub license" src="https://img.shields.io/github/stars/Kkkermit/Statify?style=for-the-badge">
<img align="center" alt="GitHub license" src="https://img.shields.io/github/forks/Kkkermit/Statify?style=for-the-badge">
</p>

<p align="center"><strong>
Statsify, an making use of easy tracking of Spotify statistics straight through Discord
</strong></p>

<p align="center">
A powerful Discord bot that integrates with Spotify to provide user statistics and music insights. Built with JDA and featuring a secure API system.
</p>

> To test out Statsify, be sure to invite him to your server by clicking [here](https://discord.com/oauth2/authorize?client_id=1341964506862977024)

<h1 align="center"><strong>
⭐ If your a fan of this repository or have used it or any of it's code, please consider leaving us a star. It would be greatly appreciated and allows us to see if users value the bot! ⭐
</strong></h1>

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
  - [Discord Setup](#discord-setup)
  - [Spotify Setup](#spotify-setup)
  - [Environment Variables](#environment-variables)
- [Local Development](#local-development)
- [Deployment](#deployment)
- [API Usage](#api-usage)
- [Bot Commands](#bot-commands)
- [Security](#security)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [Support](#support)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Features

- 🎵 **Spotify Integration**
  - User authentication via OAuth2
  - Fetch user statistics and profile information
  - Secure data handling

- 🤖 **Bot Commands**
  - `/spotify` - View your Spotify statistics
  - Modern slash command support
  - Intuitive command handling

- 🔒 **Secure API**
  - Protected endpoints with API key authentication
  - Rate limiting and error handling
  - CORS support for web integration

## Prerequisites

- Java 17 or higher
- Maven
- Discord Bot Token
- Spotify Developer Account
- Railway Account (for deployment)

## Configuration

1. **Discord Setup**
   - Create a new application at [Discord Developer Portal](https://discord.com/developers/applications)
   - Create a bot and copy the token
   - Enable necessary intents (Presence, Server Members, Message Content)

2. **Spotify Setup**
   - Create a new app at [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
   - Add `https://your-railway-app.up.railway.app/callback` to Redirect URIs
   - Copy Client ID and Client Secret

3. **Environment Variables**
   Create a `.env` file in the root directory:
   ```properties
   BOT_TOKEN=your_discord_bot_token
   SPOTIFY_CLIENT_ID=your_spotify_client_id
   SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
   SPOTIFY_REDIRECT_URI=https://your-railway-app.up.railway.app/callback
   API_KEY=your_secure_api_key
   ```

## Local Development

1. **Clone and Build**
   ```bash
   git clone https://github.com/Kkkermit/statsify.git
   cd statsify
   mvn clean package
   ```

2. **Run Locally**
   ```bash
   java -jar target/bot.jar
   ```

## Deployment

1. **Railway Setup**
   - Create new project in Railway
   - Connect your GitHub repository
   - Add environment variables from your `.env` file

2. **Deploy**
   ```bash
   railway up
   ```

## API Usage

The bot provides a REST API for accessing Spotify statistics:

```bash
# Get user stats
curl -H "X-API-Key: your_api_key" \
     "https://your-railway-app.up.railway.app/api/stats?userId=discord_user_id"
```

## Bot Commands

1. **Spotify Stats**
   ```
   /spotify
   ```
   - Shows your Spotify profile information
   - Displays follower count and profile URL
   - Requires one-time authorization

## Security

- API endpoints are protected with an API key
- OAuth2 flow for Spotify authentication
- Secure environment variable handling
- CORS protection

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## Troubleshooting

- **Bot Not Responding**: Check Discord token and intents
- **Spotify Auth Failed**: Verify redirect URI in Spotify Dashboard
- **API 401 Error**: Ensure API key is properly set
- **Railway Deploy Failed**: Check environment variables

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and feature requests, please [open an issue](https://github.com/Kkkermit/Statify/issues).

## Acknowledgments

- JDA (Java Discord API)
- Spotify Web API Java
- SparkJava
- Railway for hosting