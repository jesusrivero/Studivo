
# 📚 Studivo

[![Kotlin](https://img.shields.io/badge/Kotlin-FF5722?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Hilt-0D47A1?style=for-the-badge&logo=android&logoColor=white)](https://dagger.dev/hilt/)


**Studivo** es una aplicación móvil diseñada para gestionar rutinas de estudio de manera **eficiente y gamificada**.  
Permite crear, ejecutar y compartir rutinas, ver estadísticas de progreso y mantener rachas de estudio.

La app está desarrollada en **Android** usando **Kotlin** y **Jetpack Compose**, con arquitectura basada en **Clean Architecture** para un código modular y escalable.

---

## 🔹 Funcionalidades principales

### 📝 Gestión de rutinas
- Crear, editar y eliminar rutinas de estudio.
- Organizar las rutinas por fases y duraciones.
- Marcar rutinas como completadas.
- **Compartir rutinas mediante QR** para enviar rutinas a otros usuarios.

### ⏱ Reproducción de rutinas
- Reproducción de rutinas con temporizador por fase.
- Integración de **metróno** para marcar el ritmo de estudio.
- Pausar, reanudar o reiniciar rutinas en ejecución.

### 📊 Seguimiento de progreso
- Registrar progreso diario y porcentaje completado.
- Historial de progreso por rutina y por día.
- Visualización de calendario con progreso diario.
- Rachas de estudio (streaks) para mantener motivación.

### ⚙️ Configuración de la app
- Modo oscuro / claro.
- Sección de información: Sobre nosotros, Políticas de privacidad y Términos y condiciones (mostradas en modal dentro de la app).


## 🔹 Tecnologías y librerías

- **Kotlin** - Lenguaje principal.
- **Jetpack Compose** - UI declarativa moderna.
- **Material 3** - Componentes de UI con diseño moderno.
- **Hilt** → inyección de dependencias.
- **Room** → base de datos local opcional para progreso offline.
- **DataStore / SharedPreferences** → persistencia de configuraciones.
- **Librerías de QR** → generación y escaneo de códigos para compartir rutinas.
- **Librerías de Audio** → metróno para marcar el ritmo durante las rutinas.


