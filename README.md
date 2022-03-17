# Necrotic-Server
This project targets JDK 8 (Java 8), but should run on up to JDK 17. We are targeting intellij. This project listens on port 43594. For others to connect to your personal PC, port 43594 must be open. 

## Follow these steps to get setup

- [ ] Fork and then download this repo and [Necrotic-Client](https://github.com/NecroticPublic/Necrotic-Client/)
- [ ] Download and install Intellij. 
- [ ] Download the cache from [Mega](https://mega.nz/file/s8cwVLhC#NeGH0hPsVLJHzx8cTieYpd7jzUX-rvcifTu22Kw2UZo) or [Dropbox](https://www.dropbox.com/s/up31n4hfne0dqpd/NecroticCache.zip?dl=1).
- [ ] Make 3 folders in your %userprofile% directory. Windows users can press Windows Key + R to open run, then type "*%userprofile*%" in the box.

IE:

![Example run](https://i.imgur.com/0Z8wMJq.png)

Make this folder exists: "NecroticCache"

For example, say your username was bob.
- Bob/NecroticCache

![Example of userprofile](https://i.imgur.com/Twmfc4u.png)

Next, unzip NecroticCache.zip and place it in the "NecroticCache" folder. Inside must look like this:

![Dir](https://i.imgur.com/v5X9IDx.png)

- [ ] Open this project in Intellij.

- [ ] Configure your project settings.

Target SDK 1.8 and Language Level 8.

![Project setup](https://i.imgur.com/n3A5MfU.png)

Fix imports. 
Create a run config.
Run the project.

The main class to run is: **com.ruse.GameServer**

You'll know the project runs correctly when you get:

- A client that looks like this (after a potentially long load)

![Client](https://i.imgur.com/jjvAp6F.png)

- A server startup message **Necrotic is now online on port 43594!**
