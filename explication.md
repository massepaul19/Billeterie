# Explication du projet - Architecture et structure

Billetterie en ligne multi-agences de transport (Cameroun). Ce document explique **comment le projet est construit** : l'architecture, chaque dossier et chaque fichier. Pour le *quoi* (fonctionnalités), voir `docs/PROJET.md`. Pour le *comment coder*, voir les guides de `backend/` et `frontend/`.

---

## 1. Vue d'ensemble

```
   Voyageur / Agence / Super admin
              |
              v
   +-----------------------+        HTTP + JSON (docs/API.md)        +---------------------------+
   |  FRONTEND (Flutter)   | <-------------------------------------> |   BACKEND (Java, Spring)  |
   |  frontend/            |                                         |   backend/                |
   +-----------------------+                                         +-------------+-------------+
                                                                                   |
                                                                                   v
                                                                     +---------------------------+
                                                                     |  SQLite (1 seul fichier)  |
                                                                     |  backend/data/            |
                                                                     |  billetterie.db           |
                                                                     +---------------------------+
```

| Brique | Technologie | Rôle |
|---|---|---|
| Frontend | Flutter (Dart) | Écrans pour le public (recherche, réservation, paiement, billet) et pour l'administration (super admin et agences) |
| Backend | Java 17, Spring Boot 3, Maven | API REST : règles métier, sécurité, paiement, billet PDF, emails |
| Base de données | SQLite | Un fichier portable, schéma dans `database/schema.sql` |
| Emails | Gmail (SMTP) | Rappels de voyage (plus de PWA ni de push) |
| Versionnement | Git + GitHub | Une branche par fonctionnalité, intégration dans `develop` |
| Méthode | Scrum | 2 semaines : Sprint 0 (structure), Sprint 1, Sprint 2, stabilisation |

**Le contrat entre front et back est `docs/API.md`.** Le front ne connaît pas la base ; le back ne connaît pas Flutter. Ils ne se parlent que par les routes décrites dans ce fichier.

### Qui utilise quoi

| Acteur | Ce qu'il fait | Où dans le code |
|---|---|---|
| Client (voyageur) | Cherche, réserve, paie sans compte, reçoit un billet | routes publiques `/api/voyages`, `/api/reservations`, `/api/paiements`, `/api/billets` |
| Administrateur d'agence | Gère gares, véhicules, trajets, voyages ; vérifie les billets | routes `/api/agence/**` |
| Super administrateur | Crée les agences et leurs comptes, suit les commissions | routes `/api/admin/**` |

---

## 2. Architecture du backend (en couches)

```
  Requête HTTP
       |
       v
  controller/   <- reçoit la requête, valide l'entrée, renvoie la réponse (mince)
       |
       v
  service/      <- règles métier (places, expiration, commission, annulation...)
       |
       v
  repository/   <- accès à la base (Spring Data JPA)
       |
       v
  entity/       <- une classe Java = une table SQLite
```

**Règle d'or : controller -> service -> repository.** Un contrôleur n'appelle jamais directement un repository. Les entités ne sortent jamais de l'API : on renvoie des **DTO** (`dto/`).

### Parcours d'une réservation (exemple)

1. Le front envoie `POST /api/reservations` avec le voyage et le contact du client.
2. `ReservationController` valide et appelle `ReservationService`.
3. Le service appelle `VoyageRepository.reserverUnePlace(id)` : un seul `UPDATE ... WHERE places_restantes >= 1`. S'il modifie 0 ligne, il n'y a plus de place (erreur 409). C'est ce qui empêche de vendre deux fois la même place.
4. Le service crée le client et la réservation `EN_ATTENTE` avec `expire_a` = maintenant + 5 minutes.
5. Le client paie : `PaiementService` choisit le fournisseur (`SIMULE` ou `CAMPAY`) dans `payment/`.
6. Paiement réussi : réservation `PAYEE`, commission de 0,7 % enregistrée, billet créé avec son code unique (`CodeBilletGenerator`), rappels programmés.
7. Si le paiement n'arrive pas, `ExpirationReservationScheduler` passe la réservation à `EXPIREE` et libère la place.

---

## 3. Modèle de données (16 tables)

| Table | Contenu | Liens principaux |
|---|---|---|
| `ville` | Villes du Cameroun | - |
| `agence` | Agence de transport (nom, logo, position, délais de rappel, actif) | `ville_id` |
| `compte_admin` | Comptes de connexion (SUPER_ADMIN ou ADMIN_AGENCE) | `agence_id` (vide pour le super admin) |
| `gare` | Gare d'une agence dans une ville | `agence_id`, `ville_id` |
| `vehicule` | Bus (SIMPLE ou VIP, nombre de places, clim, wifi) | `agence_id` |
| `trajet` | Liaison assurée par l'agence (gare de départ -> gare d'arrivée) | `agence_id`, deux `gare_id` |
| `modele_voyage` | Voyage récurrent (jours, heure, période) - option | `trajet_id`, `vehicule_id` |
| `voyage` | Un départ précis avec prix et places restantes | `trajet_id`, `vehicule_id` |
| `client` | Fiche de contact créée à chaque réservation (pas de compte) | - |
| `reservation` | Une place pour un client sur un voyage | `voyage_id`, `client_id` |
| `billet` | Billet avec code unique (dans le QR) et statut | `reservation_id` |
| `paiement` | Tentative de paiement tracée (fournisseur, référence, statut) | `reservation_id` |
| `remboursement` | Demande de remboursement et son statut (fait par l'agence) | `reservation_id` |
| `rappel` | Email de rappel à envoyer avant le départ | `billet_id` |
| `releve_commission` | Total des ventes et commission due par agence et par mois | `agence_id` |
| `synonyme_recherche` | Mots de la recherche libre (clim, wifi, vip...) | - |

**Statuts importants :** voyage (`PROGRAMME`, `COMPLET`, `PARTI`, `ANNULE`), réservation (`EN_ATTENTE`, `PAYEE`, `EXPIREE`, `ANNULEE`), billet (`VALIDE`, `UTILISE`, `EXPIRE`, `ANNULE`).

**Conventions :** montants en entiers FCFA ; dates en texte `yyyy-MM-dd HH:mm:ss.SSS` heure `Africa/Douala` ; booléens 0/1.

---

## 4. Structure complète des dossiers et fichiers

```
billetterie/
├── README.md
├── explication.md                  (ce fichier)
├── .gitignore
├── docs/
├── database/
├── backend/
└── frontend/
```

### 4.1 Racine

| Fichier | Rôle |
|---|---|
| `README.md` | Présentation courte, tableau des dossiers, commandes de démarrage |
| `explication.md` | Ce document : architecture et description de tous les fichiers |
| `.gitignore` | Fichiers à ne pas versionner : `target/`, fichier `.db`, builds Flutter, secrets, sauvegardes `.bak-*` |

### 4.2 `docs/` : la documentation du projet

| Fichier | Rôle |
|---|---|
| `PROJET.md` | Résumé du cahier des charges : problème, solution, acteurs, exclus, priorités, changements de technologie |
| `REGLES_GESTION.md` | Les règles métier RG1 à RG19 (places, délai de 5 min, annulation, commission, sécurité...) |
| `API.md` | **Contrat d'API** : toutes les routes, corps JSON, codes d'erreur. Référence entre back et front |
| `SCRUM.md` | Rôles de l'équipe, calendrier J1-J14, règles Git, définition de « terminé » |

### 4.3 `database/` : la base de données

| Fichier | Rôle |
|---|---|
| `schema.sql` | Schéma SQLite : 16 tables, contraintes, index. **Source de référence** |
| `seed.sql` | Données de départ : 15 villes et les synonymes de recherche |
| `gen_demo_data.py` | Insère les données de démonstration (13 agences, gares, véhicules, trajets, ~4 900 voyages, réservations, billets...). `--reset` pour repartir de zéro |
| `README.md` | Règles : toute modification du schéma passe par Paulo |

Le schéma existe en **deux copies identiques** : `database/schema.sql` et `backend/src/main/resources/schema.sql`. Si on modifie l'un, on recopie l'autre.

### 4.4 `backend/` : l'API Java

#### Fichiers à la racine du backend

| Fichier | Rôle |
|---|---|
| `pom.xml` | Configuration Maven : Spring Boot, JPA, SQLite, Hibernate dialecte SQLite, mail, PDF (OpenPDF), QR (ZXing), Lombok, tests |
| `GUIDE_BACKEND.md` | Guide de programmation : architecture, règles, tests attendus |
| `TODO.md` | Suivi des tâches par sprint, avec le propriétaire (Back A ou Back B) |
| `data/` | Dossier où SQLite crée `billetterie.db` (ignoré par Git) |

#### `src/main/resources/`

| Fichier | Rôle |
|---|---|
| `application.properties` | Port 8080, connexion SQLite (WAL, `date_class=TEXT`), fuseau Douala, paramètres Gmail (commentés), règles métier configurables (blocage 5 min, commission 0,007, etc.) |
| `schema.sql` | Copie du schéma, exécutée au démarrage (`CREATE TABLE IF NOT EXISTS`) |
| `seed.sql` | Copie des données de départ, exécutée au démarrage (`INSERT OR IGNORE`) |

#### `src/main/java/cm/billetterie/`

| Fichier / dossier | Rôle |
|---|---|
| `BilletterieApplication.java` | Point d'entrée de l'application ; active les tâches planifiées |
| `config/CorsConfig.java` | Autorise le front à appeler l'API (CORS). À restreindre en production |
| `config/DemoDataLoader.java` | À faire : chargeur de démo côté Java (le script Python le remplace pour l'instant) |
| `security/TODO_JWT.md` | Instructions pour Back A : connexion JWT, rôles, filtre par agence |
| `exception/ApiException.java` | Exception métier avec statut HTTP et code d'erreur |
| `exception/ErreurApi.java` | Format JSON d'erreur `{ code, message }` |
| `exception/GlobalExceptionHandler.java` | Transforme les exceptions en réponses d'erreur uniformes |
| `payment/PaiementProvider.java` | Interface qui isole le moyen de paiement |
| `payment/PaiementResultat.java` | Résultat d'un paiement (référence, statut) |
| `payment/SimulePaiementProvider.java` | Paiement simulé (niveau 1, obligatoire) |
| `payment/CampayPaiementProvider.java` | Paiement Campay MTN/Orange (niveau 2, option, à coder) |
| `scheduler/ExpirationReservationScheduler.java` | Toutes les minutes : expire les réservations non payées et libère les places |
| `scheduler/RappelScheduler.java` | Envoie les rappels de voyage par email |
| `service/EmailService.java` | Envoi d'emails via Gmail |
| `service/BilletPdfService.java` | Génère le billet PDF avec logo et QR code |
| `util/CodeBilletGenerator.java` | Génère le code unique du billet (16 caractères) |
| `dto/TODO_DTO.md` | Consignes pour créer les objets d'entrée/sortie de l'API |

#### `entity/` : une classe par table (16) + `entity/enums/` (10 énumérations)

| Entité | Table |
|---|---|
| `Ville` | `ville` |
| `Agence` | `agence` |
| `CompteAdmin` | `compte_admin` |
| `Gare` | `gare` |
| `Vehicule` | `vehicule` |
| `Trajet` | `trajet` |
| `ModeleVoyage` | `modele_voyage` |
| `Voyage` | `voyage` |
| `Client` | `client` |
| `Reservation` | `reservation` |
| `Billet` | `billet` |
| `Paiement` | `paiement` |
| `Remboursement` | `remboursement` |
| `Rappel` | `rappel` |
| `ReleveCommission` | `releve_commission` |
| `SynonymeRecherche` | `synonyme_recherche` |

Les énumérations (`entity/enums/`) : `RoleCompte`, `TypeVehicule`, `StatutVoyage`, `StatutReservation`, `StatutBillet`, `FournisseurPaiement`, `StatutPaiement`, `StatutRemboursement`, `CanalRappel`, `StatutFacturation`. Elles correspondent aux contraintes `CHECK` du schéma.

#### `repository/` : un fichier par entité (`XxxRepository.java`)

Même nom que l'entité, suffixe `Repository`. Trois contiennent du code utile déjà écrit :
- `VoyageRepository` : `reserverUnePlace` (décompte atomique) et `libererUnePlace`.
- `CompteAdminRepository` : `findByTelephone` (pour la connexion).
- `BilletRepository` : `findByCodeUnique` (pour le scan du QR).

#### `controller/` et `service/` : une paire par module (18)

Le contrôleur porte les routes ; le service porte la logique. Même nom de base, suffixes `Controller` et `Service`.

| Module | Route de base | Propriétaire | Sprint |
|---|---|---|---|
| `Auth` | `/api/auth` | Back A | 1 |
| `Ville` | `/api/villes` | Back B | 1 |
| `AgencePublique` | `/api/agences` | Back B | 1 |
| `VoyagePublic` | `/api/voyages` | Back B | 1 |
| `SuperAdminAgence` | `/api/admin/agences` | Back A | 1 |
| `AgenceGare` | `/api/agence/gares` | Back A | 1 |
| `AgenceVehicule` | `/api/agence/vehicules` | Back A | 1 |
| `AgenceTrajet` | `/api/agence/trajets` | Back A | 1 |
| `AgenceVoyage` | `/api/agence/voyages` | Back A | 1 |
| `AgenceModele` | `/api/agence/modeles` (option) | Back A | 1 |
| `Reservation` | `/api/reservations` | Back B | 2 |
| `Paiement` | `/api/paiements` | Back B | 2 |
| `Billet` | `/api/billets` | Back B | 2 |
| `AgenceReservation` | `/api/agence/reservations` | Back B | 2 |
| `AgenceVerification` | `/api/agence/billets` | Back B | 2 |
| `AgenceParametres` | `/api/agence/parametres` | Back A | 2 |
| `AgenceRemboursement` | `/api/agence/remboursements` | Back A | 2 |
| `SuperAdminCommission` | `/api/admin/commissions` | Back A | 2 |

Chaque contrôleur est pour l'instant un squelette : un commentaire liste les routes à coder.

#### `src/test/java/cm/billetterie/`

| Fichier | Rôle |
|---|---|
| `BilletterieApplicationTests.java` | Vérifie que l'application démarre |
| `ReservationConcurrenceTest.java` | Test de charge à activer : 50 clients simultanés sur 5 places, aucune place vendue deux fois |

### 4.5 `frontend/` : l'application Flutter

#### Fichiers à la racine du frontend

| Fichier | Rôle |
|---|---|
| `pubspec.yaml` | Dépendances : `dio` (HTTP), `provider`, `flutter_secure_storage` (jeton), `intl`, `url_launcher`, `mobile_scanner` (scan QR), `flutter_map` + `latlong2` (carte) |
| `GUIDE_FRONTEND.md` | Guide de programmation : architecture, règles, tests |
| `TODO.md` | Suivi des modules par sprint, avec le propriétaire (Front C ou Front D) |
| `test/` | Tests Flutter |

#### `lib/`

| Fichier | Rôle |
|---|---|
| `main.dart` | Point d'entrée ; accueil de développement avec un lien vers chaque page (remplacé par la vraie navigation à l'intégration) |
| `core/config/api_config.dart` | Adresse du serveur (`--dart-define=API_URL=...`) |
| `core/network/api_client.dart` | Client HTTP unique : ajoute le jeton à chaque requête |
| `core/theme/app_theme.dart` | Thème visuel |
| `core/utils/format.dart` | Formats : montants en FCFA, dates en français |

#### `lib/features/` : un dossier par module

Chaque module contient `data/<module>_api.dart` (appels HTTP), `presentation/<module>_page.dart` (écran) et `TODO.md` (tâches, routes, tests).

| Module | Écran | Propriétaire | Sprint |
|---|---|---|---|
| `auth` | Connexion (téléphone + mot de passe) | Front C | 1 |
| `super_admin_agences` | Gestion des agences et de leurs comptes | Front C | 1 |
| `agence_gares` | Gares de l'agence | Front C | 1 |
| `agence_vehicules` | Véhicules | Front C | 1 |
| `agence_trajets` | Trajets | Front C | 1 |
| `agence_voyages` | Voyages, prix, annulation | Front C | 1 |
| `agence_reservations` | Réservations de l'agence | Front C | 2 |
| `agence_verification` | Scan du QR et vérification à l'embarquement | Front C | 2 |
| `commissions` | Relevé mensuel des commissions (super admin) | Front C | 2 |
| `recherche` | Recherche de voyages en direct | Front D | 1 |
| `carte` | Carte des agences (option) | Front D | 1 |
| `reservation` | Réservation avec décompte de 5 minutes | Front D | 2 |
| `paiement` | Paiement MTN/Orange | Front D | 2 |
| `billet` | Affichage, PDF et annulation du billet | Front D | 2 |

---

## 5. Données de démonstration

`database/gen_demo_data.py` remplit `backend/data/billetterie.db` :

- 13 agences fictives (dont une **désactivée** pour tester qu'elle disparaît du public),
- 56 gares, 39 véhicules (simples et VIP), 154 trajets entre villes camerounaises,
- environ 4 900 voyages de J-2 à J+20 (certains complets, annulés ou déjà partis),
- 80 réservations réparties dans tous les statuts, avec billets, paiements, remboursements, rappels,
- des relevés de commission,
- les comptes de test, mot de passe `demo1234` : super admin `699000000`, admins d'agence `677000001` à `677000013`.

Chaque membre de l'équipe lance le script chez lui, car le fichier `.db` n'est pas dans Git.

---

## 6. Scripts d'initialisation (hors du projet)

| Script | Rôle |
|---|---|
| `init_billetterie.py` | Sprint 0 : crée toute l'arborescence, les guides, le contrat d'API, les squelettes Java et Flutter. Ne remplace jamais un fichier existant (sauf `--force`, avec sauvegarde `.bak`) |
| `gen_demo_data.py` | Insère les données de démonstration (à placer dans `database/`) |
| `patch_dates.py` | Corrige le format des dates SQLite (`date_class=TEXT`) et le fuseau `Africa/Douala` |

---

## 7. Workflow d'équipe (résumé)

```
main      ----------------------------------------------- (stable, déployée)
develop   ----o------------o------------o----------------- (intégration, Paulo merge)
               \          /  \          /
feature/...     o--o--o--o    o--o--o--o                   (une branche par tâche)
```

1. Chacun crée `feature/<module>-<nom>` depuis `develop`.
2. Il code, écrit ses tests, coche son `TODO.md`.
3. Il ouvre une Pull Request vers `develop`.
4. Paulo relit, merge, intègre et déploie (J7 et J12).

Rôles : **Paulo** (modélisation, structure, intégration, déploiement), **Back A** et **Back B** (Java), **Front C** (administration) et **Front D** (public) (Flutter).

---

## 8. Où chercher quoi

| Je veux... | Je regarde... |
|---|---|
| Savoir quelle route appeler ou implémenter | `docs/API.md` |
| Connaître une règle métier | `docs/REGLES_GESTION.md` |
| Savoir ce que je dois faire | `backend/TODO.md`, `frontend/TODO.md` ou le `TODO.md` de mon module |
| Connaître les conventions de code | `backend/GUIDE_BACKEND.md`, `frontend/GUIDE_FRONTEND.md` |
| Comprendre une table | `database/schema.sql` et l'entité dans `backend/.../entity/` |
| Savoir comment travailler en équipe | `docs/SCRUM.md` |
