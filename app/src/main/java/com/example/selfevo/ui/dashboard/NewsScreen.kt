package com.example.selfevo.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class NewsItem(val title: String, val content: String)

@Composable
fun NewsScreen(newsItems: List<NewsItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "SelfEvo News",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(newsItems) { item ->
            NewsCard(item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    NewsScreen(
        newsItems = listOf(
            NewsItem("Update 1.0", "New walkout animations added!"),
            NewsItem("Season 1", "Complete habits to earn Special Gold cards."),
            NewsItem("System Maintenance", "Cloud sync will be offline for 2 hours.")
        )
    )
}

@Composable
fun NewsCard(item: NewsItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = item.title, style = MaterialTheme.typography.titleLarge)
            Text(text = item.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
