package com.example.studivo.presentation.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.studivo.data.preferences.ThemeDataStore

// 🎵 Colores principales para app musical
private val MusicPurple = Color(0xFF7C3AED)       // Púrpura principal (creatividad, música)
private val MusicPurpleDark = Color(0xFF5B21B6)   // Púrpura oscuro
private val MusicBlue = Color(0xFF2563EB)         // Azul profundo (concentración)
private val MusicBlueDark = Color(0xFF1E3A8A)     // Azul oscuro
private val MusicYellow = Color(0xFFFACC15)       // Amarillo (energía, ritmo)
private val MusicRed = Color(0xFFEF4444)          // Rojo (errores, alertas)

// 🎵 Grises suaves para lectura
private val NeutralGray50 = Color(0xFFFAFAFA)
private val NeutralGray100 = Color(0xFFF5F5F5)
private val NeutralGray200 = Color(0xFFE5E5E5)
private val NeutralGray800 = Color(0xFF262626)
private val NeutralGray900 = Color(0xFF171717)

private val DarkColorScheme = darkColorScheme(
	primary = MusicPurple,
	onPrimary = Color.White,
	primaryContainer = MusicPurpleDark,
	onPrimaryContainer = Color.White,
	
	secondary = MusicBlue,
	onSecondary = Color.White,
	secondaryContainer = MusicBlueDark,
	onSecondaryContainer = Color.White,
	
	tertiary = MusicYellow,
	onTertiary = Color.Black,
	tertiaryContainer = Color(0xFFEAB308),
	onTertiaryContainer = Color.Black,
	
	error = MusicRed,
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
	primary = MusicPurple,
	onPrimary = Color.White,
	primaryContainer = Color(0xFFEDE9FE),
	onPrimaryContainer = MusicPurpleDark,
	
	secondary = MusicBlue,
	onSecondary = Color.White,
	secondaryContainer = Color(0xFFDBEAFE),
	onSecondaryContainer = MusicBlueDark,
	
	tertiary = MusicYellow,
	onTertiary = Color.Black,
	tertiaryContainer = Color(0xFFFEF9C3),
	onTertiaryContainer = Color(0xFF854D0E),
	
	error = MusicRed,
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
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = false,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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

// 🎵 Tipografía más expresiva para música
private val MusicTypography = Typography(
	displayLarge = Typography().displayLarge.copy(fontWeight = FontWeight.Bold),
	headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.Bold),
	headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.SemiBold),
	titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
	titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
	bodyLarge = Typography().bodyLarge.copy(lineHeight = 24.sp)
)

// 🎵 Extensiones de utilidad
object AppColors {
	val HighlightPurple = MusicPurple
	val FocusBlue = MusicBlue
	val EnergyYellow = MusicYellow
	val WarningRed = MusicRed
	
	@Composable
	fun surface(elevation: Int = 0): Color {
		return when (elevation) {
			0 -> MaterialTheme.colorScheme.surface
			1 -> MaterialTheme.colorScheme.surfaceVariant
			else -> MaterialTheme.colorScheme.surfaceVariant
		}
	}
}

