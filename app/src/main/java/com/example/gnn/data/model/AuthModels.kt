package com.example.gnn.data.model

// Common
data class GenericResponse(
    val status: String,
    val message: String
)

// Auth
data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val gender: String? = null,
    val grade: String? = null,
    val major: String? = null,
    val hobbies: String? = null,
    val tags: String? = null
)

data class AuthResponse(
    val status: String,
    val logged_in: Boolean? = null,
    val message: String? = null,
    val data: UserData? = null
)

data class UserData(
    val uid: Int,
    val username: String,
    val info: String? = null
)

data class CommunityResponse(
    val status: String,
    val communities: List<String>
)

// Recommendations
data class RecommendationResponse(
    val student_id: Int,
    val mode: String,
    val student_info: String,
    val recommend_friends: List<RecommendedUser>
)

data class RecommendedUser(
    val id: Int,
    val username: String,
    val info: String
)

data class SocialReportResponse(
    val status: SocialReportStatus,
    val distribution: List<CommunityDistribution>,
    val advice: String
)

data class SocialReportStatus(
    val title: String,
    val description: String,
    val total_connections: Int
)

data class CommunityDistribution(
    val name: String,
    val percent: String,
    val count: Int
)

// User Detail
data class UserDetailResponse(
    val student_id: Int,
    val username: String,
    val student_info: String,
    val avatar: String? = null,
    val signature: String? = null,
    val status: String? = null
)

data class UserSearchResponse(
    val status: String,
    val results: List<RecommendedUser>
)

// Social
data class FollowListResponse(
    val student_id: Int,
    val count: Int? = null,
    val following: List<RecommendedUser>? = null,
    val followers: List<RecommendedUser>? = null
)

data class SocialStatsResponse(
    val total_users: Int,
    val total_follows: Int,
    val average_follows: Double,
    val most_popular_users: List<PopularUser>
)

data class PopularUser(
    val id: Int,
    val username: String,
    val followers_count: Int
)

// Inbox
data class InboxResponse(
    val status: String,
    val data: List<MessageItem>
)

data class MessageItem(
    val message_id: Int,
    val sender_id: Int,
    val sender_name: String,
    val avatar: String?,
    val content: String,
    val created_at: String,
    val is_read: Boolean
)

// Activity
data class ActivityListResponse(
    val status: String,
    val data: List<ActivityItem>
)

data class MyActivitiesResponse(
    val status: String,
    val launched: List<ActivityItem>,
    val joined: List<ActivityItem>
)

data class ActivityItem(
    val id: Int,
    val title: String,
    val nature: String,
    val description: String,
    val publisher_id: Int,
    val publisher_name: String,
    val total_capacity: Int,
    val deadline: String,
    val member_count: Int,
    val match_score: Int,
    val path_text: String,
    val my_status: Int
)
