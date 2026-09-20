package com.example.selfevo.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.data.model.NewsArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    modifier: Modifier = Modifier
) {
    // Dummy data for design alignment
    val categories = listOf("Fitness", "Productivity", "Nutrition", "Mental Health", "Updates")
    val featuredArticle = NewsArticle(
        id = "f1",
        title = "Maxing Out Your Physical Stats This Summer",
        description = "Expert tips on how to push your physical attribute to 99 before the next evolution season.",
        category = "Fitness",
        date = "2 hours ago",
        isFeatured = true
    )
    val latestArticles = listOf(
        NewsArticle("1", "Morning Routine for Skill Points", "How to optimize your morning for mental agility.", "Productivity", null, "4 hours ago"),
        NewsArticle("2", "Nutrition Guide: Elite Tier", "Fueling for high intensity sprint training.", "Nutrition", null, "1 day ago"),
        NewsArticle("3", "Sleep & Recovery Stats", "Why rest is the most important defensive stat.", "Mental Health", null, "2 days ago")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SelfEvo News", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF800000)) // Maroon as per wireframe text color
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Hero Section (Figure 12)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HeroSection()
            }

            // 2. Featured Article Banner
            item {
                FeaturedArticleBanner(featuredArticle)
            }

            // 3. Search & Filter Section
            item {
                SearchAndFilterSection()
            }

            // 4. News Categories
            item {
                NewsCategoriesSection(categories)
            }

            // 5. Latest Articles Grid
            item {
                LatestArticlesSection(latestArticles)
            }

            // 6. Newsletter Block
            item {
                NewsletterBlock()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("HERO BANNER", fontWeight = FontWeight.Black, color = Color.Gray)
            Text("LATEST SELF-IMPROVEMENT TRENDS", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun FeaturedArticleBanner(article: NewsArticle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF800000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("FEATURED", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(article.date, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(article.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                article.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SearchAndFilterSection() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search articles...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun NewsCategoriesSection(categories: List<String>) {
    Column {
        Text("News Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text(category) }
                )
            }
        }
    }
}

@Composable
fun LatestArticlesSection(articles: List<NewsArticle>) {
    Column {
        Text("Latest Articles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        articles.forEach { article ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(article.category, fontSize = 12.sp, color = Color(0xFF800000), fontWeight = FontWeight.Bold)
                    Text(article.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(article.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Divider(color = Color.LightGray, thickness = 0.5.dp)
        }
    }
}

@Composable
fun NewsletterBlock() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Newsletter & Updates", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Get the latest evolution tips delivered to your inbox.",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Enter your email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800000)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Subscribe")
            }
        }
    }
}
