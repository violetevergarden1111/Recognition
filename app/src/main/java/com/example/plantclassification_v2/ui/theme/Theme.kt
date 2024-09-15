package com.example.plantclassification_v2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.webkit.internal.ApiFeature.N

private val DarkColorScheme = darkColorScheme(
    primary = Aqua80,
    onPrimary = Aqua20,
    primaryContainer = Aqua30,
    onPrimaryContainer = Aqua90,
    inversePrimary = AquaGrey40,
    secondary = Emerala80,
    onSecondary = Emerala20,
    secondaryContainer = Emerala30,
    onSecondaryContainer = Emerala90,
    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = NeutralAqua10,
    onBackground = NeutralAqua90,
    surface = NeutralAqua10,
    onSurface = NeutralAqua90,
    surfaceVariant = NeutralAqua30,
    onSurfaceVariant = NeutralAqua80,
    outline = Aqua50
)

private val LightColorScheme = lightColorScheme(
    primary = Aqua40,
    onPrimary = Color.White,
    primaryContainer = Aqua90,
    onPrimaryContainer = Aqua10,
    inversePrimary = AquaGrey80,
    secondary = Emerala40,
    onSecondary = Color.White,
    secondaryContainer = Emerala90,
    onSecondaryContainer = Emerala10,
    tertiary = Blue40,
    onTertiary = Color.White,
    tertiaryContainer = Blue90,
    onTertiaryContainer = Blue10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = NeutralAqua95,
    onBackground = NeutralAqua5,
    surface = NeutralAqua95,
    onSurface = NeutralAqua5,
    surfaceVariant = NeutralAqua85,
    onSurfaceVariant = NeutralAqua25,
    outline = Aqua50
)

@Composable
fun PlantClassification_v2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes
    )
}