package co.edu.unab.sebastianlizcano.unabgo.data.local

/**
 * Catálogo estático de programas oficiales UNAB con URLs reales verificadas
 * (todas siguen el patrón https://unab.edu.co/programas/{slug}/).
 *
 * Datos extraídos directamente del sitio oficial el 20 de mayo de 2026.
 * Si UNAB añade/elimina programas, actualizar esta lista.
 *
 * No depende de la web en runtime: el catálogo está embebido en la app
 * para que la pantalla "Quiero ser UNAB" cargue al instante, sin WebView
 * y sin rotate-warning.
 */

data class ProgramItem(
    val slug: String,
    val name: String,
    val shortDescription: String,
    val emoji: String = "🎓"
) {
    val url: String get() = "https://unab.edu.co/programas/$slug/"
}

data class Modality(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val color: Long,             // Color de la card (ARGB)
    val externalUrl: String?,    // Página general (cuando no listamos uno-a-uno)
    val programs: List<ProgramItem>
)

object UnabPrograms {

    val PREGRADOS = Modality(
        id          = "pregrados",
        name        = "Pregrados",
        description = "Programas profesionales universitarios",
        emoji       = "🎓",
        color       = 0xFF8E5BFF,
        externalUrl = "https://unab.edu.co/pregrados/",
        programs = listOf(
            // Facultad de Ingenierías
            ProgramItem("ingenieria-biomedica",
                "Ingeniería Biomédica",
                "Aplica ingeniería al cuidado de la salud y dispositivos médicos.",
                "⚕️"),
            ProgramItem("ingenieria-financiera",
                "Ingeniería Financiera",
                "Modelado matemático, finanzas y mercados de capitales.",
                "💰"),
            ProgramItem("ingenieria-industrial",
                "Ingeniería Industrial",
                "Optimización de procesos, producción y logística.",
                "🏭"),
            ProgramItem("ingenieria-mecatronica",
                "Ingeniería Mecatrónica",
                "Mecánica, electrónica y control automático integrados.",
                "🤖"),
            ProgramItem("ingenieria-de-sistemas",
                "Ingeniería de Sistemas",
                "Software, redes, ciberseguridad y desarrollo de aplicaciones.",
                "💻"),
            ProgramItem("ingenieria-en-energia-y-sostenibilidad",
                "Ingeniería en Energía y Sostenibilidad",
                "Energías renovables y soluciones sostenibles.",
                "⚡"),

            // Facultad de Ciencias de la Salud
            ProgramItem("medicina",
                "Medicina",
                "Formación médica integral con énfasis en investigación.",
                "🩺"),
            ProgramItem("enfermeria",
                "Enfermería",
                "Cuidado profesional de salud humanizado.",
                "💉"),
            ProgramItem("psicologia",
                "Psicología",
                "Comprensión del comportamiento humano e intervención.",
                "🧠"),

            // Facultad de Ciencias Económicas, Administrativas y Contables
            ProgramItem("administracion-de-empresas",
                "Administración de Empresas",
                "Liderazgo, gestión empresarial y emprendimiento.",
                "📊"),
            ProgramItem("contaduria-publica",
                "Contaduría Pública",
                "Contabilidad, auditoría y normativa fiscal.",
                "📒"),
            ProgramItem("economia",
                "Economía",
                "Análisis macro y microeconómico, mercados y políticas.",
                "📈"),
            ProgramItem("negocios-internacionales",
                "Negocios Internacionales",
                "Comercio global, importación, exportación y mercados.",
                "🌎"),
            ProgramItem("marketing",
                "Marketing",
                "Estrategias de mercado, branding y experiencia del cliente.",
                "🎯"),

            // Facultad de Ciencias Sociales, Humanidades y Artes
            ProgramItem("derecho",
                "Derecho",
                "Sistema jurídico colombiano y ejercicio profesional.",
                "⚖️"),
            ProgramItem("comunicacion-social",
                "Comunicación Social",
                "Periodismo, comunicación organizacional y digital.",
                "📰"),
            ProgramItem("musica",
                "Música",
                "Formación musical, interpretación y composición.",
                "🎵"),
            ProgramItem("artes-audiovisuales",
                "Artes Audiovisuales",
                "Producción de cine, video y contenido digital.",
                "🎬"),
            ProgramItem("gastronomia",
                "Gastronomía",
                "Artes culinarias, gestión y técnicas internacionales.",
                "🍳")
        )
    )

    val POSGRADOS = Modality(
        id          = "posgrados",
        name        = "Posgrados",
        description = "Especializaciones, maestrías y doctorados",
        emoji       = "🏆",
        color       = 0xFF4361EE,
        externalUrl = "https://unab.edu.co/posgrado/",
        programs    = emptyList()   // muy extensos: redirigimos a la pagina oficial
    )

    val TECNICOS = Modality(
        id          = "tecnicos",
        name        = "Técnicos y Tecnologías",
        description = "Formación práctica de corta duración",
        emoji       = "🔧",
        color       = 0xFFFF9F40,
        externalUrl = "https://unab.edu.co/programas-tecnicos-y-tecnologias/",
        programs    = emptyList()
    )

    val VIRTUALES = Modality(
        id          = "virtuales",
        name        = "Programas Virtuales",
        description = "Educación 100% en línea",
        emoji       = "🌐",
        color       = 0xFF2EC4B6,
        externalUrl = "https://unab.edu.co/programas-virtuales/",
        programs    = emptyList()
    )

    val EDUCACION_CONTINUA = Modality(
        id          = "educacion-continua",
        name        = "Educación Continua",
        description = "Cursos, diplomados y formación corporativa",
        emoji       = "📚",
        color       = 0xFFE94BC4,
        externalUrl = "https://unab.edu.co/educacion-continua/",
        programs    = emptyList()
    )

    val ALL = listOf(PREGRADOS, POSGRADOS, TECNICOS, VIRTUALES, EDUCACION_CONTINUA)

    fun findById(id: String): Modality? = ALL.firstOrNull { it.id == id }
}
