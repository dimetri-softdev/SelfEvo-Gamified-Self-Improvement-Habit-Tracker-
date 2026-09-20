package com.example.selfevo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.selfevo.ui.dashboard.NewsItem
import com.example.selfevo.ui.dashboard.NewsScreen
import org.junit.Rule
import org.junit.Test

class NewsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun newsScreen_displaysNewsItems() {
        val newsItems = listOf(
            NewsItem("Title 1", "Content 1"),
            NewsItem("Title 2", "Content 2")
        )

        composeTestRule.setContent {
            NewsScreen(newsItems = newsItems)
        }

        composeTestRule.onNodeWithText("SelfEvo News").assertIsDisplayed()
        composeTestRule.onNodeWithText("Title 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Title 2").assertIsDisplayed()
    }
}
