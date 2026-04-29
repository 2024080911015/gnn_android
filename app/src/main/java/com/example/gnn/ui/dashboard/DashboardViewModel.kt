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
                    recommendations = recResponse.body()?.recommend_friends ?: emptyList()
                }
                val reportResponse = api.getSocialReport(currentUid)
                if (reportResponse.isSuccessful) {
                    socialReport = reportResponse.body()
                }
            } catch (e: Exception) { e.printStackTrace() }
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
        
        viewModelScope.launch {
            try {
                val response = api.toggleFollow(mapOf("target_id" to targetId.toString(), "action" to action))
                if (response.isSuccessful && response.body()?.status == "success") {
                    toastMessage = if (isCurrentlyFollowing) "已取消关注" else "关注成功"
                    // TRIGGER REFRESHES
                    loadSocialData()
                    loadRecommendations()
                    refreshProfile() // In case it affects profile stats (though unlikely here)
                } else {
                    toastMessage = response.body()?.message ?: "操作失败"
                }
                onComplete()
            } catch (e: Exception) {
                toastMessage = "网络错误"
                e.printStackTrace()
            }
        }
    }

    fun clearToast() {
        toastMessage = null
    }
}
