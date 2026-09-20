package com.example.selfevo.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1C1C1C))
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Admin Hero Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                AdminHeroSection()
            }

            // 2. System Notification Banner
            item {
                SystemNotificationBanner()
            }

            // 3. Stats Box
            item {
                AdminStatsBox()
            }

            // 4. User & Content Controls
            item {
                AdminControlsSection()
            }

            // 5. Data Management (System Records)
            item {
                SystemRecordsSection()
            }

            // 6. Authorization Block
            item {
                AuthorizationBlock()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AdminHeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF800000), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Welcome, Head Scout", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("System Admin - Level 5 Access", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SystemNotificationBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        border = BorderStroke(1.dp, Color(0xFF4CAF50))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Admin status updated: All habit sync queues are healthy.", color = Color(0xFF2E7D32), fontSize = 14.sp)
        }
    }
}

@Composable
fun AdminStatsBox() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItemCard(label = "Total Players", value = "1,240", modifier = Modifier.weight(1f))
        StatItemCard(label = "Active Syncs", value = "89", modifier = Modifier.weight(1f))
        StatItemCard(label = "Pending Approvals", value = "12", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatItemCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF800000))
            Text(label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AdminControlsSection() {
    Column {
        Text("User & Content Controls", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminControlButton("Manage Users", Icons.Default.Person, Modifier.weight(1f))
            AdminControlButton("Content Audit", Icons.Default.Settings, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminControlButton("System Logs", Icons.Default.CheckCircle, Modifier.weight(1f))
            AdminControlButton("API Health", Icons.Default.Settings, Modifier.weight(1f))
        }
    }
}

@Composable
fun AdminControlButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = {},
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1C1C1C))
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SystemRecordsSection() {
    Column {
        Text("System Records", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                repeat(3) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Record #${1000 + index}", fontWeight = FontWeight.Medium)
                        Text("Active", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                    if (index < 2) HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun AuthorizationBlock() {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800000)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Submit Admin Changes", fontWeight = FontWeight.Bold)
    }
}
