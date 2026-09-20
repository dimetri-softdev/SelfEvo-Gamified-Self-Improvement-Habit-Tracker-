package com.example.selfevo.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.selfevo.R
import com.example.selfevo.ui.component.SelfEvoButton
import com.example.selfevo.ui.component.SelfEvoLogo
import com.example.selfevo.ui.component.SelfEvoTextField
import com.example.selfevo.ui.theme.Black
import com.example.selfevo.ui.theme.Gold
import com.example.selfevo.ui.theme.TextSecondary
import com.example.selfevo.ui.theme.DarkGrey

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        SelfEvoLogo(size = 120)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SELFEVO",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Gold,
            letterSpacing = 2.sp
        )

        Text(
            text = "EVOLVE YOUR DAILY STATS",
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Tabs placeholder
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(DarkGrey, RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Gold, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.login_title), fontWeight = FontWeight.Bold, color = Black)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.signup_title), fontWeight = FontWeight.Bold, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SelfEvoTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email_label),
            placeholder = "player@selfevo.app"
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelfEvoTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password_label),
            placeholder = "••••••••",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        SelfEvoButton(
            text = stringResource(R.string.login_title),
            onClick = onLoginSuccess
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
            Text(
                text = stringResource(R.string.or),
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp),
                fontSize = 12.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        SelfEvoButton(
            text = stringResource(R.string.google_sign_in),
            onClick = {},
            containerColor = DarkGrey,
            contentColor = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))

        Row {
            Text(stringResource(R.string.dont_have_account), color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.signup_title), color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
