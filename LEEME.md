# GuiTracker 1.3

App nativa Android, en español, sin servidor, sin base de datos y sin permiso de internet. Android 8 o posterior; compilada para Android 16 (API 36).

## Instalación

Pasá GuiTracker.apk al teléfono, abrilo desde Mis archivos y autorizá a esa aplicación a instalarlo cuando Android lo solicite. Configurá el perfil y autorizá las notificaciones al guardar recordatorios. Es una APK firmada para instalación directa, fuera de Google Play.

## Cambios de la versión 1.3

Menú y pantallas secundarias con tarjetas, campos y colores coherentes con la pantalla principal. Acerca de muestra GuiTracker, la versión instalada, el código de versión y GPL v3, con texto completo disponible sin conexión. El código y los recursos propios se distribuyen bajo GPL-3.0-only; ver LICENSE.txt.

## Cambios de la versión 1.2

Pantalla principal renovada: tarjeta de peso con fondo degradado, tarjetas de IMC y meta, tipografía y espaciado cuidados, gráfico con relleno suave y leyenda, y botón de registro destacado. Conserva las funciones y el tema automático.

## Cambios de la versión 1.1

Registrar peso se encuentra al final de la pantalla, debajo del gráfico y sus flechas pequeñas. El menú de tres puntos reúne Perfil y meta, Recordatorios, Importar CSV, Exportar CSV e Historial. El historial tiene su propia pantalla, con flecha para volver. La APK conserva el identificador y la firma de la versión anterior; instalala como actualización sin desinstalar.

## Uso

- Perfil: nombre, fecha de nacimiento (18 años o más), altura en cm y meta opcional; hasta un decimal.
- Registrar peso: fecha y kg, entre 20 y 500; hasta un decimal. Una fecha repetida reemplaza su valor. Tocá un registro del historial para editarlo o eliminarlo.
- Gráfico: ventanas de 365 días y línea dorada para la meta. No se inventan mediciones para los días vacíos; se unen las existentes.
- Recordatorios: elegí varios días y una hora común. Ningún día seleccionado los desactiva. Se reprograman tras reiniciar o cambiar la zona horaria. Android puede demorar los avisos por ahorro de batería; forzar la detención de la app impide avisos hasta volver a abrirla y guardar recordatorios.
- El tema sigue el del teléfono.

## Datos y CSV

Los datos se guardan en un archivo JSON privado de la app. La copia automática de Android está desactivada. Desinstalar o borrar el almacenamiento elimina los datos. Exportá regularmente y conservá una copia fuera del teléfono.

El CSV contiene únicamente las mediciones; no contiene nombre, nacimiento, altura, meta ni recordatorios. Estos se configuran nuevamente al cambiar de teléfono. Formato UTF-8, fechas ISO, punto decimal:

```csv
fecha,peso_kg
2026-10-22,75.4
2026-10-23,75.2
```

La importación valida todo antes de pedir confirmación. Conserva las otras fechas y reemplaza las coincidentes. No acepta fechas futuras ni pesos fuera del rango. Las fechas duplicadas en el archivo conservan el último valor.

## IMC

Se calcula como peso en kg / altura en metros al cuadrado. Categorías orientativas: menos de 18,5, bajo peso; desde 18,5 hasta menos de 25, peso normal; desde 25 hasta menos de 30, sobrepeso; desde 30, obesidad. No es un diagnóstico. Referencia para personas adultas: https://www.canada.ca/en/health-canada/services/food-nutrition/healthy-eating/healthy-weights/canadian-guidelines-body-weight-classification-adults/quick-reference-tool-professionals.html

Recordatorios: https://developer.android.com/develop/background-work/services/alarms

## Compilar

El proyecto incluye archivos para Android Studio/Gradle (plugin 8.6.1, JDK 17, SDK 36). También incluye build-apk.ps1 para compilar sin Gradle, con JDK 17, Build Tools 36 y android.jar API 36. Ejecutá el script con las rutas Jdk, BuildTools, AndroidJar, KeyStore, StorePassword y opcionalmente OutDir. El alias de firma es `guitracker`. AndroidClasses permite indicar una carpeta con android.jar descomprimido si el entorno restringe la resolución de archivos ZIP de Java.

La clave de esta primera versión se conserva por separado para futuras actualizaciones; usá la misma clave y aumentá versionCode al actualizar. El CSV permite transferir mediciones si necesitás reinstalar con otra firma.

## Verificación

Se verifican compilación Java, conversión DEX, empaquetado API 36, alineación y firma APK. Los tests de DomainTest.java pasan 25 comprobaciones de validación, categorías, calendario e importación CSV. No se ha ejecutado esta versión en un teléfono ni en un emulador. La visualización y los avisos con la pantalla apagada requieren una prueba en el Samsung A34.
