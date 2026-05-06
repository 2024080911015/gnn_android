package com.example.gnn.ui.dashboard.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.LocalImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.gnn.data.api.RetrofitClient
import com.example.gnn.ui.dashboard.DashboardViewModel

@Composable
fun RecommendPanel(viewModel: DashboardViewModel) {
    var showCommunityMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRecommendations()
        viewModel.loadCommunities()
        viewModel.loadAvatars()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF2D2416), Color(0xFF5C3D1E))),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val currentAvatarUrl = viewModel.userDetail?.avatar?.let {
                        "${RetrofitClient.BASE_URL}static/avatars/$it?t=${viewModel.avatarVersion}"
                    }
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val bannerLetter = viewModel.currentUsername.firstOrNull()?.toString() ?: "?"
                        val bannerPainter = currentAvatarUrl?.let { url ->
                            rememberAsyncImagePainter(model = url, imageLoader = LocalImageLoader.current)
                        }
                        when {
                            bannerPainter != null && bannerPainter.state is AsyncImagePainter.State.Success -> {
                                Image(
                                    painter = bannerPainter,
                                    contentDescription = "头像",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(bannerLetter, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("当前分析账号", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text("${viewModel.currentUsername} #${viewModel.currentUid}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filters Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚙️ 推荐引擎配置", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF44403C))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Mode Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F4), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        FilterButton(
                            text = "社交优化",
                            isSelected = viewModel.selectedMode == "social",
                            onClick = { viewModel.selectedMode = "social" },
                            modifier = Modifier.weight(1f)
                        )
                        FilterButton(
                            text = "纯相似度",
                            isSelected = viewModel.selectedMode == "gnn",
                            onClick = { viewModel.selectedMode = "gnn" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Community Selection
                    Box {
                        OutlinedCard(
                            onClick = { showCommunityMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E5E4))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (viewModel.selectedCommunity.isEmpty()) "不限社区" else viewModel.selectedCommunity,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp,
                                    color = Color(0xFF57534E)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFFA8A29E))
                            }
                        }
                        DropdownMenu(
                            expanded = showCommunityMenu,
                            onDismissRequest = { showCommunityMenu = false },
                            modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("不限社区") },
                                onClick = {
                                    viewModel.selectedCommunity = ""
                                    showCommunityMenu = false
                                }
                            )
                            viewModel.communities.forEach { community ->
                                DropdownMenuItem(
                                    text = { Text(community) },
                                    onClick = {
                                        viewModel.selectedCommunity = community
                                        showCommunityMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.loadRecommendations() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFBEB), contentColor = Color(0xFFB45309))
                    ) {
                        Text("应用配置并重新计算", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Diagnostic Report with Pie Chart
        viewModel.socialReport?.let { report ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📡 社交雷达诊断", fontWeight = FontWeight.Bold, color = Color(0xFF44403C))
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Text(report.status.title, color = Color(0xFFB45309), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(report.status.description, fontSize = 13.sp, color = Color(0xFF78716C))
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Pie Chart
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CommunityPieChart(report.distribution)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Text("💡 建议：${report.advice}", color = Color(0xFF1E40AF), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Recommendation List
        item {
            Text("✨ AI 为您精选的相似灵魂", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1917))
        }

        items(viewModel.recommendations) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.showUserDetail(user.id) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Try avatars map first (correct extension), fallback to direct .jpg URL
                    val avatarUrl = remember(user.id, viewModel.userAvatars) {
                        val fromMap = viewModel.userAvatars[user.id.toString()]
                        if (fromMap != null) {
                            "${RetrofitClient.BASE_URL}static/avatars/$fromMap"
                        } else {
                            "${RetrofitClient.BASE_URL}static/avatars/${user.id}_avatar.jpg"
                        }
                    }
                    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                        SubcomposeAsyncImage(
                            model = avatarUrl,
                            contentDescription = "头像",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = {
                                Box(Modifier.fillMaxSize().background(Color(0xFFFDE68A), CircleShape), contentAlignment = Alignment.Center) {
                                    Text(user.username.firstOrNull()?.toString() ?: "?", color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(user.info, fontSize = 12.sp, color = Color(0xFF78716C), maxLines = 1)
                    }
                    val isFollowing = viewModel.isFollowing(user.id)
                    Button(
                        onClick = { viewModel.toggleFollow(user.id) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color(0xFFF5F5F4) else Color(0xFFFBBF24),
                            contentColor = if (isFollowing) Color(0xFF78716C) else Color.White
                        )
                    ) {
                        Text(if (isFollowing) "已关注" else "关注", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF1C1917) else Color(0xFF78716C),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CommunityPieChart(distribution: List<com.example.gnn.data.model.CommunityDistribution>) {
    val colors = listOf(
        Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFF59E0B),
        Color(0xFFEF4444), Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF14B8A6)
    )
    
    val total = distribution.sumOf { it.count }.toFloat()
    if (total == 0f) return

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.size(150.dp)) {
            var startAngle = -90f
            distribution.forEachIndexed { index, item ->
                val sweepAngle = (item.count / total) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height),
                    style = Fill
                )
                startAngle += sweepAngle
            }
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            distribution.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(colors[index % colors.size], CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${item.name} (${item.percent}%)", fontSize = 11.sp, color = Color(0xFF57534E))
                }
            }
        }
    }
}
