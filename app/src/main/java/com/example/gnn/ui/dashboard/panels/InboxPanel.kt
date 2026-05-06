package com.example.gnn.ui.dashboard.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.gnn.data.api.RetrofitClient
import com.example.gnn.ui.dashboard.DashboardViewModel

@Composable
fun InboxPanel(viewModel: DashboardViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadInbox()
        viewModel.loadAvatars()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("💌 破冰收件箱", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.inboxMessages.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("暂无留言", color = Color(0xFFA8A29E))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.inboxMessages) { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (msg.is_read) Color.White else Color(0xFFFFFBEB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Use avatars map for correct filename, fallback to direct URL
                                val inboxAvatarUrl = remember(msg.sender_id, viewModel.userAvatars) {
                                    val fromMap = viewModel.userAvatars[msg.sender_id.toString()]
                                    if (fromMap != null) {
                                        "${RetrofitClient.BASE_URL}static/avatars/$fromMap"
                                    } else {
                                        "${RetrofitClient.BASE_URL}static/avatars/${msg.sender_id}_avatar.jpg"
                                    }
                                }
                                Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                    SubcomposeAsyncImage(
                                        model = inboxAvatarUrl,
                                        contentDescription = "头像",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop,
                                        error = {
                                            Box(Modifier.fillMaxSize().background(Color(0xFFFEF3C7), CircleShape), contentAlignment = Alignment.Center) {
                                                Text(msg.sender_name.firstOrNull()?.toString() ?: "?", color = Color(0xFFB45309), fontSize = 12.sp)
                                            }
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(msg.sender_name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(msg.created_at, fontSize = 10.sp, color = Color(0xFFA8A29E))
                                }
                                if (!msg.is_read) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(msg.content, fontSize = 13.sp, color = Color(0xFF44403C))
                        }
                    }
                }
            }
        }
    }
}
