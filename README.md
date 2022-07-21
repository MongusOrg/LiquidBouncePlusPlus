# LiquidBounce+
A free mixin-based injection hacked-client for Minecraft 1.8.9 using Minecraft Forge.

### Thank you.
to Minecraft for creating the game, \
to Forge for making modding possible, \
to CCBlueX for creating LiquidBounce, which LiquidBounce+ is based on, \
to all contributors on all platforms (Forum, Discord, Github...) for making huge contributions, \
to every person using LiquidBounce and its forks for creating a big community of the best free Forge-based client.

Last year was wonderful for me. To be able to meet and be friend with so many new people is something I've never thought to be possible when I first made this fork public. If I still have a chance, I will definitely work on this fork whenever I'm free. But everything has to come to an end, I have much less time to focus on coding from time to time. So I have made the final decision and decided to officially stop developing this fork indefinitely. Thank you again and hope to see you one day.

\- inf -

*If you still wish to contact me often, inf#0425 is my Discord tag. However I may not reply to messages early. Also I may randomly check LiquidBounce's forum under the same name as on Github.*

### LiquidBounce's contact info
Website: https://liquidbounce.net \
Forum: https://forums.ccbluex.net \
Guilded: https://www.guilded.gg/CCBlueX \
YouTube: https://youtube.com/CCBlueX \
Twitter: https://twitter.com/CCBlueX 

## Issues
If you notice any bugs or missing features, you can let us know by opening an issue [here](https://github.com/WYSI-Foundation/LiquidBouncePlus/issues).

## License
This project is subject to the [GNU General Public License v3.0](LICENSE). This does only apply for source code located directly in this clean repository. During the development and compilation process, additional source code may be used to which we have obtained no rights. Such code is not covered by the GPL license.

For those who are unfamiliar with the license, here is a summary of its main points. This is by no means legal advice nor legally binding.

You are allowed to
- use
- share
- modify

this project entirely or partially for free and even commercially. However, please consider the following:

- **You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated) application.**
- **Your modified application must also be licensed under the GPL** 

Do the above and share your source code with everyone; just like we do.

## Setting up a Workspace
LiquidBounce+ is using Gradle, so make sure that it is installed properly. Instructions can be found on [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/WYSI-Foundation/LiquidBouncePlus/`. 
2. CD into the local repository folder.
4. Depending on which IDE you are using execute either of the following commands:
    - For IntelliJ: `gradlew --debug setupDevWorkspace idea genIntellijRuns build`
    - For Eclipse: `gradlew --debug setupDevWorkspace eclipse build`
5. Open the folder as a Gradle project in your IDE.
6. Select the default run configuration.

## Additional libraries
### Mixins
Mixins can be used to modify classes at runtime before they are loaded. LiquidBounce+ is using it to inject its code into the Minecraft client. This way, we do not have to ship Mojang's copyrighted code. If you want to learn more about it, check out its [Documentation](https://docs.spongepowered.org/5.1.0/en/plugin/internals/mixins.html).