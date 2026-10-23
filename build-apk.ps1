param(
 [Parameter(Mandatory=$true)][string]$Jdk,
 [Parameter(Mandatory=$true)][string]$BuildTools,
 [Parameter(Mandatory=$true)][string]$AndroidJar,
 [Parameter(Mandatory=$true)][string]$KeyStore,
 [Parameter(Mandatory=$true)][string]$StorePassword,
 [string]$OutDir = "$PSScriptRoot\dist",
 [string]$AndroidClasses = ''
)
$ErrorActionPreference='Stop'
function Run([string]$Exe,[string[]]$Arguments) { & $Exe @Arguments; if($LASTEXITCODE -ne 0){throw "Falló: $Exe ($LASTEXITCODE)"} }
New-Item -ItemType Directory -Force "$OutDir\classes" | Out-Null
$OutDir=(Resolve-Path $OutDir).Path
$source=Get-ChildItem "$PSScriptRoot\app\src\main\java" -Recurse -Filter '*.java' | ForEach-Object FullName
$compilePath=if($AndroidClasses){$AndroidClasses}else{$AndroidJar}
Run "$Jdk\bin\javac.exe" (@('-encoding','UTF-8','-source','8','-target','8','-classpath',$compilePath,'-d',"$OutDir\classes")+$source)
Run "$Jdk\bin\jar.exe" @('cf',"$OutDir\classes.jar",'-C',"$OutDir\classes",'.')
Run "$Jdk\bin\java.exe" @('-cp',"$BuildTools\lib\d8.jar",'com.android.tools.r8.D8','--lib',$AndroidJar,'--min-api','26','--output',$OutDir,"$OutDir\classes.jar")
$manifest=Get-Content "$PSScriptRoot\app\src\main\AndroidManifest.xml" -Raw
$manifest.Replace('<manifest xmlns:android=','<manifest package="com.guitracker.app" xmlns:android=') | Set-Content "$OutDir\AndroidManifest.xml" -Encoding utf8
Run "$BuildTools\aapt2.exe" @('compile','--dir',"$PSScriptRoot\app\src\main\res",'-o',"$OutDir\resources.zip")
Run "$BuildTools\aapt2.exe" @('link','-o',"$OutDir\base.apk",'--manifest',"$OutDir\AndroidManifest.xml",'-I',$AndroidJar,'-A',"$PSScriptRoot\app\src\main\assets",'--min-sdk-version','26','--target-sdk-version','36','--version-code','4','--version-name','1.3',"$OutDir\resources.zip")
Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive=[IO.Compression.ZipFile]::Open("$OutDir\base.apk",[IO.Compression.ZipArchiveMode]::Update)
try { [IO.Compression.ZipFileExtensions]::CreateEntryFromFile($archive,"$OutDir\classes.dex",'classes.dex') | Out-Null } finally {$archive.Dispose()}
Run "$BuildTools\zipalign.exe" @('-f','4',"$OutDir\base.apk","$OutDir\aligned.apk")
Run "$Jdk\bin\java.exe" @('-jar',"$BuildTools\lib\apksigner.jar",'sign','--ks',$KeyStore,'--ks-key-alias','guitracker','--ks-pass',"pass:$StorePassword",'--out',"$OutDir\GuiTracker.apk","$OutDir\aligned.apk")
Run "$Jdk\bin\java.exe" @('-jar',"$BuildTools\lib\apksigner.jar",'verify','--verbose',"$OutDir\GuiTracker.apk")
Write-Host "APK: $OutDir\GuiTracker.apk"
