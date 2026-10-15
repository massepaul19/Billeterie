# Découpage du travail - Projet Billetterie

Ce document dit **qui fait quoi, dans quel ordre, et comment on sait que c'est fini**. Il complète `docs/SCRUM.md` (organisation), `docs/API.md` (contrat), `docs/REGLES_GESTION.md` (règles RG) et les `TODO.md` de chaque module. En cas de conflit entre ce document et un `TODO.md`, **ce document fait foi** (les `TODO.md` sont mis à jour ensuite).

---

## 1. Vue d'ensemble

**Objectif :** une plateforme de billetterie en ligne multi-agences (Cameroun) : recherche, réservation 5 min, paiement simulé, billet PDF + QR, vérification, annulation, rappels email, commissions. Livrée et déployée en **14 jours**.

**Principe de travail :** tout le monde travaille **en même temps dès le premier jour**. Personne n'attend un autre : le front code contre le contrat `docs/API.md` avec des données simulées (mocks), le back teste avec la base de démonstration.

### Calendrier

| Jours | Phase | Contenu | Jalon |
|---|---|---|---|
| J1-J2 | **Sprint 0** | Structure, schéma, contrat d'API (Paulo) | Fait |
| J3-J7 | **Sprint 1** | Catalogue : auth, agences, gares, véhicules, trajets, voyages, recherche | **J7 : intégration + tag `v0.1` + 1re version sur le serveur** |
| J8-J12 | **Sprint 2** | Réserver, payer, billet, vérifier, annuler, rappels, commissions | **J12 : intégration + tag `v0.2`** |
| J13-J14 | **Sprint 3 (stabilisation)** | Corrections, intégration finale, déploiement, tests de production | **J14 : tag `v1.0` + démo** |

L'API tourne en local sur **http://localhost:6012**.

### Équipe

| Rôle | Personne | Domaine |
|---|---|---|
| **Paulo** | Paulo | Intégration, déploiement, revues, arbitrage de l'API |
| **Back A** | Balla | Auth/JWT, upload de fichiers, agences, gares, véhicules, paramètres, remboursements, commissions, emails, billet PDF + QR |
| **Back B** | Patricia | Recherche publique, trajets, voyages, réservation, paiement, billet, réservations agence, vérification, expiration |
| **Front C** | Abdias | Espace administration : connexion, agences, trajets, voyages, réservations, vérification QR, commissions, paramètres, remboursements |
| **Front D** | Bouba | Espace public et écrans véhicules/gares : recherche, carte, réservation, paiement, billet, gares, véhicules |

### Charge estimée (en jours de travail, à ±20 %)

| | Sprint 1 (5 j) | Sprint 2 (5 j) |
|---|---|---|
| Back A | 5,25 | 5,25 |
| Back B | 5,5 | 5,0 |
| Front C | 5,0 | 5,5 |
| Front D | 4,5 (+1 option) | 5,0 |
| Paulo | 3,25 (hors revues) | 3 (hors revues) |

---

## 2. Règles communes (à lire avant de coder)

1. **Une branche par tâche** : `feature/<module>-<prénom>` (ex. `feature/auth-jwt-balla`), créée depuis `develop`. Jamais de push direct sur `main` ni `develop`.
2. **Un seul propriétaire par fichier.** Les fichiers partagés ne sont modifiés que par Paulo : `pom.xml`, `pubspec.yaml`, `application.properties`, `schema.sql` (les deux copies), `main.dart`, `docs/API.md`. Besoin d'une modification : on ouvre une issue `api-change` ou `config` (voir section 6).
3. **PR petites** : moins de ~300 lignes modifiées, une seule fonctionnalité, décrite en 2 lignes avec `Closes #<issue>`.
4. **Revue en deux temps** : 1) un coéquipier du même domaine relit (Balla <-> Patricia pour le back, Abdias <-> Bouba pour le front), 2) Paulo merge. Paulo s'engage à répondre **sous 24 h**.
5. **Récupérer `develop` chaque matin** avant de coder.
6. **Commits** : `feat(module): ...`, `fix(module): ...`, `test(module): ...`, `docs: ...`.
7. **Aucun secret dans Git** (mots de passe Gmail, clé JWT, jetons) : variables d'environnement ou fichier ignoré.
8. **Définition de « terminé »** (par tâche) :
   - [ ] respecte `docs/API.md` ;
   - [ ] tests écrits et qui passent (`mvn test` ou `flutter test`) ;
   - [ ] pas de secret dans Git ;
   - [ ] case cochée dans le `TODO.md` du module ;
   - [ ] PR ouverte, relue par un coéquipier, issue liée.

### Mocks côté front (pour ne jamais attendre le back)

Chaque module Flutter a, à côté de `data/<module>_api.dart`, une version simulée qui renvoie des données conformes à `docs/API.md`. Elle est activée par `--dart-define=USE_MOCK=true`. Au moment de l'intégration, on lance sans ce paramètre : le front parle au vrai back.

### Fichiers (logo des agences, photos des véhicules)

- Colonnes existantes : `agence.logo` et `vehicule.image` contiennent le **chemin relatif** du fichier (ex. `agences/3f2a9c.png`).
- Les fichiers sont stockés dans `backend/uploads/` (ignoré par Git) et servis en lecture publique sous `/files/**`.
- Les réponses JSON renvoient l'**URL complète** dans `logo` et `image` (ex. `http://localhost:6012/files/agences/3f2a9c.png`).
- Envoi par routes multipart : `POST /api/agence/logo`, `POST /api/admin/agences/{id}/logo`, `POST /api/agence/vehicules/{id}/image` (jpg, png ou webp, 2 Mo maximum, nom aléatoire).
- Les corps de `POST /api/admin/agences` et `/api/agence/vehicules` ne contiennent plus `logo` ni `image`.
- Données de démo : quelques images d'exemple dans `database/demo-images/`, référencées par `gen_demo_data.py` et copiées dans `backend/uploads/` au lancement.

**Règle : toute image envoyée doit s'afficher dans l'interface Flutter.** Une tâche d'upload n'est « terminée » que si l'image apparaît à l'écran : le logo de l'agence dans la recherche, la liste des agences, la carte et le billet PDF ; la photo du véhicule dans la liste des véhicules et dans les résultats de recherche. À chaque intégration (J7, J12, J13), Paulo vérifie que les images s'affichent.

---

## 3. Sprint 1 (J3-J7) : le catalogue de voyages

**But du sprint :** à J7, un administrateur d'agence se connecte, crée ses gares, véhicules, trajets et voyages ; un voyageur les retrouve par la recherche.

### Jalons internes

| Jour | À livrer |
|---|---|
| J3 | Paulo : dépôt GitHub prêt, `API.md` v0.2 figé, issues créées. Tout le monde : planification (30 min) puis démarrage |
| J4 | **Connexion** livrée (Back A + Front C) ; Back B a fini villes et agences publiques |
| J5 | **Recherche** `GET /api/voyages` livrée (Back B) ; service de **stockage de fichiers** livré (Back A) |
| J6 | Trajets et voyages (Back B), gares/véhicules (Back A) mergés dans `develop` |
| J7 | **Intégration** (Paulo) : le front est branché sur le vrai back, les logos et photos s'affichent, `v0.1`, démo |

### Paulo (P)

| N° | Tâche | Critère d'acceptation | Charge |
|---|---|---|---|
| P1 | Pousser le projet sur GitHub, créer `main` et `develop`, ajouter les 4 collaborateurs | Chacun peut cloner et créer une branche | 0,5 j |
| P2 | `API.md` v0.2 et schéma : routes d'upload, champs `logo`/`image` (retrait des corps de création), route du taux de commission `GET/PUT /api/admin/parametres/commission` `{ "taux": 0.007 }`, table `parametre` (clé, valeur) dans les deux copies de `schema.sql` et taux initial 0,007 dans `seed.sql` | Document relu et annoncé à l'équipe ; `mvn spring-boot:run` démarre avec la nouvelle table | 0,5 j |
| P3 | Ajouter les dépendances Spring Security + JWT dans `pom.xml` (avant que Back A commence A1) | `mvn compile` passe | 0,25 j |
| P4 | Mettre en place GitHub Projects : jalons, étiquettes, issues du sprint | Chaque tâche de ce document est une issue assignée | 0,5 j |
| P5 | Revues de PR (24 h) | Aucune PR en attente de plus de 24 h | continu |
| P6 | **Intégration J7** : fusionner, brancher le front, corriger les écarts, tag `v0.1`, première version sur le serveur | Un utilisateur ouvre l'application sur le serveur et lance une recherche | 1,5 j |

### Back A - Balla

Rappel : l'`agenceId` vient **toujours** du jeton (RG14).

| N° | Tâche | Branche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|---|
| A1 | **Auth/JWT** : `POST /api/auth/connexion`, `POST /api/auth/mot-de-passe`, filtre JWT, rôles (voir `security/TODO_JWT.md`) - RG15 | `feature/auth-jwt-balla` | `699000000` / `demo1234` renvoie `token`, `role`, `agenceId` ; `/api/agence/**` sans jeton -> 401 ; mauvais rôle -> 403 ; le compte d'une agence désactivée est refusé (403 `INTERDIT`) | connexion OK/KO, accès refusé, agence désactivée | 1,5 j |
| A2 | **Stockage de fichiers** : `FileStorageService`, service `/files/**`, routes d'upload logo et image | `feature/upload-balla` | Un jpg de 1 Mo envoyé **s'affiche dans un navigateur** à l'URL renvoyée ; un `.exe` ou un fichier de 5 Mo -> 400 `VALIDATION` | type refusé, taille refusée | 1 j |
| A3 | **Agences (super admin)** : `GET/POST/PUT/DELETE /api/admin/agences`, `PATCH /{id}/activation`, `POST /{id}/compte`, `POST /api/admin/comptes/{id}/reinitialisation-mot-de-passe` - RG16 | `feature/admin-agences-balla` | Une agence désactivée n'apparaît plus dans `GET /api/agences` et son admin ne peut plus se connecter ; le compte est créé avec le mot de passe initial | création, désactivation | 1,5 j |
| A4 | **Gares** : `GET/POST/PUT/DELETE /api/agence/gares` | `feature/agence-gares-balla` | Un admin ne voit que les gares de son agence (test avec deux agences) | isolation RG14 | 0,5 j |
| A5 | **Véhicules** : `GET/POST/PUT/DELETE /api/agence/vehicules` | `feature/agence-vehicules-balla` | Idem + immatriculation unique par agence (erreur 400 sinon) | isolation, unicité | 0,5 j |
| A6 | Vérifier `GlobalExceptionHandler` : formats d'erreur de `API.md` (400/401/403/404/409/422/429) | `fix/erreurs-balla` | Chaque code d'erreur renvoie `{ code, message }` | un test par code | 0,25 j |

Dépendances : A1 passe en premier (Back B, Front C en ont besoin à partir du J5). `DemoDataLoader` n'est **pas** à faire (le script Python le remplace).

### Back B - Patricia

| N° | Tâche | Branche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|---|
| B1 | **Villes** `GET /api/villes?q=` | `feature/villes-patricia` | `?q=ya` renvoie Yaoundé | recherche partielle | 0,25 j |
| B2 | **Agences publiques** `GET /api/agences` (carte) - RG16 | `feature/agences-publiques-patricia` | Seulement les agences actives, avec URL du logo | agence inactive absente | 0,25 j |
| B3 | **Recherche de voyages** `GET /api/voyages` : filtres `depart`, `destination`, `dateDebut`, `dateFin`, `heure`, `type`, `clim`, `wifi`, `q` + synonymes, pagination, tri - RG5 | `feature/recherche-voyages-patricia` | Jamais de voyage passé, annulé, complet ; pas de `placesRestantes` ; tri par proximité de l'heure, puis par distance seulement si la position du voyageur est envoyée (option carte, jamais enregistrée - RG18) | chaque filtre, voyage complet absent, tri | 2 j |
| B4 | `GET /api/voyages/{id}` | idem | Même objet que dans la liste ; 404 si absent | 404 | 0,25 j |
| B5 | **Trajets** `GET/POST/PUT/DELETE /api/agence/trajets` - RG17 | `feature/agence-trajets-patricia` | Un trajet part d'une gare de l'agence (sinon 400) ; isolation par agence | RG14, RG17 | 1 j |
| B6 | **Voyages** `GET/POST/PUT /api/agence/voyages`, `PATCH /{id}/prix`, `POST /{id}/annulation` - RG6, RG10 | `feature/agence-voyages-patricia` | Places = `nbPlaces` du véhicule ; un changement de prix ne touche pas les réservations payées ; l'annulation crée des remboursements à 100 % | prix, annulation | 1,25 j |
| B7 | **Test de concurrence** : activer `ReservationConcurrenceTest` (50 clients, 5 places) | `test/concurrence-patricia` | Exactement 5 réservations réussies, 45 refusées en 409, `places_restantes` = 0 | le test lui-même | 0,5 j |

Dépendances : B1-B4 (public) ne dépendent de personne : à faire en premier (J3-J5). B5-B6 demandent l'auth (A1, livrée J4).

### Front C - Abdias

Rappel : mocks d'abord (`USE_MOCK=true`), vrai back à l'intégration.

| N° | Tâche | Branche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|---|
| C1 | **Connexion** : écran, appel `/api/auth/connexion`, jeton dans `flutter_secure_storage`, redirection selon le rôle, retour à la connexion sur 401 | `feature/auth-abdias` | Super admin et admin d'agence arrivent chacun sur leur écran ; un jeton expiré renvoie à la connexion ; un compte d'agence désactivée voit un message de refus | widget test connexion | 1 j |
| C2 | **Agences (super admin)** : liste, création/modification avec envoi du logo, activation/désactivation, création du compte admin | `feature/super-admin-agences-abdias` | Créer une agence, **voir son logo s'afficher dans la liste**, la désactiver | formulaire, activation | 1,5 j |
| C3 | **Trajets** : liste, création, modification, suppression (choix des gares de l'agence) | `feature/agence-trajets-abdias` | Impossible de choisir une gare d'une autre agence ; erreurs affichées | formulaire | 1 j |
| C4 | **Voyages** : liste, création (trajet + véhicule + date + prix), changement de prix, annulation avec confirmation | `feature/agence-voyages-abdias` | Annulation demandée deux fois de suite : message clair | formulaire, annulation | 1,5 j |

Dépendance : C1 doit être livré **avant J5** : Front D en a besoin pour tester ses écrans d'administration (gares, véhicules).

### Front D - Bouba

| N° | Tâche | Branche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|---|
| D1 | **Recherche** : formulaire (départ, destination, date, heure, type, clim, wifi, texte libre), liste paginée, états vide/chargement/erreur, logo de l'agence | `feature/recherche-bouba` | Chaque filtre modifie les résultats ; aucune « places restantes » affichée ; le logo de l'agence s'affiche sur chaque résultat | filtres, liste vide | 2 j |
| D2 | **Gares** (espace agence) : liste, création, modification, suppression | `feature/agence-gares-bouba` | Comme C3 | formulaire | 1 j |
| D3 | **Véhicules** (espace agence) : liste, création, modification, envoi de la photo | `feature/agence-vehicules-bouba` | La photo envoyée **s'affiche** dans la liste des véhicules (et dans les résultats de recherche) | formulaire, envoi | 1,5 j |
| D4 | *(option)* **Carte** des agences avec `flutter_map` | `feature/carte-bouba` | Chaque agence active a un marqueur | - | 1 j |

Dépendance : D2 et D3 utilisent la connexion de C1 ; en attendant, un jeton de démonstration en mock.

---

## 4. Sprint 2 (J8-J12) : réserver, payer, billet

**But du sprint :** à J12, un voyageur cherche, réserve (5 min), paie, télécharge son billet PDF et peut l'annuler ; l'agence vérifie le billet à l'embarquement ; le super admin voit les commissions.

### Jalons internes

| Jour | À livrer |
|---|---|
| J8 | Planification (30 min), démo du Sprint 1 |
| J9 | Réservation et paiement côté back (B8, B9) ; écran de réservation côté front en mock (D5) |
| J10 | Billet + PDF (A11, B10) ; paramètres et remboursements (A7, A8) |
| J11 | Vérification et réservations agence (B11), commissions et emails (A9, A10) |
| J12 | **Intégration** (Paulo), `v0.2`, démo |

### Paulo (P)

| N° | Tâche | Critère d'acceptation | Charge |
|---|---|---|---|
| P7 | Planification du Sprint 2, mise à jour des issues | Issues créées et assignées | 0,5 j |
| P8 | Revues de PR (24 h) | Aucune PR en attente de plus de 24 h | continu |
| P9 | **Intégration J12** : fusionner, brancher le front, corriger les écarts, tag `v0.2`, mettre à jour le serveur | Un voyageur réalise tout le parcours réserver -> payer -> billet sur le serveur | 2,5 j |

### Back A - Balla

| N° | Tâche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|
| A7 | **Paramètres de rappel** `GET/PUT /api/agence/parametres/rappels` `{ "delaisHeures": [24, 2] }` | Les délais sont sauvegardés par agence | lecture/écriture, isolation | 0,5 j |
| A8 | **Remboursements (agence)** `GET /api/agence/remboursements`, `PATCH /{id}` (`TRAITE`) - RG11 | L'agence ne voit que ses demandes | isolation, changement de statut | 0,5 j |
| A9 | **Commissions** : `GET /api/admin/commissions?mois=`, relevé mensuel par agence ; taux lu dans la table `parametre` (0,007 par défaut) et modifiable par `GET/PUT /api/admin/parametres/commission` (super admin) ; calcul arrondi à l'entier le plus proche - RG12 ; expose `CommissionService.enregistrer(reservation)` appelé par Back B (B9) | Total des ventes et commission cohérents ; après un changement de taux, les réservations déjà payées gardent leur ancienne commission | calcul, arrondi, changement de taux | 1,75 j |
| A10 | **Rappels email** : `EmailService` (Gmail), `RappelScheduler` selon les délais de l'agence | Un rappel est envoyé une seule fois à l'heure prévue (mode test : adresse de test) | planification, envoi unique | 1,25 j |
| A11 | **Billet PDF + QR** : `BilletPdfService.generer(billet)` avec logo de l'agence et QR du code unique - RG7 ; appelé par Back B (B10) | Le PDF s'ouvre, le QR contient le code, le logo de l'agence est présent et visible | génération, QR lisible | 1,25 j |

### Back B - Patricia

| N° | Tâche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|
| B8 | **Réservation** `POST /api/reservations`, `GET /api/reservations/{id}` - RG1-RG4, RG19 | `expireA` = maintenant + 5 min ; plus de place -> 409 ; plus de 3 réservations en attente par téléphone ou par IP -> 429 `TROP_DE_RESERVATIONS` (règle validée) | une place = une réservation, 409, 429 | 1,25 j |
| B9 | **Paiement** `POST /api/paiements` via `PaiementProvider` (`SIMULE`) : réservation `PAYEE`, commission enregistrée (appel de A9), billet créé avec `CodeBilletGenerator`, rappels programmés | Paiement sur réservation expirée -> 409 `RESERVATION_EXPIREE` ; réponse conforme à `API.md` | réussite, expiration | 1,25 j |
| B10 | **Billet** `GET /api/billets/{code}`, `GET /api/billets/{code}/pdf` (via A11), `POST /api/billets/{code}/annulation` - RG9 | Annulation à moins de 2 h du départ -> 422 `ANNULATION_TARDIVE` ; sinon remboursement moins 5 % | annulation tardive/OK | 1 j |
| B11 | **Côté agence** : `GET /api/agence/reservations?statut=&voyageId=`, `POST /api/agence/billets/verification`, `GET /api/agence/billets/recherche?q=` - RG13, RG14 | Le billet passe à `UTILISE` à la première vérification, la deuxième renvoie 422 `BILLET_INVALIDE` | double scan, isolation | 1,25 j |
| B12 | **Expiration** : finaliser `ExpirationReservationScheduler` | Une réservation non payée après 5 min passe à `EXPIREE` et la place est libérée | expiration, place libérée | 0,25 j |

### Front C - Abdias

| N° | Tâche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|
| C5 | **Réservations de l'agence** : liste paginée, filtres statut/voyage, client et billet | Les filtres fonctionnent | filtres | 1,25 j |
| C6 | **Vérification** : scan du QR (`mobile_scanner`) + recherche par nom ou téléphone, résultat clair (valide, déjà utilisé, invalide) | Un billet déjà utilisé s'affiche en rouge | scan simulé, résultat | 2 j |
| C7 | **Commissions** (super admin) : relevé mensuel par agence et **modification du taux de commission** | Choix du mois, totaux par agence ; le nouveau taux est enregistré et affiché | affichage, modification du taux | 1,25 j |
| C8 | **Paramètres de rappel** et **remboursements** (admin d'agence) | Modifier les délais ; passer une demande à `TRAITE` | formulaire, changement de statut | 1 j |

### Front D - Bouba

| N° | Tâche | Critère d'acceptation | Tests | Charge |
|---|---|---|---|---|
| D5 | **Réservation** : formulaire (nom, prénom, téléphone, email), **décompte de 5 min**, gestion 409 et 429 | À 0:00 l'écran bascule en « réservation expirée » | décompte, erreurs | 2 j |
| D6 | **Paiement** : choix MTN/Orange, téléphone, appel `POST /api/paiements`, états réussi/échec/expiré | Le billet s'affiche après un paiement réussi | états | 1,5 j |
| D7 | **Billet** : affichage du QR et des infos, téléchargement du PDF (`url_launcher`), annulation avec confirmation | Annulation tardive : message de refus clair | affichage, annulation | 1,5 j |

---

## 5. Sprint 3 (J13-J14) : stabilisation et production

**But du sprint :** livrer une version `v1.0` stable, déployée, et testée en conditions réelles. **Aucune nouvelle fonctionnalité** : on gèle le code.

### Rôles

| Qui | Quoi |
|---|---|
| **Paulo** | Corrections de bugs, intégration finale, déploiement, tag `v1.0` |
| **Toute l'équipe** | Tests de production sur la version déployée, signalement des bugs |

### Déroulé

| Moment | Action |
|---|---|
| Début J13 | **Gel du code** : plus aucune PR de fonctionnalité sur `develop`. Seuls les correctifs de bugs sont acceptés |
| J13 matin | Paulo déploie la version `v0.2` + correctifs sur le serveur |
| J13 | **Tests de production** (voir ci-dessous). Chaque bug trouvé = une issue GitHub étiquetée `bug-prod` avec : étapes, résultat obtenu, résultat attendu, capture d'écran |
| J13-J14 | Paulo trie les bugs (bloquant / gênant / mineur), corrige les bloquants et gênants, redéploie. Les mineurs sont notés pour plus tard |
| J14 matin | Re-test des corrections par la personne qui a signalé le bug |
| J14 après-midi | Tag `v1.0`, démo finale, rétrospective |

### Tests de production (chacun teste d'abord **son** domaine, puis celui d'un autre)

| Qui | Scénarios à tester sur le serveur |
|---|---|
| **Back A** | Connexion/déconnexion et expiration du jeton ; création d'une agence + de son compte ; désactivation d'une agence (elle disparaît du public) ; envoi d'un logo (visible dans le front) ; connexion refusée pour une agence désactivée ; commissions du mois et changement du taux ; réception d'un email de rappel ; PDF d'un billet |
| **Back B** | Recherche avec tous les filtres ; **test de charge** sur les places (50 clients simultanés sur 5 places : exactement 5 billets) ; expiration d'une réservation à 5 min ; annulation à plus et à moins de 2 h ; double vérification d'un billet |
| **Front C** | Parcours admin d'agence sur téléphone : gare -> véhicule (photo visible dans la liste) -> trajet -> voyage -> vérification d'un billet par scan ; parcours super admin : agence -> commissions |
| **Front D** | Parcours voyageur sur téléphone : recherche (logos des agences visibles) -> réservation -> décompte -> paiement -> billet PDF -> annulation ; cas d'erreur (plus de place, réservation expirée) |
| **Test croisé** | Chacun refait **un parcours qu'il n'a pas écrit** (Back <-> Front) : on trouve plus de bugs en testant le travail d'un autre |

### Critères de sortie (la `v1.0` est prête quand)

- [ ] Le parcours voyageur complet fonctionne sur le serveur, sur un vrai téléphone
- [ ] Le parcours admin d'agence complet fonctionne
- [ ] Le test de charge sur les places ne vend jamais deux fois la même place
- [ ] Aucun bug bloquant ouvert
- [ ] Les logos des agences et les photos des véhicules s'affichent partout dans le front
- [ ] Aucun secret dans le dépôt
- [ ] Données de démonstration présentes sur le serveur

---

## 6. Dépendances et changement du contrat d'API

### Qui attend qui

| Qui attend | Quoi | Livré par | Pour quand | Solution en attendant |
|---|---|---|---|---|
| Front C, Front D, Back B (B5, B6) | Connexion / jeton | Back A (A1) + Front C (C1) | J4 | Mocks + jeton de démonstration |
| Front D (D1) | `GET /api/voyages` | Back B (B3) | J5 | Mock conforme à `API.md` |
| Front C, Front D | Upload de fichiers | Back A (A2) | J5 | Mock qui renvoie une URL d'exemple |
| Front D (D5-D7) | Réservation, paiement, billet | Back B (B8-B10) | J9-J10 | Mocks |
| Front C (C6) | Vérification | Back B (B11) | J11 | Mock |
| Back B (B9) | Enregistrement de la commission | Back A (A9) | J9 | Interface `CommissionService` définie dès J8, implémentation vide au début |
| Back B (B10) | Génération du PDF | Back A (A11) | J10 | Interface `BilletPdfService` définie dès J8 |

### Modifier le contrat d'API

1. On ne change **jamais** `API.md` soi-même.
2. On ouvre une issue étiquetée `api-change` : ce qu'on veut changer, pourquoi, quelles routes.
3. Paulo répond sous 24 h. S'il accepte, il modifie `API.md`, incrémente la version (`v0.2` -> `v0.3`) et l'annonce à l'équipe.
4. Entre-temps, on continue avec le contrat actuel.

---

## 7. Rôle de Paulo et limites du goulot

Paulo est l'intégrateur, **pas le développeur des fonctionnalités**. Pour qu'il ne bloque pas le projet :

- **Revue sous 24 h**, avec la relecture d'un coéquipier **avant** celle de Paulo.
- **PR petites** (moins de ~300 lignes) : une revue prend 10 minutes, pas une heure.
- Les modifications des fichiers partagés (`pom.xml`, `schema.sql`...) sont **groupées** et faites le matin.
- Paulo ne code pas de fonctionnalité d'un autre ; s'il y a un retard, on réduit le périmètre (section 9).
- Créneaux fixes : revues de PR **matin et fin de journée**, intégration **J7** et **J12**.

---

## 8. Rituels Scrum

| Rituel | Quand | Durée | Contenu |
|---|---|---|---|
| Planification | J3 et J8 | 30 min | Lecture des tâches, questions, engagement |
| Point quotidien | Chaque jour, même heure | 10 min | Hier / aujourd'hui / blocages |
| Revue (démo) | J7 et J12, après l'intégration | 30 min | Démonstration de la version `develop` |
| Rétrospective | J7, J12, J14 | 15 min | Ce qui marche, ce qu'on change |
| Suivi | Permanent | - | **GitHub Projects** (tableau Kanban : À faire, En cours, En revue, Terminé) |

---

## 9. Tâches optionnelles (on les coupe dans cet ordre si le temps manque)

1. **Carte et proximité** (D4)
2. **Modèles de voyage récurrents** (`/api/agence/modeles`)
3. **Réutilisation du billet** (RG8)

**Campay est abandonné** (pas de compte disponible) : le paiement reste simulé (`SIMULE`) dans cette version.

---

## 10. Risques

| Risque | Conséquence | Parade |
|---|---|---|
| L'auth est en retard | Tout l'admin est bloqué | Back A la fait en premier ; mocks côté front ; jeton de démonstration |
| Dérive du contrat d'API | Le front et le back ne se comprennent plus à l'intégration | Contrat figé ; changements par issue `api-change` uniquement |
| Conflits Git | Perte de temps à l'intégration | Un propriétaire par fichier ; PR petites ; `develop` récupéré chaque matin |
| SQLite et écritures concurrentes | Erreurs « base verrouillée » sous charge | Mode WAL déjà activé ; décompte atomique (RG3) ; test de concurrence dès le Sprint 1 (B7) |
| Back B surchargé au Sprint 2 | Retard sur la réservation et le paiement | Priorité : B8, B9, B10 ; B11/B12 peuvent glisser vers Back A après A7-A11 |
| Les images ne s'affichent pas dans le front | Logos et photos absents de l'interface | Le back renvoie l'URL complète ; `/files/**` testé dans un navigateur ; vérification à chaque intégration |
| Port occupé (déjà vécu) | L'API ne démarre pas | Port 6012 ; `ss -ltnp \| grep 6012` pour vérifier |

---

## 11. Décisions validées

| Sujet | Décision |
|---|---|
| Réservations en attente | Maximum **3** par téléphone et par IP (RG19) |
| Tri des résultats | Heure d'abord ; distance ensuite, seulement si la position est envoyée (option carte) |
| Agence désactivée | Elle disparaît du public **et son compte ne peut plus se connecter** (RG16) |
| Commission | **0,7 % par défaut, modifiable par le super admin depuis l'interface** ; le changement ne s'applique qu'aux réservations payées après la modification ; arrondi à l'entier le plus proche |
| Campay | **Pas de compte : abandonné.** Paiement simulé uniquement |
