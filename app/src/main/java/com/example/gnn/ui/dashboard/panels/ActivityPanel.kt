package com.example.gnn.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ActivityPanel(viewModel: DashboardViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadActivities()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("🚩 组队大厅", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.activities) { activity ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.background(Color(0xFFEFF6FF), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text(activity.nature, color = Color(0xFF2563EB), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(activity.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                            Text("🔥 ${activity.match_score}%", color = Color(0xFFEA580C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(activity.description, fontSize = 13.sp, color = Color(0xFF57534E), maxLines = 2)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("发起人: ${activity.publisher_name}", fontSize = 12.sp, color = Color(0xFF78716C))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("容量: ${activity.member_count}/${activity.total_capacity}", fontSize = 12.sp, color = Color(0xFF78716C))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { /* TODO: Join */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309))
                        ) {
                            Text("申请加入")
                        }
                    }
                }
            }
        }
    }
}
