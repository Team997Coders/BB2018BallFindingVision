# FocusUF project

This project uses uses the DirectShow API to switch a Microsoft LifeCam 5000 to manual focus mode.

The software that comes with the LifeCam no longer works on the latest version of Windows 10 Creators edition.

The camera has an annoying habit of a slow and repeated autofocus sequence, which messes up vision processing.

This project only works on Windows, and only for the LifeCam 5000 camera.

It would be great to unify this project to work under all platforms, since this requirement exists everywhere.

This project does not use gradle for building.  To build, do `dotnet build`.  To run, do `dotnet run`.

# Credits

This project was based on the work [here](https://github.com/anotherlab/FocusUF) and [Chris Miller's blog](https://rajapet.com/2018/04/05/focusuf-or-how-to-turn-off-the-autofocus-setting-of-the-lifecam-hd-5000-webcam/) but was converted to build with VSCode.  Thanks!