# Base de données

- `schema.sql` : schéma SQLite (copie identique dans `backend/src/main/resources/schema.sql`,
  exécuté à chaque démarrage grâce à `CREATE TABLE IF NOT EXISTS`).
- `seed.sql` : villes et synonymes de recherche.
- Le fichier de base est `backend/data/billetterie.db` (ignoré par Git).

**Règle : toute modification du schéma passe par Paulo** (modifier `database/schema.sql`, recopier dans
`backend/src/main/resources/schema.sql`, mettre à jour l'entité Java correspondante).

SQLite : mode WAL activé et un seul écrivain à la fois (suffisant pour ce projet).
Pas de FULLTEXT MySQL : la recherche libre utilise LIKE (option : FTS5).
