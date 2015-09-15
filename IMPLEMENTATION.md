# Implementation

The current status of all MPD protocol commands

| Command                 | Status |
|-------------------------|--------|
| `add`                     |  |
| `addid`                   |  |
| `addtagid`                |  |
| `channels`                |  |
| `clear`                   |  |
| `clearerror`              |  |
| `cleartagid`              |  |
| `close`                   | Implemented |
| `command_list_begin`      |  |
| `command_list_end`        |  |
| `command_list_ok_begin`   |  |
| `commands`                |  |
| `config`                  |  |
| `consume`                 | **Not supported in Poweramp** |
| `count`                   |  |
| `crossfade`               |  |
| `currentsong`             | Partially implemented |
| `decoders`                |  |
| `delete`                  |  |
| `deleteid`                |  |
| `disableoutput`           |  |
| `enableoutput`            |  |
| `find`                    |  |
| `findadd`                 |  |
| `idle`                    |  |
| `kill`                    |  |
| `list`                    |  |
| `listall`                 |  |
| `listallinfo`             |  |
| `listfiles`               |  |
| `listmounts`              |  |
| `listneighbors`           |  |
| `listplaylist`            |  |
| `listplaylistinfo`        |  |
| `listplaylists`           |  |
| `load`                    |  |
| `lsinfo`                  |  |
| `mixrampdb`               |  |
| `mixrampdelay`            |  |
| `mount`                   |  |
| `move`                    |  |
| `moveid`                  |  |
| `next`                    | Implemented |
| `noidle`                  |  |
| `notcommands`             |  |
| `outputs`                 |  |
| `password`                | Implemented |
| `pause`                   | Implemented |
| `ping`                    |  |
| `play`                    |  |
| `playid`                  |  |
| `playlist`                |  |
| `playlistadd`             |  |
| `playlistclear`           |  |
| `playlistdelete`          |  |
| `playlistfind`            |  |
| `playlistid`              |  |
| `playlistinfo`            |  |
| `playlistmove`            |  |
| `playlistsearch`          |  |
| `plchanges`               |  |
| `plchangesposid`          |  |
| `previous`                | Implemented |
| `prio`                    |  |
| `prioid`                  |  |
| `random`                  |  |
| `rangeid`                 |  |
| `readcomments`            |  |
| `readmessages`            |  |
| `rename`                  |  |
| `repeat`                  |  |
| `replay_gain_mode`        |  |
| `replay_gain_status`      |  |
| `rescan`                  |  |
| `rm`                      |  |
| `save`                    |  |
| `search`                  |  |
| `searchadd`               |  |
| `searchaddpl`             |  |
| `seek`                    |  |
| `seekcur`                 |  |
| `seekid`                  |  |
| `sendmessage`             |  |
| `setvol`                  | Implemented |
| `shuffle`                 |  |
| `single`                  |  |
| `stats`                   |  |
| `status`                  | Partially implemented |
| `sticker`                 |  |
| `stop`                    | Implemented |
| `subscribe`               |  |
| `swap`                    |  |
| `swapid`                  |  |
| `tagtypes`                |  |
| `toggleoutput`            |  |
| `unmount`                 |  |
| `unsubscribe`             |  |
| `update`                  |  |
| `urlhandlers`             |  |
| `volume`                  | Deprecated (see `setvol`) |
