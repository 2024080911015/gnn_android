package com.example.gnn.data.api

import com.example.gnn.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<AuthResponse>

    @GET("community")
    suspend fun getCommunity(): Response<CommunityResponse>

    // Recommendations
    @GET("tuijian")
    suspend fun getRecommendations(
        @Query("id") uid: Int,
        @Query("mode") mode: String? = null,
        @Query("community") community: String? = null
    ): Response<RecommendationResponse>

    @GET("social/report")
    suspend fun getSocialReport(@Query("id") uid: Int): Response<SocialReportResponse>

    // Users
    @GET("user")
    suspend fun getUser(@Query("id") uid: Int): Response<UserDetailResponse>

    @GET("api/users/avatars")
    suspend fun getUserAvatars(): Response<UserAvatarsResponse>

    @GET("api/search_users")
    suspend fun searchUsers(@Query("q") query: String): Response<UserSearchResponse>

    @POST("api/user/update")
    suspend fun updateProfile(@Body data: Map<String, String?>): Response<GenericResponse>

    @Multipart
    @POST("api/user/upload_avatar")
    suspend fun uploadAvatar(@Part avatar: okhttp3.MultipartBody.Part): Response<GenericResponse>

    // Social
    @GET("following")
    suspend fun getFollowing(@Query("id") uid: Int): Response<FollowListResponse>

    @GET("followers")
    suspend fun getFollowers(@Query("id") uid: Int): Response<FollowListResponse>

    @POST("api/social/toggle_follow")
    suspend fun toggleFollow(@Body data: Map<String, String>): Response<GenericResponse>

    @GET("social/stats")
    suspend fun getSocialStats(): Response<SocialStatsResponse>

    // Messages
    @GET("api/message/inbox")
    suspend fun getInbox(): Response<InboxResponse>

    @POST("api/message/send")
    suspend fun sendMessage(@Body data: Map<String, String>): Response<GenericResponse>

    // Activity
    @GET("api/activity/list")
    suspend fun listActivities(): Response<ActivityListResponse>

    @POST("api/activity/join")
    suspend fun joinActivity(@Body data: Map<String, String>): Response<GenericResponse>

    @GET("api/activity/my")
    suspend fun getMyActivities(): Response<MyActivitiesResponse>

    @POST("api/activity/create")
    suspend fun createActivity(@Body data: Map<String, Any>): Response<GenericResponse>
}
