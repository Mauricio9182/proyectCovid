# 1. Inicializa el repositorio (si no lo hiciste antes)
git init

# 2. Agrega el repositorio remoto de GitHub
git remote add origin https://github.com/Mauricio9182/proyectCovid.git

        # 3. Agrega todos los archivos del proyecto
git add .

        # 4. Haz un commit con un mensaje
git commit -m "Subida completa del proyecto CovidTracker"

        # 5. Cambia a la rama principal (por si estás en otra)
git branch -M main

# 6. Sube los archivos al repositorio remoto
git push -u origin main
