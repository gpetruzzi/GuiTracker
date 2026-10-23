# GuiTracker

Aplicación Android para registrar tu peso y seguir tu evolución. Funciona sin internet, sin cuenta y sin servidor. Licencia **GPL-3.0-only**.

## Descargar

[Descargar GuiTracker 1.3 para Android](downloads/GuiTracker-1.3.apk?raw=true)

Abrí la APK en tu teléfono y autorizá la instalación desde esa fuente cuando Android lo solicite. Para actualizar, instalala sobre la versión anterior sin desinstalar, conservando tus registros.

## Funciones

- Una medición diaria de peso en kg, con un decimal; permite editar y eliminar.
- Perfil con nombre, nacimiento, altura en cm y peso meta.
- IMC y categoría orientativa para adultos, con aviso de que no constituye un diagnóstico.
- Gráfico anual, con navegación entre períodos de 365 días.
- Recordatorios por días de la semana y hora común.
- Exportación e importación CSV.
- Tema claro u oscuro según el teléfono.
- Datos guardados en un archivo JSON privado de la app; sin permiso de internet.

El CSV contiene fecha y peso. Desinstalar o borrar los datos de la app elimina los registros locales: exportá una copia para conservarlos.

## Compilar

Requiere JDK 17, Android SDK API 36 y Build Tools 36. Android mínimo: 8 (API 26).

Abrí este directorio en Android Studio para sincronizar el proyecto Gradle. También podés utilizar `build-apk.ps1`, que compila con las herramientas del SDK sin Gradle:

```powershell
.\build-apk.ps1 -Jdk 'RUTA_JDK' -BuildTools 'RUTA_BUILD_TOOLS' -AndroidJar 'RUTA_ANDROID_JAR' -KeyStore 'TU_CLAVE.p12' -StorePassword 'TU_CONTRASEÑA'
```

El alias esperado es `guitracker`. La clave privada de la APK publicada no se incluye en este repositorio. Las compilaciones firmadas con otra clave requieren reinstalar; antes exportá tus registros.

## Pruebas

```powershell
javac -encoding UTF-8 -d test-classes app/src/main/java/com/guitracker/app/Domain.java tests/DomainTest.java
java -cp test-classes DomainTest
```

Las reglas de datos cuentan con 25 comprobaciones de validación, IMC, calendario y CSV. Se verificaron empaquetado y firma de la APK. La interfaz y los recordatorios deben verificarse en el dispositivo; Android puede demorar las notificaciones por ahorro de batería.

## Licencia

El código y los recursos propios se distribuyen bajo [GNU General Public License versión 3](LICENSE.txt), sin garantía. La licencia completa también está disponible sin conexión dentro de «Acerca de».

Más detalles en [LEEME.md](LEEME.md).
