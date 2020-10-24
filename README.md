# OSMPlayer
[![](https://jitpack.io/v/OscarMyoMin/OSMPlayer.svg)](https://jitpack.io/#OscarMyoMin/OSMPlayer)

OSMPlayer is an ExoPlayer2 wrapper that simplifies streaming videos with subtitles.


## Original Project
[Streamer](https://github.com/SniperDW/Streamer)

### Usage

1. Add it in your root build.gradle at the end of repositories:

        allprojects {
            repositories {
                jcenter()
                maven { url "https://jitpack.io" }
            }
        }
        
2. Step 2. Add the dependency
    
        implementation 'com.github.OscarMyoMin:OSMPlayer:1.0'
        
3. Use the library

        OSMPlayer.play(context, videoTitle, videoURL, subtitleURL);
        
        
