package com.example.gnn.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gnn.ui.dashboard.DashboardViewModel

@Composable
fun ProfilePanel(viewModel: DashboardViewModel) {
    val user = viewModel.userDetail

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("个人空间", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1C1917))
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(100.dp).background(
                        Brush.linearGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFFEDD5))),
                        CircleShape
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(viewModel.currentUsername.firstOrNull()?.toString() ?: "?", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(viewModel.currentUsername, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(user?.status ?: "找朋友", color = Color(0xFFB45309), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.signature?.let { "“$it”" } ?: "“未设置签名”",
                    color = Color(0xFF78716C),
                    fontSize = 14.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF5F5F4))
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoBox("基础资料", user?.student_info ?: "未知", Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    InfoBox("系统标识", "UID: #${viewModel.currentUid}", Modifier.weight(1f), isMono = true)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { /* TODO: Edit Profile */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF57534E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F4))
                ) {
                    Text("📝 修改个人资料")
                }
            }
        }
    }
}

@Composable
fun InfoBox(title: String, content: String, modifier: Modifier = Modifier, isMono: Boolean = false) {
    Column(
        modifier = modifier.background(Color(0xFFFAF9F7), RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA8A29E))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 13.sp,
            color = if (isMono) Color(0xFFB45309) else Color(0xFF44403C),
            fontWeight = if (isMono) FontWeight.Bold else FontWeight.Normal
        )
    }
}
