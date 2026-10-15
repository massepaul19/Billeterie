# Guide de programmation - Backend (Java / Spring Boot / SQLite)

Pour : Back A et Back B. Lire aussi `docs/SCRUM.md`, `docs/API.md` et `docs/REGLES_GESTION.md`.

## Lancer
```
cd backend
mvn spring-boot:run        # API sur http://localhost:6012
mvn test                   # tests
```
La base est créée au démarrage dans `backend/data/billetterie.db` (schéma : `database/schema.sql`).

## Architecture (`src/main/java/cm/billetterie`)
| Dossier | Rôle |
|---|---|
| `entity/` | Entités JPA (déjà créées par Paulo - ne pas changer les colonnes sans lui) |
| `repository/` | Accès à la base (Spring Data) |
| `service/` | Logique métier et règles de gestion |
| `controller/` | Routes HTTP, minces : valident l'entrée, appellent un service |
| `dto/` | Objets d'entrée/sortie de l'API (records) |
| `payment/` | Fournisseurs de paiement (SIMULE, CAMPAY) derrière une interface |
| `scheduler/` | Tâches planifiées (expiration des réservations, rappels) |
| `exception/` | Erreurs de l'API |

## Règles
1. **Controller -> Service -> Repository.** Un controller n'appelle jamais un repository.
2. Les routes, noms de champs et codes d'erreur suivent **exactement** `docs/API.md`.
3. Ne jamais renvoyer une entité : toujours un DTO.
4. Erreurs : lancer `ApiException(status, code, message)`.
5. Sécurité : l'`agenceId` vient du jeton (RG14). Mots de passe hachés (BCrypt).
6. Places : utiliser **uniquement** `VoyageRepository.reserverUnePlace` (jamais lire puis écrire).
7. Pas de secret dans Git : variables d'environnement.
8. SQLite : une transaction courte, un seul écrivain à la fois.

## Tests attendus
Pour chaque route : cas nominal, entrée invalide (400), non authentifié (401), mauvais rôle (403),
ressource d'une autre agence (404 ou 403), cas métier (ex. plus de place -> 409).
Outils : JUnit 5, MockMvc, base de test dédiée. Test de concurrence sur les places (Back B, Sprint 2).

## Où trouver quoi faire
Chaque contrôleur et service contient en commentaire ses routes à coder, son propriétaire et son sprint.
Suivi global : `backend/TODO.md`.
