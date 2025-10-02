package com.example.studivo.presentation.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.studivo.presentation.navegacion.AppRoutes
import com.example.studivo.presentation.ui.commons.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
	navController: NavController,
) {
	MainScreenContent(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
	navController: NavController,
) {
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							imageVector = Icons.Default.MusicNote,
							contentDescription = null,
							tint = Color(0xFF1976D2),
							modifier = Modifier.size(28.dp)
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text(
							text = "Studivo",
							style = MaterialTheme.typography.titleLarge.copy(
								fontWeight = FontWeight.Bold,
								color = Color(0xFF1976D2)
							)
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = Color.White
				)
			)
		},
		bottomBar = { BottomNavigationBar(navController) },
		floatingActionButton = {
			FloatingActionButton(
				onClick = { navController.navigate(AppRoutes.CreateRoutineScreen) },
				containerColor = Color(0xFF4CAF50),
				contentColor = Color.White,
				elevation = FloatingActionButtonDefaults.elevation(
					defaultElevation = 8.dp
				)
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Nueva rutina",
					modifier = Modifier.size(28.dp)
				)
			}
		},
		containerColor = Color(0xFFF8F9FA)
	) { innerPadding ->
		LazyColumn(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
			
			// Estadísticas rápidas
			item {
				QuickStatsCard()
			}
			
			// Sección de rutinas
			item {
				SectionTitle("Mis Rutinas")
			}
			
			// Lista de rutinas
			items(sampleRoutines) { rutina ->
				RoutineCard(rutina,navController)
			}
			
			// Espacio extra para el FAB
			item {
				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}


// --- Estadísticas rápidas ---
@Composable
fun QuickStatsCard() {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier.padding(20.dp)
		) {
			Text(
				text = "Esta Semana",
				style = MaterialTheme.typography.titleMedium.copy(
					fontWeight = FontWeight.SemiBold
				),
				color = Color.Black
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceEvenly
			) {
				StatItem(
					icon = Icons.Default.Timer,
					value = "2.5h",
					label = "Practicado",
					color = Color(0xFF2196F3)
				)
				StatItem(
					icon = Icons.Default.CheckCircle,
					value = "8",
					label = "Rutinas",
					color = Color(0xFF4CAF50)
				)
				StatItem(
					icon = Icons.Default.TrendingUp,
					value = "5",
					label = "Racha",
					color = Color(0xFFFF9800)
				)
			}
		}
	}
}

// --- Item de estadística ---
@Composable
fun StatItem(
	icon: ImageVector,
	value: String,
	label: String,
	color: Color
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.size(40.dp)
				.background(
					color = color.copy(alpha = 0.1f),
					shape = CircleShape
				),
			contentAlignment = Alignment.Center
		) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				tint = color,
				modifier = Modifier.size(20.dp)
			)
		}
		
		Spacer(modifier = Modifier.height(8.dp))
		
		Text(
			text = value,
			style = MaterialTheme.typography.titleMedium.copy(
				fontWeight = FontWeight.Bold
			),
			color = Color.Black
		)
		
		Text(
			text = label,
			style = MaterialTheme.typography.bodySmall,
			color = Color.Gray
		)
	}
}

// --- Título de sección ---
@Composable
fun SectionTitle(title: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge.copy(
				fontWeight = FontWeight.Bold
			),
			color = Color.Black,
			modifier = Modifier.weight(1f))
	}
}

// --- Componente reutilizable de tarjeta de rutina mejorado ---
@Composable
fun RoutineCard(rutina: Routinee, navController: NavController) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.height(100.dp),
		colors = CardDefaults.cardColors(
			containerColor = Color.White
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
		shape = RoundedCornerShape(16.dp),
		onClick = { navController.navigate(AppRoutes.DetailRoutineScreen) }
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Ícono circular con gradiente
			Box(
				modifier = Modifier
					.size(56.dp)
					.background(
						brush = Brush.linearGradient(
							colors = listOf(
								rutina.primaryColor,
								rutina.secondaryColor
							)
						),
						shape = CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = rutina.emoji,
					style = MaterialTheme.typography.headlineSmall
				)
			}
			
			Spacer(modifier = Modifier.width(16.dp))
			
			// Información de la rutina
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = rutina.name,
					style = MaterialTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.SemiBold
					),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					color = Color.Black
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						imageVector = Icons.Default.Timer,
						contentDescription = null,
						modifier = Modifier.size(16.dp),
						tint = Color.Gray
					)
					Spacer(modifier = Modifier.width(4.dp))
					Text(
						text = "${rutina.duration} min",
						style = MaterialTheme.typography.bodyMedium,
						color = Color.Gray
					)
					
					Spacer(modifier = Modifier.width(16.dp))
					
					Icon(
						imageVector = Icons.Default.Star,
						contentDescription = null,
						modifier = Modifier.size(16.dp),
						tint = if (rutina.isFavorite) Color(0xFFFFC107) else Color.Gray
					)
				}
			}
			
			// Botón de reproducir
			Box(
				modifier = Modifier
					.size(40.dp)
					.background(
						color = Color(0xFF4CAF50).copy(alpha = 0.1f),
						shape = CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = Icons.Default.PlayArrow,
					contentDescription = "Iniciar rutina",
					tint = Color(0xFF4CAF50),
					modifier = Modifier.size(24.dp)
				)
			}
		}
	}
}

// --- Datos de prueba mejorados ---
data class Routinee(
	val name: String,
	val duration: Int,
	val emoji: String,
	val primaryColor: Color,
	val secondaryColor: Color,
	val isFavorite: Boolean = false
)

val sampleRoutines = listOf(
	Routinee(
		name = "Calentamiento de manos",
		duration = 10,
		emoji = "🔥",
		primaryColor = Color(0xFFFF6B6B),
		secondaryColor = Color(0xFFFFE66D),
		isFavorite = true
	),
	Routinee(
		name = "Escalas y arpegios",
		duration = 20,
		emoji = "🎹",
		primaryColor = Color(0xFF4ECDC4),
		secondaryColor = Color(0xFF44A08D)
	),
	Routinee(
		name = "Lectura a primera vista",
		duration = 15,
		emoji = "👀",
		primaryColor = Color(0xFF667EEA),
		secondaryColor = Color(0xFF764BA2),
		isFavorite = true
	),
	Routinee(
		name = "Repertorio clásico",
		duration = 30,
		emoji = "🎼",
		primaryColor = Color(0xFFF093FB),
		secondaryColor = Color(0xFFF5576C)
	),
	Routinee(
		name = "Técnica avanzada",
		duration = 25,
		emoji = "⚡",
		primaryColor = Color(0xFF4FACFE),
		secondaryColor = Color(0xFF00F2FE)
	),
	Routinee(
		name = "Improvisación",
		duration = 20,
		emoji = "🎭",
		primaryColor = Color(0xFFA8EDEA),
		secondaryColor = Color(0xFFFED6E3)
	)
)