# WifiKeyboard-Android

----
# Configuration

- 1-  Install server: https://github.com/Stefifox/WifiKeyboard-Server
- 2-  Read your computer IP (for windows use ipconfig in CMD)
- 3-  Start the server
- 4-  Insert IP and PORT on application
- 5-  Press CONNECT

# Personalizations
To make new button you can edit config.json file, in the future we will add an simple web editor

For apply changes save json file and restart server

example of button
´´´´
{
    "id" : 0,
    "name" : "Test",
    "key" : "k"
}
´´´´

- id: The id of button, must be unique
- name: The text on the button in application
- key: The Keycode that the button send when it pressed

keycodes f13 to f24 is special keys

you can add virtually infinite buttons