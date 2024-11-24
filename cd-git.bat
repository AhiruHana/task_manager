@echo off
CD %*
where git >nul 2>&1
if %errorlevel% neq 0 (
    goto :eof
)
for /f "usebackq tokens=* delims=" %%g in (`git rev-parse --is-inside-work-tree ^>nul 2^>^&1 ^&^& git branch --show-current`) do (
    set branchname=%%g
)
if "%branchname%"=="" (
    prompt $p$_$$$s
) else (
    prompt $p $c$e[36m%branchname%$e[0m$f$_$$$s
)
set branchname=
