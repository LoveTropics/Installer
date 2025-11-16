# Building an Installer

### Prerequisites

- Windows
- 7-zip

## Create a bundle

- Create a bundle folder with the following files:
  - `installer.json`: An installer config file (see below)
  - `installer.jar`: A build of the LT Installer (this repo!)
  - `forge-installer.jar`: A copy of the (Neo)Forge Installer, matching the name in `installer.json`
  - `modpack.zip`: A zip containing the modpack in zip-bomb form (the root of the zip is the root of the instance, i.e. where options.txt goes)
  - `jre/`: An JRE for the installer to use, extracted into this folder (so javaw is at `jre/bin/javaw.exe`)
- Select all files within the bundle folder and select 7-zip -> Add to bundle.7z

## Build the exe

- Move the bundle.7z file you just created into another folder
- In the same location, place the header.sfx and config.txt files from this repository
- Edit config.txt if necessary to point to the correct file locations
- In cmd, within the folder, run `copy /b header.sfx + config.txt + bundle.7z <output file name>.exe`

Done!

# installer.json

The Installer is configured via a JSON file, which can contain the following:
```json
{
	"forgeInstallerPath": "<forge installer filename>",
	"profileName": "<name that will be used in the vanilla launcher for the created profile>",
	"gameDir": "<what to call the installation folder by default, user can change>",
	"serverIp": "<optional, the IP of a server to autoconnect to>"
}
```

# JRE
If you don't want to include an entire JRE, you can use `jlink` to build a stripped-down JRE that only contains the
required modules:
- Use `jdeps` to get the modules needed by the JARfiles (e.g. `jdeps installer.jar`)
- Use `jlink` to build a JRE with those needed modules:
  - `jlink --add-modules java.base,java.desktop,java.management,java.logging,java.sql --strip-debug --no-man-pages --no-header-files --compress=2 --output jre`