$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$localProperties = Join-Path $repoRoot "local.properties"
$exampleProperties = Join-Path $repoRoot "local.properties.example"

if (-not (Test-Path $exampleProperties)) {
    throw "Missing local.properties.example"
}

if (-not (Test-Path $localProperties)) {
    Copy-Item -Path $exampleProperties -Destination $localProperties
    Write-Host "Created local.properties from local.properties.example"
} else {
    Write-Host "local.properties already exists; leaving it unchanged"
}

Write-Host ""
Write-Host "App setup complete."
Write-Host "To build:"
Write-Host "  .\gradlew.bat :app:assembleDebug"
Write-Host ""
Write-Host "For Supabase schema/data work, ask for project access and run:"
Write-Host "  npx.cmd supabase login"
Write-Host "  npx.cmd supabase link --project-ref nwzcylzzudpwghqfhdks"
