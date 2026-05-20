package co.edu.unab.sebastianlizcano.unabgo.data.repository

/**
 * Base de conocimiento ESTÁTICA y VERIFICADA de la UNAB.
 *
 * Los datos se extrajeron directamente del sitio oficial (https://unab.edu.co/)
 * el 20 de mayo de 2026. Este archivo se inyecta en el system prompt de Banu
 * para evitar que la IA "alucine" carreras o datos inexistentes.
 *
 * Si la UNAB añade o elimina programas, actualizar esta lista.
 */
object UnabKnowledge {

    /** Programas de PREGRADO oficialmente ofrecidos por la UNAB. */
    val PREGRADO = listOf(
        // Facultad de Ingeniería
        "Ingeniería Biomédica",
        "Ingeniería Financiera",
        "Ingeniería Industrial",
        "Ingeniería Mecatrónica",
        "Ingeniería de Sistemas",
        "Ingeniería en Energía y Sostenibilidad",

        // Facultad de Ciencias de la Salud
        "Medicina",
        "Enfermería",
        "Psicología",

        // Facultad de Ciencias Económicas, Administrativas y Contables
        "Administración de Empresas",
        "Contaduría Pública",
        "Economía",
        "Negocios Internacionales",
        "Marketing",

        // Facultad de Ciencias Sociales, Humanidades y Artes
        "Derecho",
        "Comunicación Social",
        "Música",
        "Artes Audiovisuales",
        "Gastronomía",

        // Facultad de Educación
        "Licenciatura en Educación Infantil",
        "Licenciatura en Ciencias Sociales",
        "Licenciatura en Lenguas Extranjeras con Énfasis en Inglés y Español"
    )

    /** Programas de POSGRADO destacados (lista no exhaustiva). */
    val POSGRADO = listOf(
        // Doctorados
        "Doctorado en Derecho",
        "Doctorado en Educación",
        "Doctorado en Ingeniería",
        "Doctorado en Sostenibilidad",

        // Maestrías (selección)
        "Maestría en Administración (MBA)",
        "Maestría en Educación",
        "Maestría en Ingeniería de Sistemas e Informática",
        "Maestría en Salud Pública",
        "Maestría en Derecho",

        // Especializaciones (selección)
        "Especialización en Ciberseguridad Organizacional",
        "Especialización en Auditoría en Salud",
        "Especialización en Derecho Penal",
        "Especialización en Gerencia de Recursos Energéticos",
        "Especialización en Gestión Humana"
    )

    /** Información institucional verificada. */
    const val INSTITUCIONAL = """
- Nombre oficial: Universidad Autónoma de Bucaramanga (UNAB).
- Ubicación: Bucaramanga, Santander, Colombia.
- Sitio web oficial: https://unab.edu.co
- Admisiones: admisiones@unab.edu.co
- Línea de atención: (57) 607 6436111
- Modalidad: Presencial y virtual.
- Acreditación: Institución de Educación Superior acreditada en alta calidad
  por el Ministerio de Educación Nacional de Colombia.
"""

    /** Lista de carreras que la gente PREGUNTA pero que NO existen en la UNAB. */
    val NO_OFRECIDAS_EJEMPLOS = listOf(
        "Ingeniería Mecánica (la UNAB ofrece Ingeniería Mecatrónica, que es distinta)",
        "Ingeniería Civil",
        "Ingeniería Electrónica",
        "Ingeniería Química",
        "Ingeniería Ambiental",
        "Ingeniería Aeronáutica",
        "Arquitectura",
        "Odontología",
        "Veterinaria",
        "Fisioterapia",
        "Nutrición y Dietética",
        "Optometría",
        "Sociología",
        "Antropología",
        "Filosofía"
    )

    /** Bloque listo para inyectar al system prompt. */
    fun asPromptBlock(): String = buildString {
        appendLine("## INFORMACIÓN VERIFICADA DE LA UNAB (fuente oficial unab.edu.co)")
        appendLine()
        appendLine("### Datos institucionales")
        append(INSTITUCIONAL.trimIndent())
        appendLine()
        appendLine()
        appendLine("### Programas de PREGRADO que SI ofrece la UNAB (lista exhaustiva)")
        PREGRADO.forEach { appendLine("- $it") }
        appendLine()
        appendLine("### Programas de POSGRADO destacados (existen más, consultar web)")
        POSGRADO.forEach { appendLine("- $it") }
        appendLine()
        appendLine("### CARRERAS QUE NO EXISTEN EN LA UNAB (NO LAS INVENTES)")
        appendLine("Si el usuario pregunta por estas, di claramente que NO se ofrecen y")
        appendLine("sugiere alguna de la lista de pregrado de arriba si es similar:")
        NO_OFRECIDAS_EJEMPLOS.forEach { appendLine("- $it") }
    }
}
