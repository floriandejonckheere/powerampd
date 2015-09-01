# PoweraMPD

PoweraMPD is an Android application that acts as a virtual MPD server interface to [Poweramp](http://powerampapp.com/). It is written to act exactly like the official MPD server following the [MPD protocol](http://www.musicpd.org/doc/protocol/). As such it should work with any MPD client.

## MPD Protocol differences

There are a few differences in protocol response with the official MPD server. These differences are mere compliance to the specification rather than the code, and should introduce no incompatabilities.

- Error handling: the command resulting error is always printed
- Encoding: special characters are handled better

## Roadmap

**v0.1**

- Basic MPD protocol (playback control, song information)

**v0.2**

- ZeroConf support
- Extended MPD protocol (library)
- Access control

**v0.3**

- Full MPD protocol compliance
- Possibly streaming endpoint
