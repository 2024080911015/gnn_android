package com.example.gnn.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gnn.ui.dashboard.DashboardViewModel

@Composable
fun StatsPanel(viewModel: DashboardViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadSocialData()
    }

    val stats = viewModel.socialStats

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("📊 全校生态大盘", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        stats?.let {
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard("总用户数", it.total_users.toString(), Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                StatCard("总关注数", it.total_follows.toString(), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            StatCard("平均关注", it.average_follows.toString(), Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
            Text("🔥 最受欢迎的同学", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            it.most_popular_users.forEach { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(user.username, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("${user.followers_count} 粉丝", color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontSize = 12.sp, color = Color(0xFF78716C))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1917))
        }
    }
}
