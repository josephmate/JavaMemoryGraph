javac src\Main.java


$workingDir=(Get-Item -Path ".\" -Verbose).FullName


# http://stackoverflow.com/questions/4762982/powershell-get-process-id-of-called-application
# http://stackoverflow.com/questions/651223/powershell-start-process-and-cmdline-switches
$p=Start-Process -FilePath "java" -ArgumentList '-cp','src','Main' -PassThru

# http://stackoverflow.com/questions/10262231/obtaining-exitcode-using-start-process-and-waitforexit-instead-of-wait
#$pinfo = New-Object System.Diagnostics.ProcessStartInfo
#$pinfo.FileName = "java.exe"
#$pinfo.RedirectStandardError = $true
#$pinfo.RedirectStandardOutput = $true
#$pinfo.UseShellExecute = $false
#$pinfo.Arguments = "-cp src Main"
#$p = New-Object System.Diagnostics.Process
#$p.StartInfo = $pinfo
#$p.Start() | Out-Null




# wait 10 seconds for the memory to load
Start-Sleep -s 10

# generate the heap dump
jmap -dump:live,format=b,file=$workingDir\heap.dump.windows.hprof $p.Id


Stop-Process -id $p.Id

