# Implementation

Deprecated commands are implemented to maintain compatibility.

## Meta

| Command                   | Status |
|---------------------------|--------|
| `command_list_begin`      |  |
| `command_list_end`        |  |
| `command_list_ok_begin`   |  |

## Status

| Command                   | Status |
|---------------------------|--------|
| `clearerror`              |  |
| `currentsong`             | Partially implemented |
| `idle`                    |  |
| `noidle`                  |  |
| `status`                  | Partially implemented |
| `stats`                   |  |

## Playback options

| Command                   | Status |
|---------------------------|--------|
| `consume`                 | **Not supported in Poweramp** |
| `crossfade`               |  |
| `mixrampdb`               |  |
| `mixrampdelay`            |  |
| `random`                  |  |
| `repeat`                  |  |
| `setvol`                  | Implemented |
| `single`                  |  |
| `replay_gain_mode`        |  |
| `replay_gain_status`      |  |
| `volume`                  | Deprecated (see `setvol`) |

## Playback

| Command                   | Status |
|---------------------------|--------|
| `next`                    | Implemented |
| `pause`                   | Implemented |
| `play`                    |  |
| `playid`                  |  |
| `previous`                | Implemented |
| `seek`                    |  |
| `seekid`                  |  |
| `seekcur`                 |  |
| `stop`                    | Implemented |

## Current playlist

| Command                   | Status |
|---------------------------|--------|
| `add`                     |  |
| `addid`                   |  |
| `clear`                   |  |
| `delete`                  |  |
| `deleteid`                |  |
| `move`                    |  |
| `moveid`                  |  |
| `playlist`                |  |
| `playlistfind`            |  |
| `playlistid`              |  |
| `playlistinfo`            |  |
| `playlistsearch`          |  |
| `plchanges`               |  |
| `plchangesposid`          |  |
| `prio`                    |  |
| `prioid`                  |  |
| `rangeid`                 |  |
| `shuffle`                 |  |
| `swap`                    |  |
| `swapid`                  |  |
| `addtagid`                |  |
| `cleartagid`              |  |

## Stored playlist

| Command                   | Status |
|---------------------------|--------|
| `listplaylist`            |  |
| `listplaylistinfo`        |  |
| `listplaylists`           |  |
| `load`                    |  |
| `playlistadd`             |  |
| `playlistclear`           |  |
| `playlistdelete`          |  |
| `playlistmove`            |  |
| `rename`                  |  |
| `rm`                      |  |
| `save`                    |  |

## Database

| Command                   | Status |
|---------------------------|--------|
| `count`                   |  |
| `find`                    |  |
| `findadd`                 |  |
| `list`                    |  |
| `listall`                 |  |
| `listallinfo`             |  |
| `listfiles`               |  |
| `lsinfo`                  |  |
| `readcomments`            |  |
| `search`                  |  |
| `searchadd`               |  |
| `searchaddpl`             |  |
| `update`                  |  |
| `rescan`                  |  |

## Mounts

| Command                   | Status |
|---------------------------|--------|
| `mount`                   |  |
| `unmount`                 |  |
| `listmounts`              |  |
| `listneighbors`           |  |

## Stickers

| Command                   | Status |
|---------------------------|--------|
| `sticker`                 |  |

## Connection

| Command                   | Status |
|---------------------------|--------|
| `close`                   | Implemented |
| `kill`                    |  |
| `password`                | Implemented |
| `ping`                    |  |

## Audio devices

| Command                   | Status |
|---------------------------|--------|
| `disableoutput`           |  |
| `enableoutput`            |  |
| `toggleoutput`            |  |
| `outputs`                 |  |

## Reflection

| Command                   | Status |
|---------------------------|--------|
| `config`                  |  |
| `commands`                |  |
| `notcommands`             |  |
| `tagtypes`                |  |
| `urlhandlers`             |  |
| `decoders`                |  |

## Client to client

| Command                   | Status |
|---------------------------|--------|
| `subscribe`               |  |
| `unsubscribe`             |  |
| `channels`                |  |
| `readmessages`            |  |
| `sendmessage`             |  |