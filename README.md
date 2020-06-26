# Twitch Chat Bridge
This light-weight mod links a Twitch channel's chat to your Minecraft chatbox!

There are more mods that do this, but none are available for Fabric... so I made it!

This mod requires [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) and 
[Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) to work.

### Usage:
1.  Get a Twitch oauth key here https://twitchapps.com/tmi/ and write it down somewhere.
2.  Open the mod menu.
3.  Open this mod's configuration inside that menu.
4.  Fill in the config textboxes.
5.  Go ingame and type /twitch watch \<channel\> (channel should be the name of the Twitch channel you want to join).
You can use this command as many times as you want. 
6.  Type /twitch enable.

### Also available on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/twitch-chat)

I've started school, so this project is going to be updated sparingly, but I'll try to answer every comment and resolve
your issues.

### Translations
If you find the mod is not available on a language you know I would really appreciate it if you could create a pull
request with a translation for that language.

Language files are located in [src/main/resources/assets/twitchchat/lang](src/main/resources/assets/twitchchat/lang)
and they contain translations for every (translatable) user-facing piece of text in the mod.

If you're interested you'll have to create a file with an appropiate file name for your language. To find your
language's code, visit this [link](https://minecraft.gamepedia.com/Language#Available_languages).

You should probably use US or UK English as a base for your translation. You can find their respective files
by clicking on these links: [en_us.json](src/main/resources/assets/twitchchat/lang/en_us.json) and
[en_gb.json](src/main/resources/assets/twitchchat/lang/en_gb.json).

Don't translate **%d** or **%s** signs, they're used to dynamically insert numbers and text (respectively).

### Contact

If you have any questions don't hesitate to email, tweet or DM me, you can find my public profiles on my
[GitHub profile](https://github.com/PabloPerezRodriguez). I'll answer ASAP.

Just don't personally send me bugs, ideas or feature/help requests, those go in
[GitHub issues](https://github.com/PabloPerezRodriguez/twitch-chat/issues) or
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/twitch-chat).