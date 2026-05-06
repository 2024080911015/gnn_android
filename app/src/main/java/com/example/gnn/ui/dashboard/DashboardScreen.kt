package com.example.gnn.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import coil.compose.AsyncImage
import com.example.gnn.data.api.RetrofitClient
import com.example.gnn.ui.dashboard.panels.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("推荐交友") }

    val menuItems = listOf(
        MenuItem("✨", "推荐交友"),
        MenuItem("🌌", "我的社交星系"),
        MenuItem("🌟", "全校星图"),
        MenuItem("🚩", "组队大厅"),
        MenuItem("🤝", "关系管理"),
        MenuItem("🔍", "找朋友"),
        MenuItem("👤", "个人空间"),
        MenuItem("📊", "全校生态大盘"),
        MenuItem("💌", "破冰收件箱")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = Color.White
            ) {
                Text(
                    text = "校园社交",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFB45309),
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Drawer avatar
                    val drawerAvatarUrl = viewModel.userDetail?.avatar?.let {
                        "${RetrofitClient.BASE_URL}static/avatars/$it?t=${viewModel.avatarVersion}"
                    }
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (drawerAvatarUrl != null) {
                            AsyncImage(
                                model = drawerAvatarUrl,
                                contentDescription = "头像",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color(0xFFFEF3C7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = viewModel.currentUsername.firstOrNull()?.toString() ?: "?",
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = viewModel.currentUsername, fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1917))
                        Text(text = "ID: ${viewModel.currentUid}", fontSize = 12.sp, color = Color(0xFFA8A29E))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF5F5F4))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(menuItems) { item ->
                        NavigationDrawerItem(
                            label = { Text(text = "${item.icon}  ${item.name}") },
                            selected = selectedItem == item.name,
                            onClick = {
                                selectedItem = item.name
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFFFFFBEB),
                                selectedTextColor = Color(0xFFB45309),
                                unselectedTextColor = Color(0xFF57534E)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF78716C)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("退出登录")
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedItem, color = Color(0xFF1C1917), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFFAF9F7)
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (selectedItem) {
                    "推荐交友" -> RecommendPanel(viewModel)
                    "我的社交星系" -> GraphPanel(viewModel.currentUid)
                    "全校星图" -> StarMapPanel()
                    "组队大厅" -> ActivityPanel(viewModel)
                    "关系管理" -> RelationsPanel(viewModel)
                    "找朋友" -> SearchPanel(viewModel)
                    "个人空间" -> ProfilePanel(viewModel)
                    "全校生态大盘" -> StatsPanel(viewModel)
                    "破冰收件箱" -> InboxPanel(viewModel)
                }

                // Global User Detail Dialog
                viewModel.currentUserToShow?.let { user ->
                    UserDetailDialog(
                        user = user,
                        isFollowing = viewModel.isFollowing(user.student_id),
                        onToggleFollow = { viewModel.toggleFollow(user.student_id) },
                        onDismiss = { viewModel.closeUserDetail() }
                    )
                }
            }
        }
    }
}

data class MenuItem(val icon: String, val name: String)
