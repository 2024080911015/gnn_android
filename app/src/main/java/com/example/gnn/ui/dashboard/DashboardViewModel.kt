package com.example.gnn.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gnn.data.api.RetrofitClient
import com.example.gnn.data.model.*
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val api = RetrofitClient.instance

    var currentUid: Int = 0
    var currentUsername: String = ""

    // Profile State
    var userDetail by mutableStateOf<UserDetailResponse?>(null)
    
    // Recommendations State
    var recommendations by mutableStateOf<List<RecommendedUser>>(emptyList())
    var socialReport by mutableStateOf<SocialReportResponse?>(null)
    var communities by mutableStateOf<List<String>>(emptyList())
    var selectedCommunity by mutableStateOf("")
    var selectedMode by mutableStateOf("social")
    
    // Social State
    var followingList by mutableStateOf<List<RecommendedUser>>(emptyList())
    var followersList by mutableStateOf<List<RecommendedUser>>(emptyList())
    var socialStats by mutableStateOf<SocialStatsResponse?>(null)
    
    // Activity State
    var activities by mutableStateOf<List<ActivityItem>>(emptyList())
    var myLaunchedActivities by mutableStateOf<List<ActivityItem>>(emptyList())
    var myJoinedActivities by mutableStateOf<List<ActivityItem>>(emptyList())
    
    // Inbox State
    var inboxMessages by mutableStateOf<List<MessageItem>>(emptyList())
    
    // Search State
    var searchResults by mutableStateOf<List<RecommendedUser>>(emptyList())

    // Feedback State
    var toastMessage by mutableStateOf<String?>(null)

    // User Detail Dialog State
    var currentUserToShow by mutableStateOf<UserDetailResponse?>(null)

    fun init(uid: Int, username: String) {
        currentUid = uid
        currentUsername = username
        refreshProfile()
        loadSocialData() 
        loadRecommendations()
    }

    fun isFollowing(targetId: Int): Boolean {
        return followingList.any { it.id == targetId }
    }

    fun showUserDetail(targetUid: Int) {
        viewModelScope.launch {
            try {
                val response = api.getUser(targetUid)
                if (response.isSuccessful) {
                    currentUserToShow = response.body()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun closeUserDetail() {
        currentUserToShow = null
    }

    fun refreshProfile() {
        viewModelScope.launch {
            try {
                val response = api.getUser(currentUid)
                if (response.isSuccessful) {
                    userDetail = response.body()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun loadRecommendations() {
        viewModelScope.launch {
            try {
                val recResponse = api.getRecommendations(currentUid, selectedMode, if (selectedCommunity.isEmpty()) null else selectedCommunity)
                if (recResponse.isSuccessful) {
                    val body = recResponse.body()
                    val raw = body?.recommend_friends ?: emptyList()
                    android.util.Log.i("GNN", "推荐API返回: mode=${body?.mode}, raw_size=${raw.size}, currentUid=$currentUid")
                    recommendations = raw
                    android.util.Log.i("GNN", "过滤后推荐: ${recommendations.size}")
                } else {
                    android.util.Log.w("GNN", "推荐API失败: code=${recResponse.code()}, msg=${recResponse.message()}")
                }
                // 冷启动兜底：推荐API返回0人时，用热门用户替代
                if (recommendations.isEmpty()) {
                    android.util.Log.i("GNN", "推荐为空，尝试热门用户兜底")
                    try {
                        val statsResponse = api.getSocialStats()
                        if (statsResponse.isSuccessful) {
                            val popular = statsResponse.body()?.most_popular_users ?: emptyList()
                            if (popular.isNotEmpty()) {
                                recommendations = popular
                                    .filter { it.id != currentUid }
                                    .map { RecommendedUser(id = it.id, username = it.username, info = "${it.followers_count} 人关注") }
                                android.util.Log.i("GNN", "冷启动兜底: 加载了 ${recommendations.size} 个热门用户")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("GNN", "热门用户兜底失败", e)
                    }
                }
                val reportResponse = api.getSocialReport(currentUid)
                if (reportResponse.isSuccessful) {
                    socialReport = reportResponse.body()
                } else {
                    android.util.Log.w("GNN", "社交报告加载失败: code=${reportResponse.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("GNN", "loadRecommendations 异常", e)
            }
        }
    }

    fun loadCommunities() {
        viewModelScope.launch {
            try {
                val response = api.getCommunity()
                if (response.isSuccessful) {
                    communities = response.body()?.communities ?: emptyList()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun loadSocialData() {
        viewModelScope.launch {
            try {
                val following = api.getFollowing(currentUid)
                if (following.isSuccessful) followingList = following.body()?.following ?: emptyList()
                
                val followers = api.getFollowers(currentUid)
                if (followers.isSuccessful) followersList = followers.body()?.followers ?: emptyList()
                
                val stats = api.getSocialStats()
                if (stats.isSuccessful) socialStats = stats.body()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun loadActivities() {
        viewModelScope.launch {
            try {
                val list = api.listActivities()
                if (list.isSuccessful) activities = list.body()?.data ?: emptyList()
                
                val my = api.getMyActivities()
                if (my.isSuccessful) {
                    myLaunchedActivities = my.body()?.launched ?: emptyList()
                    myJoinedActivities = my.body()?.joined ?: emptyList()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun loadInbox() {
        viewModelScope.launch {
            try {
                val response = api.getInbox()
                if (response.isSuccessful) inboxMessages = response.body()?.data ?: emptyList()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            try {
                val response = api.searchUsers(query)
                if (response.isSuccessful) searchResults = response.body()?.results ?: emptyList()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun toggleFollow(targetId: Int, onComplete: () -> Unit = {}) {
        val isCurrentlyFollowing = isFollowing(targetId)
        val action = if (isCurrentlyFollowing) "unfollow" else "follow"

        // === 乐观更新：立即修改本地状态，保证 UI 即时响应 ===
        if (action == "follow") {
            if (!followingList.any { it.id == targetId }) {
                val placeholder = RecommendedUser(
                    id = targetId,
                    username = "加载中...",
                    info = ""
                )
                followingList = followingList + placeholder
            }
        } else {
            followingList = followingList.filter { it.id != targetId }
        }

        viewModelScope.launch {
            try {
                val response = api.toggleFollow(mapOf("target_id" to targetId.toString(), "action" to action))
                if (response.isSuccessful && response.body()?.status == "success") {
                    toastMessage = if (action == "follow") "关注成功" else "已取消关注"
                    if (action == "follow") {
                        // 拉取目标用户的真实资料替换占位符
                        val userResp = api.getUser(targetId)
                        if (userResp.isSuccessful) {
                            val detail = userResp.body()
                            if (detail != null) {
                                followingList = followingList.map {
                                    if (it.id == targetId) RecommendedUser(
                                        id = detail.student_id,
                                        username = detail.username,
                                        info = detail.student_info
                                    ) else it
                                }
                            }
                        }
                    }
                } else {
                    // 操作失败，回滚本地状态
                    toastMessage = response.body()?.message ?: "操作失败"
                    loadSocialData()
                }
                onComplete()
            } catch (e: Exception) {
                // 网络错误，回滚本地状态
                toastMessage = "网络错误"
                loadSocialData()
                e.printStackTrace()
            }
        }
    }

    fun clearToast() {
        toastMessage = null
    }
}
