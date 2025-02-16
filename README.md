# README.md

# Discord Bot

This project is a Discord bot that features an advanced and beginner-friendly handler system, utilizing events, functions, commands, and utilities for easier navigation.

## Features

- **Command Management**: Easily register, execute, and list commands.
- **Event Handling**: Respond to various Discord events with a structured event manager.
- **Configuration Utilities**: Manage bot settings through a configuration file.

## Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd discord-bot
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Configure your bot settings in `src/resources/config.properties`.

5. Run the bot:
   ```
   mvn exec:java -Dexec.mainClass="Main"
   ```

## Usage Guidelines

- Customize your bot's token and prefix in the `config.properties` file.
- Add commands by implementing new classes in the `commands` package.
- Handle events by creating listeners in the `events` package.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.