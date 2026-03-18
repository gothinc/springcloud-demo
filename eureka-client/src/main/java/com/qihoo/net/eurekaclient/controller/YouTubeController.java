package com.qihoo.net.eurekaclient.controller;

import com.qihoo.net.eurekaclient.model.YouTubeVideo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YouTube 热门视频接口
 * GET /youtube/trending?regionCode=US&maxResults=5
 */
@RestController
@RequestMapping("/youtube")
public class YouTubeController {

    private static final String YOUTUBE_API_URL =
            "https://www.googleapis.com/youtube/v3/videos" +
            "?part=snippet,statistics" +
            "&chart=mostPopular" +
            "&regionCode={regionCode}" +
            "&maxResults={maxResults}" +
            "&key={apiKey}";

    @Value("${youtube.api.key:YOUR_YOUTUBE_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取今日 YouTube 热门 Top N 视频
     *
     * @param regionCode 地区代码，默认 US
     * @param maxResults 返回数量，默认 5
     * @return 视频列表
     */
    @GetMapping("/trending")
    public ResponseEntity<List<YouTubeVideo>> getTrendingVideos(
            @RequestParam(defaultValue = "US") String regionCode,
            @RequestParam(defaultValue = "5") int maxResults) {

        Map response = restTemplate.getForObject(
                YOUTUBE_API_URL,
                Map.class,
                regionCode,
                maxResults,
                apiKey
        );

        List<YouTubeVideo> videos = new ArrayList<>();

        if (response != null && response.containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            for (Map<String, Object> item : items) {
                String videoId = (String) item.get("id");

                Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                String title = (String) snippet.get("title");
                String channelTitle = (String) snippet.get("channelTitle");
                String publishedAt = (String) snippet.get("publishedAt");

                Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                Map<String, Object> highThumb = (Map<String, Object>) thumbnails.get("high");
                String thumbnailUrl = highThumb != null ? (String) highThumb.get("url") : "";

                Map<String, Object> statistics = (Map<String, Object>) item.get("statistics");
                long viewCount = parseLong(statistics.get("viewCount"));
                long likeCount = parseLong(statistics.get("likeCount"));

                videos.add(new YouTubeVideo(videoId, title, channelTitle,
                        publishedAt, thumbnailUrl, viewCount, likeCount));
            }
        }

        return ResponseEntity.ok(videos);
    }

    private long parseLong(Object value) {
        if (value == null) return 0L;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
