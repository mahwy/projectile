# Planning Poker

@@@ note { title=Prerequisites }

This page assumes you have checked out a local copy of [estimate.poker](https://github.com/KyleU/estimate) and switched to the `09-websocket` branch

@@@


Wow, *finally* we get around to building an actual useful application. 
After the last step, we've got a web application that connects via Websocket, and passes ping messages.
Now we'll extend that application to support [planning poker](https://en.wikipedia.org/wiki/Planning_poker) sessions.


## SessionContext

Since we're lazy, let's create a shared SessionContext object that hold sets of members, polls, and votes. 
These match our database model, and uses the classes generated by Projectile. 
This class will be used on the JVM as well as in JavaScript

- [`shared/src/main/scala/models/session/context/SessionContext.scala`](https://github.com/KyleU/estimate/blob/10-planning-poker/shared/src/main/scala/models/session/context/SessionContext.scala)

Let's also create a listener, that registers callbacks and reacts to change in the SessionContext

- [`shared/src/main/scala/models/session/context/SessionContextListener.scala`](https://github.com/KyleU/estimate/blob/10-planning-poker/shared/src/main/scala/models/session/context/SessionContextListener.scala)


## Protocol

We'll extend our messages files to define our protocol, mainly just add/edit/remove messages and updates from the server

- [`shared/src/main/scala/models/message/ClientMessage.scala`](https://github.com/KyleU/estimate/blob/10-planning-poker/shared/src/main/scala/models/message/ClientMessage.scala)
- [`shared/src/main/scala/models/message/ServerMessage.scala`](https://github.com/KyleU/estimate/blob/10-planning-poker/shared/src/main/scala/models/message/ServerMessage.scala)


## SessionManager


## Explore the code

https://github.com/KyleU/estimate/tree/10-planning-poker

See this branch's Pull Request for detailed comments on the generated files

https://github.com/KyleU/estimate/pull/10


## Next steps

@ref[We're done](11-wrapping-up.md)!
