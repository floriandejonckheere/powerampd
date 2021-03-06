# PoweraMPD

PoweraMPD is an Android application that acts as a virtual MPD server interface to [Poweramp](http://powerampapp.com/). It is written to act as close to the official MPD server as possible following the [MPD protocol](http://www.musicpd.org/doc/protocol/). If you notice any incompatibilities with any MPD clients, please file an issue.

## MPD Protocol differences

There are a few differences in how the application handles certain situations. This is usually due to Poweramp not supporting an MPD feature or vice-versa.

- Consume mode is not supported in Poweramp
- MPD's random mode maps onto Poweramp's shuffle mode
- MPD's repeat and single modes map onto Poweramp's repeat mode
- Single mode is currently not supported
- Poweramp default shuffle/repeat mode can be set in preferences
- Poweramp provides no API for crossfade options

Poweramp repeat and single mode mapping is as follows:

```
MPD			Poweramp

(repeat, single)	mode

(0, 0)			REPEAT_NONE
(0, 1)			Stop playback after current song
(1, 0)			User defined preference (REPEAT_ON or REPEAT_ADVANCE)
(1, 1)			REPEAT_SONG

```

## License

The application and its source code are licensed under the MIT license. A copy of the license is included in the `LICENSE.md` file. All image assets are generated using the [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/), and are licensed under the [Creative Commons Attribution 3.0 Unported](http://creativecommons.org/licenses/by/3.0/legalcode) license. The [Poweramp API library](https://github.com/maxmpz/powerampapi/) is needed for the application to function, and is licensed under a custom license available in the library directory. 

## Permissions

The applications needs the following permissions:

- `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`: Start service when wifi connected
- `CHANGE_WIFI_MULTICAST_STATE`: Multicast resolution for mDNS Service Discovery
- `INTERNET`: IP communication

## Building

- Download the [Poweramp API library](https://github.com/maxmpz/powerampapi/tree/master/poweramp_api_lib) to the appropriate directory 
(`app/src/main/java/com/maxmpz/poweramp`)

## Contributing

At the moment only the missing commands need to be written. See `IMPLEMENTATION.md` for a summary of implemented commands. If you want to contribute, here are a few pointers:

- Commands are classes that extend the `command/Command` class. Constructors should pass the unparsed command line (`cmdline`) if needed and the permission level (`Permission.PERMISSION_*`). All command logic should be done in the overriden `executeCommand` method, including checking the number and validity of arguments (see `Command.checkArguments`). A `protocol.Connection` object is available as a handle to the client-server connection, and is to be used for any communication with the client and/or Poweramp system. Commands are grouped in categories according to the MPD protocol specification.

## Roadmap

**v0.1**

- MPD Protocol parsing framework
	- Implemented commands: close, next, previous, pause

**v0.2**

- MPD Protocol parsing framework v2
	- Implemented commands: close, next, previous, pause, stop, setvol, ping, password, command_list_begin, command_list_ok_begin, command_list_end
- Access control
- ZeroConf support

**v0.3**

- Full playback control

**v0.4**

- Basic playlist manipulation

**v0.5**

- Extended MPD protocol (library access)

**v1.0**

- Full MPD protocol compliance
