package com.example.studivo.domain.services


/**
 * Mensajes de notificación estilo Duolingo
 * Personalizados según el comportamiento del usuario
 */
object NotificationMessages {
	
	data class NotificationMessage(
		val title: String,
		val body: String
	)
	
	fun getMessage(daysWithoutStudy: Int, hasStreak: Boolean): NotificationMessage {
		return when {
			daysWithoutStudy == 0 -> getTodayMessages().random()
			daysWithoutStudy == 1 -> getYesterdayMessages().random()
			daysWithoutStudy in 2..3 -> getFewDaysMessages().random()
			daysWithoutStudy in 4..7 -> getWeekMessages().random()
			daysWithoutStudy in 8..14 -> getTwoWeeksMessages().random()
			else -> getLongTimeMessages().random()
		}
	}
	
	// ============================================
	// HOY (día actual sin completar)
	// ============================================
	private fun getTodayMessages() = listOf(
		NotificationMessage(
			"⏰ Todavía no has estudiado hoy",
			"¡Tu racha está en peligro! Completa una rutina rápida antes de que termine el día."
		),
		NotificationMessage(
			"🔥 ¡No pierdas tu racha!",
			"Solo unos minutos de estudio son suficientes. ¿Empezamos?"
		),
		NotificationMessage(
			"📚 Tu rutina te está esperando",
			"Aún estás a tiempo de mantener tu racha. ¡Solo toma una rutina!"
		),
		NotificationMessage(
			"⚡ Momento de estudiar",
			"Hoy no has practicado. ¡No dejes que el día termine sin estudiar!"
		),
		NotificationMessage(
			"🎯 Estás a un paso",
			"Completa una rutina rápida y mantén viva tu racha de estudio."
		),
		NotificationMessage(
			"🌟 El día no ha terminado",
			"Todavía tienes tiempo para mantener tu progreso. ¡Vamos, solo 10 minutos!"
		),
		NotificationMessage(
			"💡 Recordatorio amistoso",
			"Hoy aún no has estudiado. ¿Qué tal una sesión cortita antes de dormir?"
		),
		NotificationMessage(
			"🎵 Tu instrumento te extraña",
			"Dale unos minutos de práctica hoy. ¡Tu yo del futuro te lo agradecerá!"
		),
		NotificationMessage(
			"⌛ El reloj avanza...",
			"¡Quedan solo unas horas! No dejes que tu racha termine hoy."
		),
		NotificationMessage(
			"🚀 ¡Última llamada!",
			"El día está por terminar. Una rutina rápida y tu racha sigue intacta."
		),
		NotificationMessage(
			"✨ Tu talento necesita práctica",
			"Incluso los grandes artistas practican todos los días. ¿Empezamos?"
		),
		NotificationMessage(
			"🎼 La música te llama",
			"Tu instrumento está esperando. Solo unos minutos pueden hacer la diferencia."
		),
		NotificationMessage(
			"💪 Mantén el impulso",
			"Has llegado hasta aquí. ¡No rompas tu racha justamente hoy!"
		),
		NotificationMessage(
			"🏆 Campeones entrenan todos los días",
			"Y tú eres un campeón. ¿Qué tal si practicamos un ratito?"
		),
		NotificationMessage(
			"🌙 Antes de dormir...",
			"¿Qué tal si terminas el día con una sesión de práctica? ¡Solo 10 minutos!"
		)
	)
	
	// ============================================
	// AYER (perdió la racha de 1 día)
	// ============================================
	private fun getYesterdayMessages() = listOf(
		NotificationMessage(
			"😟 Te extrañamos ayer",
			"¡Volvamos al ritmo! Una sesión de estudio hoy puede recuperar tu impulso."
		),
		NotificationMessage(
			"📉 Tu racha se perdió ayer",
			"Pero no te preocupes, ¡hoy podemos empezar una nueva! ¿Qué dices?"
		),
		NotificationMessage(
			"💪 ¡De vuelta al juego!",
			"Ayer no estudiaste, pero hoy es un nuevo día para brillar."
		),
		NotificationMessage(
			"🔄 Segundo intento",
			"Todos tienen días difíciles. ¡Hoy puedes comenzar una nueva racha!"
		),
		NotificationMessage(
			"🌅 Nuevo amanecer, nueva oportunidad",
			"Ayer quedó atrás. Hoy es el día perfecto para retomar tu práctica."
		),
		NotificationMessage(
			"🎯 Sin presión, solo progreso",
			"No pasa nada por perder un día. ¡Lo importante es volver hoy!"
		),
		NotificationMessage(
			"💡 Un día no define tu progreso",
			"Lo que importa es la constancia. ¿Empezamos de nuevo?"
		),
		NotificationMessage(
			"🌱 Cada día es una nueva semilla",
			"Ayer se fue, pero hoy puedes plantar el inicio de una gran racha."
		),
		NotificationMessage(
			"⚡ ¡Rebote rápido!",
			"Los mejores músicos saben levantarse. ¿Listo para continuar?"
		),
		NotificationMessage(
			"🎵 Tu instrumento te perdonó",
			"Solo está esperando que vuelvas. ¡Dale algo de amor hoy!"
		)
	)
	
	// ============================================
	// POCOS DÍAS (2-3 días sin practicar)
	// ============================================
	private fun getFewDaysMessages() = listOf(
		NotificationMessage(
			"😅 Me tienes preocupado",
			"No he sabido de ti en días. ¿Retomamos juntos tu entrenamiento?"
		),
		NotificationMessage(
			"🎵 Te echo de menos",
			"Han pasado varios días sin que practiques. ¡Vuelve cuando estés listo!"
		),
		NotificationMessage(
			"💭 ¿Me olvidaste?",
			"Hace días que no estudias. Recuerda que la práctica constante es la clave."
		),
		NotificationMessage(
			"🤔 ¿Todo bien por ahí?",
			"Llevas algunos días sin practicar. Tu instrumento te está esperando."
		),
		NotificationMessage(
			"📱 Mensaje de tu yo del futuro",
			"'¿Por qué no practiqué esos días?' - No dejes que tu yo futuro piense esto."
		),
		NotificationMessage(
			"🎼 Las habilidades necesitan mantenimiento",
			"Como un jardín, tu técnica necesita atención regular. ¿Volvemos?"
		),
		NotificationMessage(
			"⏰ El tiempo vuela",
			"Ya pasaron varios días. Mientras más esperes, más difícil será volver."
		),
		NotificationMessage(
			"💔 Tu instrumento está triste",
			"Siente que lo has abandonado. ¿Qué tal si le das algo de cariño?"
		),
		NotificationMessage(
			"🌟 Aún estás a tiempo",
			"Unos días no borran tu progreso. ¡Pero es hora de volver!"
		),
		NotificationMessage(
			"🔔 Toc toc... ¿Hay alguien ahí?",
			"Solo vengo a recordarte que tu práctica te extraña."
		),
		NotificationMessage(
			"🎯 La disciplina es un músculo",
			"Y está perdiendo fuerza. ¡Entrenémoslo de nuevo juntos!"
		),
		NotificationMessage(
			"🌊 No dejes que se forme óxido",
			"Tus dedos necesitan movimiento. Una sesión corta es todo lo que necesitas."
		),
		NotificationMessage(
			"📖 Cada día cuenta",
			"Los días se están acumulando. ¿Qué tal si hoy rompemos la racha... de no practicar?"
		),
		NotificationMessage(
			"🚶 Pequeños pasos, grandes logros",
			"No necesitas una sesión maratónica. Solo 5 minutos hoy pueden cambiar todo."
		),
		NotificationMessage(
			"🎭 El escenario te espera",
			"Pero primero necesitas práctica. ¿Nos vemos hoy en el estudio?"
		)
	)
	
	// ============================================
	// UNA SEMANA (4-7 días sin practicar)
	// ============================================
	private fun getWeekMessages() = listOf(
		NotificationMessage(
			"😢 Ya casi es una semana...",
			"Llevas una semana sin estudiar. ¿Qué tal si empezamos de nuevo hoy?"
		),
		NotificationMessage(
			"🌱 Todo crecimiento necesita constancia",
			"Ha pasado tiempo desde tu última práctica. ¿Volvemos a intentarlo?"
		),
		NotificationMessage(
			"🔄 Nunca es tarde para volver",
			"Una semana es mucho tiempo. Pero estoy aquí cuando quieras retomar."
		),
		NotificationMessage(
			"😔 Ha sido una semana larga",
			"Sin tu práctica diaria. ¿Qué pasó? ¿Puedo ayudarte a volver?"
		),
		NotificationMessage(
			"🎵 Tu música interior está en silencio",
			"Una semana sin tocar es demasiado. ¡Dale voz de nuevo!"
		),
		NotificationMessage(
			"⚠️ Alerta: Hábito en peligro",
			"Llevas 7 días sin practicar. Los hábitos se rompen más rápido de lo que se construyen."
		),
		NotificationMessage(
			"💡 ¿Perdiste la motivación?",
			"Es normal. Pero recuerda por qué empezaste. Una semana sin practicar es mucho."
		),
		NotificationMessage(
			"🌧️ Después de la tormenta...",
			"Viene la calma. Sea lo que sea que te detuvo, hoy puedes volver."
		),
		NotificationMessage(
			"🎼 La música no juzga",
			"No importa cuánto tiempo pasó. Tu instrumento te espera sin reproches."
		),
		NotificationMessage(
			"🔑 La clave está en regresar",
			"No en cuánto tiempo pasó. Una semana no es nada si vuelves hoy."
		),
		NotificationMessage(
			"🏃 El momento perfecto es ahora",
			"Has dejado pasar una semana. Pero hoy, justo ahora, puedes cambiar eso."
		),
		NotificationMessage(
			"📉 Tu progreso está en pausa",
			"Y solo tú puedes presionar 'play' de nuevo. ¿Qué dices?"
		),
		NotificationMessage(
			"🤗 Sin juicios, solo apoyo",
			"Sé que la vida puede ser complicada. Pero estoy aquí para cuando estés listo."
		),
		NotificationMessage(
			"⏳ Una semana perdida...",
			"Pero un futuro por ganar. ¿Empezamos hoy mismo?"
		),
		NotificationMessage(
			"🎯 Reset necesario",
			"Una semana es señal de que algo cambió. Pero puedes retomar tu camino ahora."
		)
	)
	
	// ============================================
	// DOS SEMANAS (8-14 días sin practicar)
	// ============================================
	private fun getTwoWeeksMessages() = listOf(
		NotificationMessage(
			"😰 Han pasado dos semanas...",
			"Esto es serio. Tu progreso está en riesgo. ¿Podemos hablar?"
		),
		NotificationMessage(
			"💔 Esto se está poniendo difícil",
			"Dos semanas sin ti. ¿Qué puedo hacer para que vuelvas?"
		),
		NotificationMessage(
			"🚨 Alerta crítica",
			"Llevas dos semanas sin practicar. Estás perdiendo lo que tanto trabajaste."
		),
		NotificationMessage(
			"🎵 El silencio es ensordecedor",
			"Dos semanas sin música. ¿Qué te detuvo? Hablemos de ello."
		),
		NotificationMessage(
			"⚡ Chispa perdida",
			"La pasión que tenías está ahí, solo necesita una pequeña chispa. ¿La encendemos?"
		),
		NotificationMessage(
			"🌑 En la oscuridad más profunda...",
			"Es cuando más necesitas la luz de tu música. Vuelve, por favor."
		),
		NotificationMessage(
			"🔥 El fuego se apaga",
			"Pero aún quedan brasas. Sopla un poco y volverá a encenderse."
		),
		NotificationMessage(
			"💪 Esto requiere valentía",
			"Volver después de dos semanas no es fácil. Pero sé que puedes hacerlo."
		),
		NotificationMessage(
			"🎯 Objetivo: Sobrevivir",
			"Olvidemos las rachas. Solo toca hoy. Un día a la vez."
		),
		NotificationMessage(
			"🌅 Un nuevo comienzo",
			"Dos semanas es mucho, pero no es el final. Hoy puede ser tu día uno."
		),
		NotificationMessage(
			"📞 ¿Sigues ahí?",
			"Solo quiero saber si estás bien. Tu música puede esperar, pero yo me preocupo."
		),
		NotificationMessage(
			"🎼 La música es medicina",
			"Y llevas dos semanas sin tu dosis. ¿No crees que es hora?"
		),
		NotificationMessage(
			"🤲 Con los brazos abiertos",
			"Te espero sin importar cuánto tiempo haya pasado. Vuelve cuando puedas."
		),
		NotificationMessage(
			"⏰ El reloj sigue corriendo",
			"Dos semanas se convirtieron en un hábito de NO practicar. Rómpelo hoy."
		),
		NotificationMessage(
			"🌟 Todavía brillas",
			"Aunque lleves dos semanas sin practicar. Tu talento sigue ahí, esperándote."
		)
	)
	
	// ============================================
	// MUCHO TIEMPO (15+ días sin practicar)
	// ============================================
	private fun getLongTimeMessages() = listOf(
		NotificationMessage(
			"🕰️ ¡Cuánto tiempo!",
			"Han pasado semanas sin que practiques. ¿Todo está bien?"
		),
		NotificationMessage(
			"💌 Sigo aquí esperándote",
			"Hace mucho que no estudias, pero cuando estés listo, estaré aquí."
		),
		NotificationMessage(
			"🌟 El momento perfecto es ahora",
			"No importa cuánto tiempo haya pasado, ¡hoy podemos empezar de nuevo!"
		),
		NotificationMessage(
			"🤗 Te extraño muchísimo",
			"Hace tanto que no vienes... ¿Qué tal si hacemos una rutina cortita juntos?"
		),
		NotificationMessage(
			"😢 ¿Esto es un adiós?",
			"Hace tanto tiempo... Pero aún tengo esperanza de que vuelvas."
		),
		NotificationMessage(
			"🎵 La música nunca te abandona",
			"Aunque tú la hayas dejado. Siempre estará ahí cuando decidas volver."
		),
		NotificationMessage(
			"💭 Recuerdo cuando empezaste",
			"Con tanta ilusión. ¿Qué pasó con ese entusiasmo?"
		),
		NotificationMessage(
			"🌱 Las raíces siguen ahí",
			"Aunque la planta parezca seca. Un poco de agua y volverá a florecer."
		),
		NotificationMessage(
			"🔮 En un universo paralelo...",
			"Tu yo alternativo nunca dejó de practicar. ¿Quieres alcanzarlo?"
		),
		NotificationMessage(
			"📜 Tu historia no ha terminado",
			"Solo está en pausa. ¿Cuándo escribiremos el siguiente capítulo?"
		),
		NotificationMessage(
			"🎭 El escenario sigue esperando",
			"Las luces están apagadas, pero pueden volver a encenderse. Solo di cuándo."
		),
		NotificationMessage(
			"⚓ Anclado en el pasado",
			"Tu último progreso fue hace semanas. ¿No crees que es hora de zarpar de nuevo?"
		),
		NotificationMessage(
			"🌊 Como el mar que va y viene",
			"Puedes volver. La música es paciente, pero tu tiempo no lo es."
		),
		NotificationMessage(
			"🕊️ Sin presión, sin culpa",
			"Solo una invitación abierta. Cuando quieras volver, estaré aquí."
		),
		NotificationMessage(
			"🎪 El show debe continuar",
			"Pero falta el artista principal. ¿Ese eres tú? Te estamos esperando."
		),
		NotificationMessage(
			"💎 Lo valioso no se olvida",
			"Como tu talento. Está ahí, solo necesita ser pulido de nuevo."
		),
		NotificationMessage(
			"🌈 Después de la ausencia",
			"Viene el reencuentro. Y puede ser hermoso si decides volver hoy."
		),
		NotificationMessage(
			"🎁 Un regalo para ti",
			"La oportunidad de volver. Sin juicios, sin presiones. Solo música."
		),
		NotificationMessage(
			"🏔️ La cima parece lejos",
			"Desde aquí abajo. Pero cada viaje comienza con un paso. ¿Lo damos hoy?"
		),
		NotificationMessage(
			"🔊 Rompe el silencio",
			"Llevas demasiado tiempo sin hacer música. Hoy puede ser el día del cambio."
		),
		NotificationMessage(
			"⭐ Nunca es demasiado tarde",
			"Para volver a algo que amas. Tu música te espera con paciencia infinita."
		),
		NotificationMessage(
			"🎼 La partitura está abierta",
			"En la primera página. Como si nunca te hubieras ido. ¿Empezamos?"
		),
		NotificationMessage(
			"🌻 Como una flor dormida",
			"Tu habilidad espera el momento de florecer de nuevo. ¿Será hoy?"
		),
		NotificationMessage(
			"🕊️ Libertad para elegir",
			"Volver o no volver. Pero recuerda por qué empezaste este viaje."
		),
		NotificationMessage(
			"🎯 Sin metas imposibles",
			"Solo un pequeño paso hoy. Eso es todo lo que necesitas para volver."
		)
	)
}