package com.example.graduationproject.data;

import com.example.graduationproject.models.YouTubeResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeApiService {
    @GET("youtube/v3/search")
    Call<YouTubeResponse> getEmbeddableVideos(
        @Query("part") String part,
        @Query("q") String query,
        @Query("type") String type,
        @Query("videoEmbeddable") String videoEmbeddable,
        @Query("maxResults") int maxResults,
        @Query("key") String apiKey
    );

    @GET("youtube/v3/videos")
    Call<YouTubeResponse> getVideoDetails(
        @Query("part") String part,
        @Query("id") String ids,
        @Query("key") String apiKey
    );
}
