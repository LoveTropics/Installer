# Building an Installer

### Prerequisites

- Windows
- 7-zip

## Create a bundle

- Create a bundle folder, copy in the modpack in zip format
- Download desired forge installer, place in bundle folder
- Copy a LTInstaller jar to the bundle folder
- Download a JRE for the installer to use, extract it and place it inside a folder in the bundle
- Create a file called `installer.json`
    - Inside that file, fill in the following json config with your own values:
  
```json
{
	"forgeInstallerPath": "<forge installer filename>",
	"profileName": "<name that will be used in the vanilla launcher for the created profile>",
	"gameDir": "<what to call the installation folder by default, user can change>",
	"serverIp": "<optional, the IP of a server to autoconnect to>"
}
```

Finally, select all files within the bundle folder and select 7-zip -> Add to bundle.7z

## Build the exe

- Move the bundle.7z file you just created into another folder
- In the same location, place the header.sfx and config.txt files from this repository
- Edit config.txt if necessary to point to the correct file locations
- In cmd/powershell, within the folder, run `copy /b header.sfx + config.txt + bundle.7z <output file name>.exe`

Done!