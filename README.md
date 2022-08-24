<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/PlusPlusMC/LiquidBouncePlusPlus">
    <img src="src/main/resources/assets/minecraft/liquidbounce%2B/big.png" alt="Logo" width="100" height="100">
  </a>
  <h3 align="center">LiquidBounce++</h3>

  <p align="center">
    An free and open source Forge 1.8.9 hacked client for Minecraft!
    <br />
    <a href="https://github.com/PlusPlusMC/LiquidBouncePlusPlus/wiki"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/PlusPlusMC/LiquidBouncePlusPlus">View Demo</a>
    ·
    <a href="https://github.com/PlusPlusMC/LiquidBouncePlusPlus/issues">Report Bugs</a>
    ·
    <a href="https://github.com/PlusPlusMC/LiquidBouncePlusPlus/issues">Request Features</a>
  </p>
</div>

  <h3 align="left">Good night LiquidBounce+</h3>
LiquidBounce+ is discontinued, we hope inf will return.

  <h3 align="left">Info</h3>
Website: https://lbplusplus.ga /
YouTube: none / Discord: https://discord.gg/EyqcPDEBd8

  <h2 align="left">How to install</h2>
1. Download [Forge 1.8.9](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html)
3. Run forge installer jar.
4. Run forge 1.8.9 in your minecraft launcher
(After Minecraft has loaded, you can close it)
4. Go to your minecraft folder's mods
   - Windows: %APPDATA%\\.minecraft\\mods
   - Mac OS: ~/Library/Application Support/minecraft/mods
   - Linux: ~/.minecraft/mods
5. Put LiquidBounce++ jar in the mods folder
6. Run forge 1.8.9 in your minecraft launcher again

  <h2 align="left">License</h2>
This project is subject to the [GNU General Public License v3.0](LICENSE). This does only apply for source code located directly in this clean repository. During the development and compilation process, additional source code may be used to which we have obtained no rights. Such code is not covered by the GPL license.

For those who are unfamiliar with the license, here is a summary of its main points. This is by no means legal advice nor legally binding.

You are allowed to
- use
- share
- modify

this project entirely or partially for free and even commercially. However, please consider the following:

- **You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated) application.**
- **Your modified application must also be licensed under the GPL.** 

Do the above and share your source code with everyone; just like we do.

  <h2 align="left">Setting up a Workspace</h2>
LiquidBounce++ is using Gradle, so make sure that it is installed properly. Instructions can be found on [Gradle's website](https://gradle.org/install/).
1. Clone the repository using `git clone https://github.com/PlusPlusMC/LiquidBouncePlusPlus`. 
2. CD into the local repository folder.
3. Depending on which IDE you are using execute either of the following commands:
    - For IntelliJ: `gradlew setupDevWorkspace idea genIntellijRuns build`
    - For Eclipse: `gradlew setupDevWorkspace eclipse build`

    (you can add `-debug` right after `gradlew` if you want to enable debug logging.)
4. Open the folder as a Gradle project in your IDE.
5. Select the default run configuration.

  <h2 align="left">Additional libraries</h2>
  <h3 align="left">Mixins</h3>
Mixins can be used to modify classes at runtime before they are loaded. LiquidBounce++ is using it to inject its code into the Minecraft client. This way, we do not have to ship Mojang's copyrighted code. If you want to learn more about it, check out its [Documentation](https://docs.spongepowered.org/5.1.0/en/plugin/internals/mixins.html).
