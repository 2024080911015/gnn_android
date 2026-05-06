package com.example.gnn.ui.dashboard.panels

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.gnn.data.api.RetrofitClient
import com.example.gnn.data.model.UserDetailResponse
import kotlinx.coroutines.launch

@Composable
fun UserDetailDialog(
    user: UserDetailResponse,
    isFollowing: Boolean,
    onToggleFollow: () -> Unit,
    onDismiss: () -> Unit
) {
    var showMessageInput by remember { mutableStateOf(false) }
    var messageContent by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                val avatarUrl = user.avatar?.let { "${RetrofitClient.BASE_URL}static/avatars/$it" }
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "头像",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFFFEF3C7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(user.username.firstOrNull()?.toString() ?: "?", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(user.username, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1C1917))
                Text("UID: #${user.student_id}", fontSize = 12.sp, color = Color(0xFFA8A29E))
                
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(user.status ?: "找朋友", color = Color(0xFFB45309), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (user.signature.isNullOrBlank()) "“未设置签名”" else "“${user.signature}”",
                    color = Color(0xFF78716C),
                    fontSize = 14.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                // Info Tags
                Text("基本资料", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA8A29E), modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))
                Text(user.student_info, fontSize = 13.sp, color = Color(0xFF44403C), modifier = Modifier.align(Alignment.Start))

                Spacer(modifier = Modifier.height(32.dp))

                // Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onToggleFollow,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color(0xFFF5F5F4) else Color(0xFFFBBF24),
                            contentColor = if (isFollowing) Color(0xFF78716C) else Color(0xFF292524)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isFollowing) "取消关注" else "点击关注")
                    }
                    
                    OutlinedButton(
                        onClick = { showMessageInput = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E5E4))
                    ) {
                        Text("发送留言", color = Color(0xFF57534E))
                    }
                }

                if (showMessageInput) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = messageContent,
                        onValueChange = { messageContent = it },
                        placeholder = { Text("输入留言内容...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (messageContent.isBlank()) return@Button
                            isSending = true
                            scope.launch {
                                try {
                                    val res = RetrofitClient.instance.sendMessage(mapOf(
                                        "receiver_id" to user.student_id.toString(),
                                        "content" to messageContent
                                    ))
                                    if (res.isSuccessful && res.body()?.status == "success") {
                                        showMessageInput = false
                                        messageContent = ""
                                    }
                                } catch (e: Exception) {} finally { isSending = false }
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !isSending,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSending) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF292524))
                        else Text("确认发送")
                    }
                }
            }
        }
    }
}
