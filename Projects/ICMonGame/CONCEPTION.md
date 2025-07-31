# ICMon Game Conceptual Design Document

## Introduction

This document outlines the key design decisions and architectural considerations made during the development of the ICMon game. It details modifications to the proposed architecture, important classes and interfaces added, and the behavior attributed to each component.

### Event-Driven Architecture

The game is designed around a robust event-driven architecture, with ICMonEvent at its core. This design allows for a flexible and modular approach to game development, where different events manage specific segments of gameplay, such as battles, interactions, or narrative progression.

### Action Encapsulation

Actions, encapsulated by the Action interface and its implementations, represent discrete operations within the game. This design allows events to execute complex sequences of actions without becoming overly complex or monolithic.

## Overview

Welcome to ICMon, a rich and interactive game world where players embark on an adventure filled with challenges, battles, and intriguing narratives. This document serves as a comprehensive guide to the game's architecture, detailing the mechanics, structures, and functionalities that make up the ICMon experience.

## Key Components

### Events

Events are the backbone of the ICMon game, driving the narrative and player interactions. Each event is an instance of ICMonEvent or its subclasses, designed to handle specific game scenarios:

- \*\*ICMonEvent (ch.epfl.cs107.icmon.gamelogic.events.ICMonEvent)\*\*: This abstract base class manages the lifecycle of all events in the game. It includes methods to start, complete, suspend, and resume events, as well as to attach specific actions to these phases.

- \*\*IntroductionEvent (ch.epfl.cs107.icmon.gamelogic.events.IntroductionEvent)\*\*: Manages the game's introduction, presenting the initial narrative and setting through dialogues and text graphics.

- \*\*PokemonFightEvent (ch.epfl.cs107.icmon.gamelogic.events.PokemonFightEvent)\*\*: Handles the dynamics of Pokemon battles, tracking each fighter's status and the progression of the fight.

- \*\*FirstInteractionWithProfOakEvent (ch.epfl.cs107.icmon.gamelogic.events.FirstInteractionWithProfOakEvent)\*\*: A special event representing the player's first meeting with Professor Oak, triggering the beginning of their Pokemon journey.

- \*\*EndOfTheGameEvent (ch.epfl.cs107.icmon.gamelogic.events.EndOfTheGameEvent)\*\*: Signifies the completion of the game's main narrative or a significant milestone, handling any concluding interactions or dialogs.

### Actions

Actions are discrete operations that events can perform. They represent the smallest unit of gameplay logic and are used to modify the game's state or the player's progress.

- \*\*Action (ch.epfl.cs107.icmon.gamelogic.actions.Action)\*\*: The foundational interface for all actions. Each action must implement the perform method, defining what occurs when the action is executed.

- \*\*StartDialogAction (ch.epfl.cs107.icmon.gamelogic.actions.StartDialogAction)\*\*: Initiates a dialogue with the player. It's used within events to present information, choices, or narrative progressions.

- \*\*RegisterEventAction and UnRegisterEventAction (ch.epfl.cs107.icmon.gamelogic.actions)\*\*: Manage the registration and unregistration of events with the game's event manager, effectively scheduling or removing them from the game's active event loop.

### Messaging

Gameplay messages are used for communicating changes in game state or player actions, particularly those that affect the flow or outcome of events.

- \*\*GamePlayMessage (ch.epfl.cs107.icmon.gamelogic.messaging.GamePlayMessage)\*\*: An interface defining a standard for all gameplay messages. Each message must implement the process method, detailing how it affects the game.

- \*\*SuspendWithEvent (ch.epfl.cs107.icmon.gamelogic.messaging.SuspendWithEvent)\*\*: A specific type of message that pauses the game or an ongoing event, often in response to user input or a significant game event. It typically involves suspending all actions and animations.

## Game Logic

ICMon's game logic is driven by the interaction of events and actions. Players progress through the game by triggering events, which in turn execute actions to modify the game state or present new scenarios. The game responds dynamically to player inputs, with each decision or interaction potentially leading to different outcomes or paths.

## Extensions 

Sign () element (draft)
Animation ? in front of a Door or a Sign()
## Conclusion

This conceptual design document outlines the core architecture and components of the ICMon game. By adopting an event-driven architecture and encapsulating actions and messages, the game achieves a flexible and modular design. Each component is carefully crafted to fit within this architecture, contributing to the game's

---