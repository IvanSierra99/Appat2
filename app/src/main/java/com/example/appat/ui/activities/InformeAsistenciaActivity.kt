package com.example.appat.ui.activities

import DefaultDrawerContent
import MyAppTopBar
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.appat.R
import com.example.appat.domain.usecases.DatosInformeInput
import com.example.appat.ui.viewmodel.InformeAsistenciaViewModel
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class InformeAsistenciaActivity : ComponentActivity() {
    private val informeAsistenciaViewModel: InformeAsistenciaViewModel by viewModel()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val nombreCentro = sharedPreferences.getString("nombreCentro", "Centro Escolar")
        val centroEscolarId = sharedPreferences.getString("centroEscolarId", null)
        val token = sharedPreferences.getString("token", null)
        val rol = sharedPreferences.getString("rol", "")

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val coroutineScope = rememberCoroutineScope()

            MyAppTopBar(
                onMenuClick = { coroutineScope.launch { drawerState.open() } },
                schoolName = nombreCentro,
                drawerState = drawerState,
                drawerContent = { DefaultDrawerContent(this, drawerState, rol) },
                content = { paddingValues ->
                    InformeAsistenciaScreen(
                        paddingValues = paddingValues,
                        viewModel = informeAsistenciaViewModel,
                        centroEscolarId = centroEscolarId!!,
                        token = token!!
                    ){ requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) }
                }
            )
            LaunchedEffect(Unit) {
                informeAsistenciaViewModel.consultarInformeDiario(LocalDate.now(), centroEscolarId!!, token!!)
            }
        }
    }

    internal fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // WRITE_EXTERNAL_STORAGE no es necesario para Android 10+
        } else {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InformeAsistenciaScreen(
    paddingValues: PaddingValues,
    viewModel: InformeAsistenciaViewModel,
    centroEscolarId: String,
    token: String,
    requestPermission: () -> Unit
) {
    val datosInforme by viewModel.datosInforme.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val error by viewModel.error.collectAsState()
    val showDropdown = remember { mutableStateOf(false) }

    var reportTitle by remember { mutableStateOf("Informe de Asistencia Diaria") }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetError()
            viewModel.consultarInformeDiario(LocalDate.now(), centroEscolarId, token)
        }
    }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        content = {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(colorResource(id = R.color.accent).copy(0.6f)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier
                                .weight(0.33f)
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            onClick = {
                                coroutineScope.launch {
                                    val selectedDate = selectDate(context)
                                    if (selectedDate != null) {
                                        viewModel.consultarInformeDiario(selectedDate, centroEscolarId, token)
                                        reportTitle = "Informe de Asistencia Diaria"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent)),
                            contentPadding = PaddingValues(vertical = 2.dp, horizontal = 4.dp),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Diario",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
                            )
                        }
                        Button(
                            modifier = Modifier
                                .weight(0.33f)
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.consultarInformeMensual(centroEscolarId, token)
                                    reportTitle = "Informe de Asistencia Mensual"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent)),
                            contentPadding = PaddingValues(vertical = 2.dp, horizontal = 4.dp),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Mensual",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.33f)
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { showDropdown.value = true },
                                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent)),
                                contentPadding = PaddingValues(vertical = 2.dp, horizontal = 4.dp),
                                enabled = !isLoading
                            ) {
                                Text(
                                    text = "Descargar",
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showDropdown.value,
                                onDismissRequest = { showDropdown.value = false },
                                offset = DpOffset(x = (0).dp, y = 0.dp)
                            ) {
                                DropdownMenuItem(onClick = {
                                    showDropdown.value = false
                                    if ((context as InformeAsistenciaActivity).checkPermission()) {
                                        downloadReport("PDF", context, datosInforme, centroEscolarId, token, reportTitle)
                                    } else {
                                        requestPermission()
                                    }
                                },
                                    text = {
                                        Text("PDF", modifier = Modifier.fillMaxWidth().padding(8.dp))
                                    })
                                HorizontalDivider(color = colorResource(id = R.color.secondary_text))
                                DropdownMenuItem(onClick = {
                                    showDropdown.value = false
                                    if ((context as InformeAsistenciaActivity).checkPermission()) {
                                        downloadReport("Excel", context, datosInforme, centroEscolarId, token, reportTitle)
                                    } else {
                                        requestPermission()
                                    }
                                },
                                    text = {
                                        Text("Excel", modifier = Modifier.fillMaxWidth().padding(8.dp))
                                    })
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))

                        datosInforme?.let { informe ->
                            Text(
                                text = reportTitle,
                                fontSize = 22.sp,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Fecha: ${informe.fecha}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Total: ${informe.total}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Habituales: ${informe.habituales}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "No Habituales: ${informe.noHabituales}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Recuento por etapas:",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            informe.etapas.forEach { (etapa, count) ->
                                Text(
                                    text = "$etapa: $count",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Alumnos no habituales:",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            informe.alumnosNoHabituales.forEach { alumno ->
                                Text(
                                    text = "${alumno.apellido}, ${alumno.nombre} - Días: ${alumno.dias.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}


fun downloadReport(format: String, context: Context, datosInforme: DatosInformeInput?, centroEscolarId: String, token: String, reportTitle: String) {
    when (format) {
        "PDF" -> datosInforme?.let { downloadPDF(context, it, reportTitle) }
        "Excel" -> datosInforme?.let { downloadExcel(context, it, reportTitle) }
    }
    Toast.makeText(context, "Descargando informe en formato $format...", Toast.LENGTH_LONG).show()
}


fun downloadPDF(context: Context, informe: DatosInformeInput, reportTitle: String) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()
    paint.color = android.graphics.Color.BLACK
    var yOffset = 25f

    // Información del informe
    canvas.drawText("Informe de Asistencia - ${informe.fecha}", 10f, yOffset, paint)
    yOffset += 25f
    canvas.drawText("Total: ${informe.total}", 10f, yOffset, paint)
    yOffset += 25f
    canvas.drawText("Habituales: ${informe.habituales}", 10f, yOffset, paint)
    yOffset += 25f
    canvas.drawText("No Habituales: ${informe.noHabituales}", 10f, yOffset, paint)
    yOffset += 25f

    // Información de las etapas
    informe.etapas.forEach { (etapa, count) ->
        canvas.drawText("$etapa: $count", 10f, yOffset, paint)
        yOffset += 25f
    }

    yOffset += 25f
    canvas.drawText("Alumnos no habituales:", 10f, yOffset, paint)
    yOffset += 25f
    informe.alumnosNoHabituales.forEach { alumno ->
        val diasString = alumno.dias.joinToString(", ")
        val text = "${alumno.apellido}, ${alumno.nombre} - Días: $diasString"
        val lines = wrapText(text, paint, 280)
        lines.forEach { line ->
            canvas.drawText(line, 10f, yOffset, paint)
            yOffset += 25f
        }
    }

    pdfDocument.finishPage(page)

    val fileName = if (reportTitle.contains("Diaria")) {
        "InformeAsistencia${informe.fecha}.pdf"
    } else {
        "InformeAsistencia${informe.fecha.split(" ")[0].uppercase()}.pdf"
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
    }

    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    uri?.let {
        try {
            val outputStream = contentResolver.openOutputStream(it)
            pdfDocument.writeTo(outputStream)
            Toast.makeText(context, "PDF guardado en $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    pdfDocument.close()
}

fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = StringBuilder()

    for (word in words) {
        val potentialLine = if (currentLine.isEmpty()) word else "${currentLine} $word"
        if (paint.measureText(potentialLine) <= maxWidth) {
            currentLine.append(if (currentLine.isEmpty()) word else " $word")
        } else {
            lines.add(currentLine.toString())
            currentLine = StringBuilder(word)
        }
    }

    if (currentLine.isNotEmpty()) {
        lines.add(currentLine.toString())
    }

    return lines
}
fun downloadExcel(context: Context, informe: DatosInformeInput, reportTitle: String) {
    val workbook = HSSFWorkbook()
    val sheet = workbook.createSheet("Informe Asistencia")
    var rownum = 0

    // Información del informe
    var row = sheet.createRow(rownum++)
    row.createCell(0).setCellValue("Fecha")
    row.createCell(1).setCellValue(informe.fecha)
    row = sheet.createRow(rownum++)
    row.createCell(0).setCellValue("Total")
    row.createCell(1).setCellValue(informe.total.toDouble())
    row = sheet.createRow(rownum++)
    row.createCell(0).setCellValue("Habituales")
    row.createCell(1).setCellValue(informe.habituales.toDouble())
    row = sheet.createRow(rownum++)
    row.createCell(0).setCellValue("No Habituales")
    row.createCell(1).setCellValue(informe.noHabituales.toDouble())

    // Información de las etapas
    informe.etapas.forEach { (etapa, count) ->
        row = sheet.createRow(rownum++)
        row.createCell(0).setCellValue(etapa)
        row.createCell(1).setCellValue(count.toDouble())
    }

    // Espacio entre secciones
    rownum++

    // Información de alumnos no habituales
    row = sheet.createRow(rownum++)
    row.createCell(0).setCellValue("Alumnos no habituales:")
    informe.alumnosNoHabituales.forEach { alumno ->
        val diasString = alumno.dias.joinToString(", ") { it }
        row = sheet.createRow(rownum++)
        row.createCell(0).setCellValue("${alumno.apellido}, ${alumno.nombre}")
        row.createCell(1).setCellValue(diasString)
    }

    val fileName = if (reportTitle.contains("Diaria")) {
        "InformeAsistencia${informe.fecha}.xls"
    } else {
        "InformeAsistencia${informe.fecha.split(" ")[0].uppercase()}.pdf"
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
    }

    val contentResolver = context.contentResolver
    val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    uri?.let {
        try {
            val outputStream = contentResolver.openOutputStream(it)
            workbook.write(outputStream)
            workbook.close()
            outputStream?.close()
            Toast.makeText(context, "Excel guardado en $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


suspend fun selectDate(context: Context): LocalDate? {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return suspendCoroutine { continuation ->
        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            continuation.resume(selectedDate)
        }, year, month, day).show()
    }
}
