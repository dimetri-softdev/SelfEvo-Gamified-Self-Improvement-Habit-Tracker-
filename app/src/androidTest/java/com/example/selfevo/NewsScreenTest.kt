package com.example.selfevo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.selfevo.ui.news.NewsScreen
import org.junit.Rule
import org.junit.Test

class NewsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun newsScreen_displaysNewsHeaderAndCategories() {
        composeTestRule.setContent {
            NewsScreen()
        }

        composeTestRule.onNodeWithText("SelfEvo News").assertIsDisplayed()
        composeTestRule.onNodeWithText("News Categories").assertIsDisplayed()
        composeTestRule.onNodeWithText("Newsletter & Updates").assertIsDisplayed()
    }
}
