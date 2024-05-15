import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.example.appat.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar(onMenuClick: () -> Unit, schoolName: String?) {
    TopAppBar(
        title = { Text(schoolName ?: "Centro Escolar", color = colorResource(id = R.color.primary_text)) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = colorResource(id = R.color.primary_text)
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = colorResource(id = R.color.primary)
        )
    )
}
