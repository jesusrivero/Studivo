package com.example.studivo.domain.model.helpers


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * Helper para gestionar optimización de batería en diferentes fabricantes
 */
object BatteryOptimizationHelper {
	
	enum class Manufacturer {
		XIAOMI, HUAWEI, SAMSUNG, OPPO, VIVO, ONEPLUS, ASUS, SONY, GENERIC
	}
	
	fun getCurrentManufacturer(): Manufacturer {
		val manufacturer = Build.MANUFACTURER.lowercase()
		return when {
			manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> Manufacturer.XIAOMI
			manufacturer.contains("huawei") || manufacturer.contains("honor") -> Manufacturer.HUAWEI
			manufacturer.contains("samsung") -> Manufacturer.SAMSUNG
			manufacturer.contains("oppo") || manufacturer.contains("realme") -> Manufacturer.OPPO
			manufacturer.contains("vivo") -> Manufacturer.VIVO
			manufacturer.contains("oneplus") -> Manufacturer.ONEPLUS
			manufacturer.contains("asus") -> Manufacturer.ASUS
			manufacturer.contains("sony") -> Manufacturer.SONY
			else -> Manufacturer.GENERIC
		}
	}
	
	fun getInstructions(manufacturer: Manufacturer): List<String> {
		return when (manufacturer) {
			Manufacturer.XIAOMI -> listOf(
				"1. Ve a Configuración > Apps > Administrar apps",
				"2. Busca y selecciona Studivo",
				"3. Selecciona 'Ahorro de batería' y elige 'Sin restricciones'",
				"4. Ve a 'Inicio automático' y actívalo",
				"5. Ve a 'Aplicaciones en segundo plano' y permite que Studivo se ejecute",
				"6. En 'Permisos', permite 'Mostrar ventanas emergentes'",
				"⚠️ MIUI es muy agresivo con apps en segundo plano"
			)
			
			Manufacturer.HUAWEI -> listOf(
				"1. Ve a Configuración > Batería > Inicio de aplicaciones",
				"2. Busca Studivo y desactiva 'Administración automática'",
				"3. Activa manualmente: Inicio automático, Actividad secundaria, Ejecutar en segundo plano",
				"4. Ve a Configuración > Apps > Studivo",
				"5. En 'Batería', selecciona 'Sin restricciones'",
				"⚠️ EMUI/HarmonyOS puede requerir configuración adicional"
			)
			
			Manufacturer.SAMSUNG -> listOf(
				"1. Ve a Configuración > Batería y cuidado del dispositivo",
				"2. Selecciona 'Batería' > 'Límites de uso de batería en segundo plano'",
				"3. Asegúrate de que Studivo NO esté en 'Apps en reposo' o 'Apps en reposo profundo'",
				"4. Ve a Configuración > Apps > Studivo > Batería",
				"5. Selecciona 'Sin restricciones'",
				"6. Vuelve atrás y en 'Aplicaciones en reposo', excluye Studivo"
			)
			
			Manufacturer.OPPO -> listOf(
				"1. Ve a Configuración > Batería > Ahorro de energía de la aplicación",
				"2. Busca Studivo y selecciona 'Sin restricciones'",
				"3. Ve a Configuración > Privacidad > Permisos > Inicio automático",
				"4. Activa Studivo",
				"5. En 'Administrador de aplicaciones en segundo plano', permite Studivo"
			)
			
			Manufacturer.VIVO -> listOf(
				"1. Ve a Configuración > Batería > Consumo de energía en segundo plano",
				"2. Busca Studivo y permite el consumo",
				"3. Ve a Configuración > Más ajustes > Permisos > Inicio automático",
				"4. Activa Studivo",
				"5. En 'Administrador de aplicaciones', permite que Studivo se ejecute en segundo plano"
			)
			
			Manufacturer.ONEPLUS -> listOf(
				"1. Ve a Configuración > Batería > Optimización de batería",
				"2. Selecciona 'Todas las apps' en el menú desplegable",
				"3. Busca Studivo y selecciona 'No optimizar'",
				"4. Ve a Configuración > Apps > Studivo > Batería",
				"5. Selecciona 'Sin restricciones'"
			)
			
			Manufacturer.ASUS -> listOf(
				"1. Ve a Configuración > Batería > PowerMaster",
				"2. Selecciona 'Inicio automático'",
				"3. Activa Studivo",
				"4. Vuelve y selecciona 'Apps en espera'",
				"5. Asegúrate de que Studivo NO esté en la lista"
			)
			
			Manufacturer.SONY -> listOf(
				"1. Ve a Configuración > Batería",
				"2. Toca el menú (⋮) y selecciona 'Optimización de batería'",
				"3. Selecciona 'Todas las apps'",
				"4. Busca Studivo y selecciona 'No optimizar'"
			)
			
			Manufacturer.GENERIC -> listOf(
				"1. Ve a Configuración > Apps > Studivo",
				"2. Selecciona 'Batería' o 'Uso de batería'",
				"3. Elige 'Sin restricciones' o 'No optimizar'",
				"4. Si hay opciones de 'Inicio automático' o 'Ejecución en segundo plano', actívalas"
			)
		}
	}
	
	fun openBatterySettings(context: Context, manufacturer: Manufacturer) {
		try {
			val intent = when (manufacturer) {
				Manufacturer.XIAOMI -> {
					// Intentar abrir configuración específica de MIUI
					Intent().apply {
						component = android.content.ComponentName(
							"com.miui.securitycenter",
							"com.miui.permcenter.autostart.AutoStartManagementActivity"
						)
					}
				}
				
				Manufacturer.HUAWEI -> {
					Intent().apply {
						component = android.content.ComponentName(
							"com.huawei.systemmanager",
							"com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
						)
					}
				}
				
				Manufacturer.SAMSUNG -> {
					Intent().apply {
						action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
						data = Uri.parse("package:${context.packageName}")
					}
				}
				
				else -> {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
							data = Uri.parse("package:${context.packageName}")
						}
					} else {
						Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
							data = Uri.parse("package:${context.packageName}")
						}
					}
				}
			}
			context.startActivity(intent)
		} catch (e: Exception) {
			// Fallback: configuración general de apps
			try {
				val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
					data = Uri.parse("package:${context.packageName}")
				}
				context.startActivity(intent)
			} catch (e: Exception) {
				// Último fallback: configuración general
				val intent = Intent(Settings.ACTION_SETTINGS)
				context.startActivity(intent)
			}
		}
	}
}