package com.example.studivo.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.studivo.data.preferences.ThemeDataStore

// 🎵 Colores principales
// 🎵 Colores principales (Inspiración: concentración, ritmo y precisión)
private val FocusBlue = Color(0xFF0EA5E9)      // Azul brillante (energía, enfoque)
private val DeepBlue = Color(0xFF1E3A8A)       // Azul oscuro (estabilidad, profesional)
private val AccentTurquoise = Color(0xFF14B8A6) // Verde-agua (fluidez, equilibrio)
private val EnergyAmber = Color(0xFFF59E0B)     // Ámbar cálido (alerta suave)
private val WarningRed = Color(0xFFDC2626)      // Rojo fuerte (error, atención)

// 🎵 Grises neutros (equilibrio entre luz y sombra)
private val NeutralGray50 = Color(0xFFF9FAFB)
private val NeutralGray100 = Color(0xFFF3F4F6)
private val NeutralGray200 = Color(0xFFE5E7EB)
private val NeutralGray800 = Color(0xFF1F2937)
private val NeutralGray900 = Color(0xFF111827)


private val DarkColorScheme = darkColorScheme(
	primary = FocusBlue,
	onPrimary = Color.White,
	primaryContainer = DeepBlue,
	onPrimaryContainer = Color.White,
	
	secondary = AccentTurquoise,
	onSecondary = Color.White,
	secondaryContainer = Color(0xFF134E4A),
	onSecondaryContainer = Color.White,
	
	tertiary = EnergyAmber,
	onTertiary = Color.Black,
	tertiaryContainer = Color(0xFF92400E),
	onTertiaryContainer = Color.White,
	
	error = WarningRed,
	onError = Color.White,
	errorContainer = Color(0xFF7F1D1D),
	onErrorContainer = Color.White,
	
	background = NeutralGray900,
	onBackground = Color.White,
	surface = NeutralGray800,
	onSurface = Color.White,
	surfaceVariant = Color(0xFF374151),
	onSurfaceVariant = Color(0xFFE5E7EB),
	
	outline = Color(0xFF6B7280),
	outlineVariant = Color(0xFF4B5563)
)

private val LightColorScheme = lightColorScheme(
	primary = FocusBlue,
	onPrimary = Color.White,
	primaryContainer = Color(0xFFE0F2FE),
	onPrimaryContainer = DeepBlue,
	
	secondary = AccentTurquoise,
	onSecondary = Color.White,
	secondaryContainer = Color(0xFFD1FAE5),
	onSecondaryContainer = Color(0xFF065F46),
	
	tertiary = EnergyAmber,
	onTertiary = Color.Black,
	tertiaryContainer = Color(0xFFFEF3C7),
	onTertiaryContainer = Color(0xFF78350F),
	
	error = WarningRed,
	onError = Color.White,
	errorContainer = Color(0xFFFEE2E2),
	onErrorContainer = Color(0xFF7F1D1D),
	
	background = Color.White,
	onBackground = NeutralGray900,
	surface = NeutralGray50,
	onSurface = NeutralGray900,
	surfaceVariant = NeutralGray100,
	onSurfaceVariant = Color(0xFF6B7280),
	
	outline = NeutralGray200,
	outlineVariant = Color(0xFFD1D5DB)
)


@Composable
fun StudivoTheme(
	// Se actualiza automáticamente al cambiar el modo del sistema
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val darkTheme = isSystemInDarkTheme() // 🔥 Se sincroniza con el sistema
	val context = LocalContext.current
	
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			if (darkTheme) dynamicDarkColorScheme(context)
			else dynamicLightColorScheme(context)
		}
		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}
	
	MaterialTheme(
		colorScheme = colorScheme,
		typography = MusicTypography,
		content = content
	)
}

// 🎵 Tipografía expresiva
private val MusicTypography = Typography(
	displayLarge = Typography().displayLarge.copy(fontWeight = FontWeight.Bold),
	headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.Bold),
	headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.SemiBold),
	titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
	titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
	bodyLarge = Typography().bodyLarge.copy(lineHeight = 24.sp)
)

// 🎨 Utilidades
object AppColors {
	val Primary = FocusBlue
	val Secondary = AccentTurquoise
	val Highlight = EnergyAmber
	val Error = WarningRed
	
	@Composable
	fun surface(elevation: Int = 0): Color {
		return when (elevation) {
			0 -> MaterialTheme.colorScheme.surface
			1 -> MaterialTheme.colorScheme.surfaceVariant
			else -> MaterialTheme.colorScheme.surfaceVariant
		}
	}
}
