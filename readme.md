# TV Accesible Android - TV en vivo

Aplicación Android diseñada para facilitar el acceso a contenido de televisión mediante una interfaz sencilla y directa. Evitando registros o menus innecesarios, con especial atención a las necesidades de las personas adultas mayores.

## Objetivo

Reducir la complejidad de uso al mínimo, priorizando la reproducción inmediata del contenido audiovisual y una navegación accesible.

## Características

- Reproducción de canales IPTV mediante streams M3U8.
- Carga de canales desde una fuente JSON en línea.
- Actualización de la lista de canales cada cinco minutos mientras la aplicación está activa.
- Inicio de la reproducción al abrir la aplicación.
- Navegación entre canales mediante controles compatibles con Android TV.
- Reproducción a pantalla completa.
- Visualización de información del canal.

## Tecnologías

- **Plataforma:** Android
- **Lenguaje:** Kotlin
- **Reproducción multimedia:** AndroidX Media3 / ExoPlayer
- **Interfaz:** Jetpack Compose
- **Entorno de desarrollo:** Android Studio

## Descargas

Consulta las versiones publicadas y descarga el APK desde la sección de [Releases](../../releases).

## Instalación

1. Descarga el APK de la versión deseada desde GitHub Releases.
2. Instala el APK en un dispositivo Android compatible.
3. Abre la aplicación y comprueba la disponibilidad de los canales configurados.

## Desarrollo

Para obtener el código fuente:

```bash
git clone https://github.com/erihucor/tv-accesible-reto-ANDROID
```

Abre el proyecto en Android Studio, sincroniza las dependencias de Gradle y ejecútalo en un dispositivo compatible o emulador.

## Historial de cambios

Consulta el archivo [CHANGELOG.md](CHANGELOG.md) para conocer las modificaciones de cada versión.

## Antecedentes

El proyecto comenzó como una aplicación web. Esa versión fue descontinuada debido a las limitaciones del navegador del sistema y al control limitado sobre el entorno de ejecución.

La versión Android representa una reimplementación nativa orientada a mejorar la reproducción multimedia y la experiencia de uso en dispositivos Android y Android TV.
