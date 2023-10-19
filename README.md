# Start GG Bot

This is an example of a Discord bot, primarily used to demonstrate my Spring Boot capabilities.

Discord is an online instant messaging and VoIP social platform. It is commonly used by gamers around the world to 
communicate and stay in contact with eachother. There are many communities for various games, hobbies and even trades.

StartGG is a website that provides tournament organising tools for eSports. This involves tournament brackets, payments and rankings. 
It is a popular application for video game tournaments. A tournament can have multiple events, for example the way tennis may have singles and doubles.

The purpose of this bot is to allow a user to subscribe to a tournament and receive periodic updates on the current standings for a particular event.
This means users do not have to check StartGG manually, and will receive updates from the bot when standings change.


## Basic Architecture

The user will send a message to the bot containing `!subscribe tournament-slug event-slug`.

This will cause the bot to register that tournament event with the channel that the user sent the message on. 

The bot will then poll the StartGG API for updates to that tournament event. If the standings of the event change, the user will be notified on the channel they registered the bot with. Unfortunately, there is no Webhook implementation for StartGG, so I'm unable to just wait for updates. 

This will continue until the user unsubscribes with `!unsubscribe tournament-slug event slug`, the event ends and a final winner is decided, or the bot finds itself unable to access the tournament standings.
The final case can only really happen if a tournament organiser makes the event private or deletes it. Later in this document, I detail workarounds and improvements for a full application that would handle StartGG or the bot going down.

### Discord

This application contains event based architecture. It waits for Discord messages in order to determine which tournament events it should subscribe to. 

The `MessageListener` is a generic Listener class that allows the developer to execute code based on messages the bot receives.
I have designed it so that there are separate methods for validating whether a command is correct, and whether this bot should respond to other bots.

The philosophy behind this is that any developer can come and extend `MessageListener` to implement their own logic, and they only have to think of two logical methods to write.

I have written two `MessageListener`'s, one for subscribing to an event, and one for unsubscribing from an event.

The Discord side is authenticated with a simple token, that can be retrieved from your Discord developer account to host the bot.

#### Improvements and Thoughts

- Event based architecture means not a lot of waste! 
- The `MessageListener` class is lightweight and extendable. 
- `MessageListener` could be extended to recognise more content from Discord messages, such as whether a message contains a file or link.
- Discord commands are natively supported, and could be used to replace this implementation in the future.

### StartGG

The StartGG side of the application is a simple API endpoint being called.

There is no authentication required to access this API, so I can freely call it when a user supplies the correct arguments to the bot.

Right now, only one API call has been implemented, and it has been implemented manually.

#### Improvements and Thoughts

- An `Entrant` from StartGG is a record! This is good because this data should not be altered by this application. It ensures that any `Entrant` information passed back to the user is accurate and unchanged.
- OpenAPI Generator could be used to automate the REST API, as opposed to writing it myself, but for this example one endpoint was sufficient.
- The generator would also autogenerate responses and StartGG model, which would be useful in a fully fledged implementation.

### BotChannelRegistry

After the user successfully sends a message, the channel they're on will be registered with the `BotChannelRegistry`. This keeps track of all events that should be checked, along with the list of channels to send the update to.

This is stored in a `ConcurrentHashmap`, as this application has potential to be multithreaded.

#### Improvements and Thoughts
- Simple and effective for a PoC like this.
- Nice and airtight. It does not go beyond storing the keys, ensuring the Single Responsibility Principle is maintained.
- Easy to replace.
- In future this could be a service of its own, such as a database. This way, registered channels will not be forgotten if the bot application fails and restarts.

### BotEventCache

This part of the application is a simple cache.

When the bot checks for an update for a particular tournament event, it needs to verify that it's standings have changed from when it last checked. 

This has been implemented as a `ConcurrentHashmap` too. The bot will retain previous event information indefinitely, so that less API requests need to be made.

#### Improvements and Thoughts

- Similar benefits to the `BotChannelRegistry`.
- Could become an in-memory database, such as Redis/Memcached, in the future.
- There are no mechanisms for removing old tournament information at the moment. Something to clean up would be required for a full application.

### BotScheduler

The `BotScheduler` really contains the main business logic for the application.

Since there is no Webhook available for StartGG, the bot application must periodically check StartGG for updates.

There is a Scheduled task that will periodically perform this update. It will iterate through the registered tournament events, and send updates to any channels that are subscribed to it.
If a tournament event is complete, it will remove it from the registry, and send a final message to the user.

The `BotScheduler` makes use of parallel streams to process the registry quickly.

#### Improvements and Thoughts

- Adjustable polling time is good!
- Standing number could be configured by the user rather than the person who deploys the bot in a full application.
- Could be completely replaced if StartGG supported Webhooks!
- Could be extended to give users updates about other information, such as match results.