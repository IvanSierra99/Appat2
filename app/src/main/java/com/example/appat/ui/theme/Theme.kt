import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import com.example.appat.ui.theme.*

@Composable
fun AppatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = DarkPrimary,
            onPrimary = TextIcons,
            primaryContainer = LightPrimary,
            secondary = Accent,
            onSecondary = TextIcons,
            background = PrimaryText,
            onBackground = SecondaryText,
            surface = Divider,
            onSurface = SecondaryText,
            error = Accent,
            onError = PrimaryText
        )
    } else {
        lightColorScheme(
            primary = LightPrimary,
            onPrimary = LightTextIcons,
            primaryContainer = LightLightPrimary,
            secondary = LightAccent,
            onSecondary = LightTextIcons,
            background = LightPrimaryText,
            onBackground = LightSecondaryText,
            surface = LightDivider,
            onSurface = LightSecondaryText,
            error = LightAccent,
            onError = LightPrimaryText
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
