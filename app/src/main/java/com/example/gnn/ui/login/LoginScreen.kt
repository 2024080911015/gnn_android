package com.example.gnn.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gnn.R
import com.example.gnn.data.api.RetrofitClient
import com.example.gnn.data.model.LoginRequest
import com.example.gnn.data.model.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String, Int) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showRegisterDetails by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if (showRegisterDetails) {
        RegisterDetailsDialog(
            username = username,
            password = password,
            onDismiss = { showRegisterDetails = false },
            onSuccess = { name, id ->
                showRegisterDetails = false
                onLoginSuccess(name, id)
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.login_bg),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF05050F).copy(alpha = 0.72f),
                            Color(0xFF05050F).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main Glass Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF0F0C08).copy(alpha = 0.45f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
            ) {
                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    TabButton(
                        text = "Log in",
                        isSelected = isLoginTab,
                        onClick = { isLoginTab = true; errorMessage = null },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TabButton(
                        text = "Sign up",
                        isSelected = !isLoginTab,
                        onClick = { isLoginTab = false; errorMessage = null },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Form Area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    GlassTextField(
                        value = username,
                        onValueChange = { username = it; errorMessage = null },
                        placeholder = if (isLoginTab) "用户名" else "设置用户名"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        placeholder = if (isLoginTab) "密码" else "设置密码",
                        isPassword = true
                    )

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color(0xFFFDA4AF), // rose-300
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Button
                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                errorMessage = "用户名和密码不能为空"
                                return@Button
                            }

                            if (isLoginTab) {
                                isLoading = true
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.instance.login(LoginRequest(username, password))
                                        if (response.isSuccessful && response.body()?.status == "success") {
                                            val data = response.body()?.data
                                            if (data != null) {
                                                onLoginSuccess(data.username, data.uid)
                                            } else {
                                                errorMessage = "服务器返回数据异常"
                                            }
                                        } else {
                                            errorMessage = response.body()?.message ?: "认证失败"
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "网络连接错误: ${e.localizedMessage}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            } else {
                                showRegisterDetails = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF292524)
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF292524))
                        } else {
                            Text(
                                text = if (isLoginTab) "立即登录" else "下一步：完善资料",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (!isLoginTab) {
                        Text(
                            text = "注册即表示您同意校园社交网络规范",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterDetailsDialog(
    username: String,
    password: String,
    onDismiss: () -> Unit,
    onSuccess: (String, Int) -> Unit
) {
    var gender by remember { mutableStateOf("男") }
    var grade by remember { mutableStateOf("大一") }
    var major by remember { mutableStateOf("计算机") }
    val selectedHobbies = remember { mutableStateListOf<String>() }
    val selectedTags = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val OPT_GENDERS = listOf("男", "女")
    val OPT_GRADES = listOf("大一", "大二", "大三", "大四", "研一", "研二", "研三", "博士")
    val OPT_MAJORS = listOf("计算机", "新闻", "会计", "美术", "通信", "医学", "法学", "土木", "英语", "生物", "电气", "体育")
    val OPT_HOBBIES = listOf("绘画", "编程", "动漫", "足球", "羽毛球", "音乐", "天文", "围棋", "缝纫", "骑行", "剪纸", "种植", "机械", "舞蹈", "跑步")
    val OPT_TAGS = listOf("社恐星人", "社交牛逼症", "社交普通型", "熬夜的神", "早睡早起", "作息规律", "高冷", "可爱", "温和", "吃货", "宅属性", "镇圈大佬", "段子手", "技术大牛", "运动达人")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0C08).copy(alpha = 0.95f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("完善个人信息", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("性别")
                OptionRow(OPT_GENDERS, gender) { gender = it }

                SectionTitle("年级")
                OptionRow(OPT_GRADES, grade) { grade = it }

                SectionTitle("专业")
                OptionRow(OPT_MAJORS, major) { major = it }

                SectionTitle("爱好 (可多选)")
                MultiOptionRow(OPT_HOBBIES, selectedHobbies)

                SectionTitle("个性标签 (可多选)")
                MultiOptionRow(OPT_TAGS, selectedTags)

                errorMessage?.let {
                    Text(it, color = Color(0xFFFDA4AF), fontSize = 12.sp, modifier = Modifier.padding(top = 16.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            try {
                                val request = RegisterRequest(
                                    username = username,
                                    password = password,
                                    gender = gender,
                                    grade = grade,
                                    major = major,
                                    hobbies = if (selectedHobbies.isEmpty()) "无" else selectedHobbies.joinToString(" "),
                                    tags = if (selectedTags.isEmpty()) "无标签" else selectedTags.joinToString(" ")
                                )
                                val response = RetrofitClient.instance.register(request)
                                if (response.isSuccessful && response.body()?.status == "success") {
                                    // Registration success, now login
                                    val loginRes = RetrofitClient.instance.login(LoginRequest(username, password))
                                    if (loginRes.isSuccessful && loginRes.body()?.status == "success") {
                                        val data = loginRes.body()?.data
                                        if (data != null) onSuccess(data.username, data.uid)
                                    } else {
                                        onDismiss() // Go back to login
                                    }
                                } else {
                                    errorMessage = response.body()?.message ?: "注册失败"
                                }
                            } catch (e: Exception) {
                                errorMessage = "网络错误"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24), contentColor = Color(0xFF292524)),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF292524))
                    else Text("确认注册并生成社交档案", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
}

@Composable
fun OptionRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options) { opt ->
            val isSelected = opt == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.05f))
                    .clickable { onSelect(opt) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(opt, color = if (isSelected) Color(0xFF292524) else Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MultiOptionRow(options: List<String>, selected: MutableList<String>) {
    FlowRow(modifier = Modifier.fillMaxWidth(), spacing = 8.dp) {
        options.forEach { opt ->
            val isSelected = selected.contains(opt)
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.05f))
                    .clickable {
                        if (isSelected) selected.remove(opt) else selected.add(opt)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(opt, color = if (isSelected) Color(0xFF292524) else Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    spacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        var x = 0
        var y = 0
        var maxHeight = 0
        val placeables = measurables.map { measurable ->
            val placeable = measurable.measure(constraints)
            if (x + placeable.width > constraints.maxWidth) {
                x = 0
                y += maxHeight + spacing.roundToPx()
                maxHeight = 0
            }
            val pos = androidx.compose.ui.unit.IntOffset(x, y)
            x += placeable.width + spacing.roundToPx()
            maxHeight = maxOf(maxHeight, placeable.height)
            placeable to pos
        }
        layout(constraints.maxWidth, y + maxHeight) {
            placeables.forEach { (placeable, pos) ->
                placeable.placeRelative(pos)
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.15f) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 14.sp
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.13f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
            cursorColor = Color(0xFFFBBF24),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(12.dp)
            )
    )
}
