package com.pfplaybackend.api.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Demo track constants for initialization
 */
public class DemoTrackConstants {

    private static final Random RANDOM = new Random();

    @Getter
    @AllArgsConstructor
    public static class TrackInfo {
        private final String linkId;
        private final String thumbnailImage;
        private final String duration;
        private final String name;
    }

    public static final List<TrackInfo> DEMO_TRACKS = Arrays.asList(
            new TrackInfo("Qe8fa4b5xNU", "https://i.ytimg.com/vi/Qe8fa4b5xNU/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLAb_Fh3foLbv029V1wlARZiPdzoaA", "3:46", "화사 (HWASA) - 'Good Goodbye' MV"),
            new TrackInfo("qE35JN1ogeI", "https://i.ytimg.com/vi/qE35JN1ogeI/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLDkwftM18YKuxuHW8VLzrS7wNqQbw", "4:23", "다비치 (DAVICHI) '타임캡슐' Official Music Video"),
            new TrackInfo("NbKH4iZqq1Y", "https://i.ytimg.com/vi/NbKH4iZqq1Y/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLA1rSJIWc01s2gMrXlXNoKMTtRF-w", "4:05", "Drowning (Drowning)"),
            new TrackInfo("EmeW6li6bbo", "https://i.ytimg.com/vi/EmeW6li6bbo/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLA--fybOUjmXqm00wKTJPU_W7m_vg", "3:15", "NMIXX(엔믹스) \"Blue Valentine\" M/V"),
            new TrackInfo("yebNIHKAC4A", "https://i.ytimg.com/vi/yebNIHKAC4A/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLDE7CMSk9kdgrpEtZR-UMNdhd9VxQ", "3:19", "\"Golden\" Official Lyric Video | KPop Demon Hunters | Sony Animation"),
            new TrackInfo("eKtSbXHaqLM", "https://i.ytimg.com/vi/eKtSbXHaqLM/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLAotwHtcAKxD2PnyJZK99rDYPwdFw", "2:45", "한로로 - 사랑하게 될 거야 [가사 | Lyrics]"),
            new TrackInfo("OgEwJ8a1OoY", "https://i.ytimg.com/vi/OgEwJ8a1OoY/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLAy35NTPAEAhLFKNH05qMpAHXigpw", "3:23", "ALLDAY PROJECT - 'ONE MORE TIME' M/V"),
            new TrackInfo("TvVtYaqCni8", "https://i.ytimg.com/vi/TvVtYaqCni8/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLBslMk7tlB6C9-9wpavWfc_9qO_bA", "3:19", "LE SSERAFIM (르세라핌) 'SPAGHETTI (feat. j-hope of BTS)' OFFICIAL MV"),
            new TrackInfo("19oT04OuBhg", "https://i.ytimg.com/vi/19oT04OuBhg/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLBR0YUHGOpjoKa5enjIhov8v1fUMw", "3:50", "이찬혁 (LEE CHANHYUK) - '멸종위기사랑' M/V"),
            new TrackInfo("1mZ-hoIvEx8", "https://i.ytimg.com/vi/1mZ-hoIvEx8/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLD3Ex5rwjxsg28oc9erXRlc1cBHCQ", "3:57", "[MV] Woody(우디) _ Sadder Than Yesterday(어제보다 슬픈 오늘)"),
            new TrackInfo("EhKaflRVBpY", "https://i.ytimg.com/vi/EhKaflRVBpY/hq720.jpg?sqp=-oaymwE9COgCEMoBSFryq4qpAy8IARUAAAAAGAElAADIQj0AgKJDeAHwAQH4Af4JgALQBYoCDAgAEAEYZSBlKGUwDw==&rs=AOn4CLCKTMn-LtdrnBZs97uqQeh7ytJkzA", "3:55", "노아주다 (noahjooda) - 힙합보단 사랑, 사랑보단 돈 (Feat. 베이식) 가사(lyrics)"),
            new TrackInfo("mHe3amVvtVo", "https://i.ytimg.com/vi/mHe3amVvtVo/hq720.jpg?sqp=-oaymwE9COgCEMoBSFryq4qpAy8IARUAAAAAGAElAADIQj0AgKJDeAHwAQH4Af4JgALQBYoCDAgAEAEYfyATKCswDw==&rs=AOn4CLDEcxmp24Y_Q8toY6g_fyhasZjCQg", "3:29", "EXO(엑소) - 첫 눈 [가사/Lyrics]"),
            new TrackInfo("5ZB9JLfIw_Q", "https://i.ytimg.com/vi/5ZB9JLfIw_Q/hq720.jpg?sqp=-oaymwEjCOgCEMoBSFryq4qpAxUIARUAAAAAGAElAADIQj0AgKJDeAE=&rs=AOn4CLCfoDYv283W2W3ZJ3ydbuXPgc-JYQ", "4:14", "fromis_9 (프로미스나인) '하얀 그리움' MV")
    );

    /**
     * Get random track from the demo tracks list
     */
    public static TrackInfo getRandomTrack() {
        return DEMO_TRACKS.get(RANDOM.nextInt(DEMO_TRACKS.size()));
    }

    /**
     * Get track by index
     */
    public static TrackInfo getTrackByIndex(int index) {
        if (index < 0 || index >= DEMO_TRACKS.size()) {
            throw new IllegalArgumentException("Invalid track index: " + index);
        }
        return DEMO_TRACKS.get(index);
    }
}
