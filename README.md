# Billetterie multi-agences de transport (Cameroun)

Projet scolaire d'équipe - plateforme de réservation de billets de bus avec paiement Mobile Money.

| Dossier | Contenu |
|---|---|
| `docs/` | Projet, règles de gestion, **contrat d'API**, organisation Scrum |
| `database/` | Schéma SQLite et données de départ |
| `backend/` | API Java (Spring Boot, SQLite) - voir `backend/GUIDE_BACKEND.md` |
| `frontend/` | Application Flutter - voir `frontend/GUIDE_FRONTEND.md` |

## Stack
Java 17 + Spring Boot 3 (backend), Flutter (front), SQLite (un seul fichier), Gmail pour les rappels, Git/GitHub, méthode Scrum.

## Démarrage
Backend : `cd backend && mvn spring-boot:run` (API sur http://localhost:8080).
Frontend : `cd frontend && flutter create . --project-name billetterie_app && flutter pub get && flutter run`.

## Qui fait quoi
Voir `docs/SCRUM.md`. **Lire d'abord** : `docs/SCRUM.md`, puis le guide de votre dossier, puis votre `TODO`.
