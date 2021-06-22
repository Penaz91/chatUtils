# chatUtils for Spigot 1.12

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) ![Status:Archived](https://img.shields.io/badge/Status-Archived-inactive)

chatUtils is a small plugin with the essentials to control chat flow.
This plugin doesn't include muting features or channels, but just some things
I personally felt could be useful in a decently-sized minecraft server.

### Commands

- **/chatutils | /cu**: Shows the help screen
- **/cu cc**: Clears chat.
- **/cu ld**: Puts the chat in lockdown
- **/cu kw | /cu keywords**: Show the available keywords
- **@playername**: Typing this in chat makes <playername> hear a sound to let them
know they have been mentioned or that someone needs their attention.
- **!keyword**: Typing this, !keyword will be replaced according to the configuration,
 for instance !website could be replaced with the server website URL.

### Other features

- **Capital letters limitation**: This will warn and eventually downcase all sentences
that go over a certain amount of capital letters.
- **Chat Cooldown**: This will avoid spambots to make chat impossible to read, by
putting a cooldown on all messages.
- **Automatic timed broadcasts**: This will allow you to send messages automatically in chat, for example to inform players of some server features

### Permissions

- **chatutils.use**: Allows the usage of the plugin via the /chatutils Commands
- **chatutils.cc**: Permission to clear chat
- **chatutils.bypasscc**: Chat clears can be pretty annoying when working with
prism or other logging tools, whoever holds this permission won't see their chat
cleared.
- **chatutils.ld**: Permission to put and lift chat lockdowns
- **chatutils.bypassld**: Permission to talk when chat Lockdown is in place

### Disclaimer
The code is not commented, and it's probably pretty ugly too. Be careful, eyes might bleed.

##### License and other stuff
I'm fine with commercial usage and modification of this plugin, as long as I'm quoted
as the original author of this plugin.
