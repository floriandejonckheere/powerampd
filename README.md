# PoweraMPD

PoweraMPD is an Android application that acts as a virtual MPD server interface to [Poweramp](http://powerampapp.com/). It is written to act exactly like the official MPD server following the [MPD protocol](http://www.musicpd.org/doc/protocol/). As such it should work with any MPD client.

## MPD Protocol differences

There are a few differences in protocol response with the official MPD server. These differences are mere compliance to the specification rather than the code, and should introduce no incompatabilities.

- Error handling: the command resulting error is always printed
- Encoding: special characters are handled better

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
