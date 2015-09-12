# PoweraMPD

PoweraMPD is an Android application that acts as a virtual MPD server interface to [Poweramp](http://powerampapp.com/). It is written to act exactly like the official MPD server following the [MPD protocol](http://www.musicpd.org/doc/protocol/). As such it should work with any MPD client.

## MPD Protocol differences

There are a few differences in protocol response with the official MPD server. These differences are mere compliance to the specification rather than the code, and should introduce no incompatabilities.

- Error handling: the command resulting error is always printed
- Encoding: special characters are handled better

Poweramp repeat mode mapping is as follows:

```
Poweramp                    MPD

REPEAT_NONE                 repeat: 0
                            single: 0
REPEAT_ON                   repeat: 1
                            single: 0
REPEAT_ADVANCE              repeat: 1
                            single: 0
REPEAT_SONG                 repeat: 1
                            single: 1
```

## License

The application and its source code are licensed under the MIT license. A copy of the license is included in the `LICENSE.md` file. All image assets are generated using the [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/), and are licensed under the [Creative Commons Attribution 3.0 Unported](http://creativecommons.org/licenses/by/3.0/legalcode) license. The [Poweramp API library](https://github.com/maxmpz/powerampapi/) is needed for the application to function, and is licensed under a custom license available in the library directory. 

## Building

- Download the [Poweramp API library](https://github.com/maxmpz/powerampapi/tree/master/poweramp_api_lib) to the appropriate directory 
(`app/src/main/java/com/maxmpz/poweramp`)

## Roadmap

**v0.1**

- MPD Protocol parser
    - Implemented commands: close, next, previous, pause

**v0.2**

- Basic MPD protocol (playback control, song information)
- Access control

**v0.3**

- ZeroConf support

**v0.4**

- Extended MPD protocol (library access)

**v1.0**

- Full MPD protocol compliance
