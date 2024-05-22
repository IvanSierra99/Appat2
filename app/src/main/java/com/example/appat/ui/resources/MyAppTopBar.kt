import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.colorResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.TaskStackBuilder
import com.example.appat.R
import com.example.appat.ui.activities.AlumnoManagementActivity
import com.example.appat.ui.activities.ClaseManagementActivity
import com.example.appat.ui.activities.UserManagementActivity
import com.example.appat.ui.activities.AdminMainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar(
    onMenuClick: () -> Unit,
    schoolName: String?,
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(colorResource(id = R.color.white))
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            ) {
                drawerContent()
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            schoolName ?: "Centro Escolar",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = colorResource(id = R.color.primary_text),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = colorResource(id = R.color.primary_text)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(id = R.color.primary),
                    )
                )
            },
            content = content
        )
    }
}

@Composable
fun DrawerMenuItem(icon: ImageVector, label: String, onClick: () -> Unit, enabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, enabled = enabled)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(colorResource(id = R.color.white)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) colorResource(id = R.color.primary_text) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) colorResource(id = R.color.primary_text) else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

fun isCurrentActivity(context: Context, activityClass: Class<*>): Boolean {
    val currentActivityClass = context::class.java
    return currentActivityClass == activityClass
}

@Composable
fun DefaultDrawerContent(context: Context, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.background(colorResource(id = R.color.white))) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Menú",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.primary_text)
            )
        )
        Divider(color = colorResource(id = R.color.divider))
        DrawerMenuItem(
            icon = Icons.Default.Person,
            label = "Gestión de Usuarios",
            onClick = {
                openActivityWithBackStack(context, UserManagementActivity::class.java)
                scope.launch { drawerState.close() }
            },
            enabled = !isCurrentActivity(context, UserManagementActivity::class.java)
        )
        DrawerMenuItem(
            icon = Icons.Default.School,
            label = "Gestión de Alumnos",
            onClick = {
                openActivityWithBackStack(context, AlumnoManagementActivity::class.java)
                scope.launch { drawerState.close() }
            },
            enabled = !isCurrentActivity(context, AlumnoManagementActivity::class.java)
        )
        DrawerMenuItem(
            icon = Icons.Default.Class,
            label = "Gestión de Clases",
            onClick = {
                openActivityWithBackStack(context, ClaseManagementActivity::class.java)
                scope.launch { drawerState.close() }
            },
            enabled = !isCurrentActivity(context, ClaseManagementActivity::class.java)
        )
        DrawerMenuItem(
            icon = Icons.Default.Assessment,
            label = "Informes",
            onClick = {
                // Aquí puedes añadir la funcionalidad futura
                scope.launch { drawerState.close() }
            },
            enabled = true // Puedes cambiar esta condición cuando implementes esta funcionalidad
        )
    }
}

fun openActivityWithBackStack(context: Context, destinationActivity: Class<*>) {
    TaskStackBuilder.create(context).apply {
        addNextIntent(Intent(context, AdminMainActivity::class.java))
        addNextIntent(Intent(context, destinationActivity))
    }.startActivities()
}
