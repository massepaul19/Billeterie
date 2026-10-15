#!/usr/bin/env python3
"""
Patch : dates en texte dans SQLite + fuseau Africa/Douala.
A lancer depuis la racine du projet :  python3 patch_dates.py
Sauvegarde .bak-<horodatage> avant chaque modification, assertion count == 1, grep de vérification.
"""
import shutil
import time

STAMP = time.strftime("%Y%m%d-%H%M%S")

PATCHES = [
    ("backend/src/main/resources/application.properties",
     "?journal_mode=WAL&busy_timeout=5000&foreign_keys=true",
     "?journal_mode=WAL&busy_timeout=5000&foreign_keys=true&date_class=TEXT"),
    ("backend/src/main/java/cm/billetterie/BilletterieApplication.java",
     "import org.springframework.boot.SpringApplication;",
     "import java.util.TimeZone;\nimport org.springframework.boot.SpringApplication;"),
    ("backend/src/main/java/cm/billetterie/BilletterieApplication.java",
     "        SpringApplication.run(BilletterieApplication.class, args);",
     "        TimeZone.setDefault(TimeZone.getTimeZone(\"Africa/Douala\"));\n"
     "        SpringApplication.run(BilletterieApplication.class, args);"),
    ("database/schema.sql",
     "-- Dates : texte ISO-8601 heure locale Africa/Douala.",
     "-- Dates : texte 'yyyy-MM-dd HH:mm:ss.SSS' heure locale Africa/Douala (date_class=TEXT)."),
    ("backend/src/main/resources/schema.sql",
     "-- Dates : texte ISO-8601 heure locale Africa/Douala.",
     "-- Dates : texte 'yyyy-MM-dd HH:mm:ss.SSS' heure locale Africa/Douala (date_class=TEXT)."),
]

for chemin, ancien, nouveau in PATCHES:
    with open(chemin, "r", encoding="utf-8") as f:
        contenu = f.read()
    if nouveau in contenu:
        print("déjà appliqué : %s" % chemin)
        continue
    assert contenu.count(ancien) == 1, "motif introuvable ou multiple dans %s" % chemin
    shutil.copy2(chemin, chemin + ".bak-" + STAMP)
    with open(chemin, "w", encoding="utf-8", newline="\n") as f:
        f.write(contenu.replace(ancien, nouveau))
    print("patché : %s" % chemin)

print()
print("Vérification : grep -n \"date_class\\|TimeZone\\|date_class=TEXT\" "
      "backend/src/main/resources/application.properties backend/src/main/java/cm/billetterie/BilletterieApplication.java")
