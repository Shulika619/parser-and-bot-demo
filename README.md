# Parser and Telegram Bot

A useful program for collecting data from HTML pages. For convenience, you can download the latest version of the file in the Telegram bot. More details about the functions in the next section

## Features

- Using the async Scheduler, the program starts parsing at the specified date/time
- Administrator can start parsing from a bot
- The parser goes through all pages of the selected categories
- If an error occurs during parsing, retries begin (@Retryable) and the administrator receives a message in the Telegram bot
- By default all bot commands are available only to the Administrator, only he can give rights to download files
- Users get the latest version from the files

## Usage

Аdd `.env` file to the root directory, it will contain environment variables

````properties
TASK_CRON=0 0 6 * * ?
SITE_NAME=SomeSiteName
SITE_URL=https://example.com
BOT_NAME=botUserName
BOT_TOKEN=777777777777777
ADMIN_CHAT_ID=11111111111
````

Аdd categories.txt file to the root directory, contains site categories

````txt
/cats
/dogs
````

You will also need to register the Telegram bot in [BotFather](https://t.me/BotFather)

## TelegramBot commands

- `/start` bot start, displays available commands and instructions

### Administrator Commands  

- `useradd 12345678 Bob` add a user (Telegram chatId)(Name)
- `userdelete 12345678` delete user (Telegram chatId)
- `userlist` a list of users
- `parse siteName` start data parsing

### Users commands
- `/price` get file, displays a menu for selecting a site
  
> [!NOTE]  
> Access is only available after the administrator adds users to the list

## Technologies
- Java 17
- Spring Boot
- Spring Web
- Spring Retry
- AOP
- Jsoup
- Telegrambots
- Telegrambots Abilities
- Lombok
- Maven
- Git/GitHub

##
> [!IMPORTANT]  
> If you want the program to collect data from another site, you need to make changes to the `ParserServiceImpl.java` file depending on the HTML markup of the site!
