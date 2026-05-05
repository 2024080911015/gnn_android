package com.example.gnn.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gnn.ui.dashboard.DashboardViewModel

@Composable
fun RelationsPanel(viewModel: DashboardViewModel) {
    // 不在此处调用 loadSocialData()，避免多 worker 场景下
    // 服务端返回陈旧数据覆盖本地乐观更新。
    // 数据由 init() 初始加载，由 toggleFollow() 保持实时同步。

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🤝 关系管理", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("我的关注 (${viewModel.followingList.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF78716C))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.followingList) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showUserDetail(user.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).background(Color(0xFFFEF3C7), CircleShape), contentAlignment = Alignment.Center) {
                            Text(user.username.firstOrNull()?.toString() ?: "?", color = Color(0xFFB45309), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(user.username, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.toggleFollow(user.id) }) {
                            Text("取消关注", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
