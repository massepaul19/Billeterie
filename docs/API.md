# Contrat d'API - v0.2 (Paulo)

**Ce document est la référence entre back et front. Toute modification passe par Paulo.**

- Base : `/api` - JSON UTF-8 - champs en camelCase.
- Montants : entiers en FCFA. Dates : ISO-8601 heure locale Africa/Douala (`2026-11-02T07:30:00`).
- Auth : `Authorization: Bearer <jwt>` pour `/api/admin/**` (SUPER_ADMIN) et `/api/agence/**` (ADMIN_AGENCE).
  L'`agenceId` vient TOUJOURS du jeton.
- Le public (recherche, réservation, paiement, billet) n'a pas besoin de jeton.

## Erreurs
```json
{ "code": "PLACES_INDISPONIBLES", "message": "Il n'y a plus de place pour ce voyage." }
```
HTTP 400 `VALIDATION` - 401 `NON_AUTHENTIFIE` - 403 `INTERDIT` - 404 `INTROUVABLE` -
409 `PLACES_INDISPONIBLES` / `RESERVATION_EXPIREE` - 422 `ANNULATION_TARDIVE` / `BILLET_INVALIDE` -
429 `TROP_DE_RESERVATIONS`.

## Pagination
`?page=0&taille=20` -> `{ "contenu": [...], "page": 0, "taille": 20, "total": 134, "derniere": false }`

## Fichiers (images)
- Les colonnes `agence.logo` et `vehicule.image` contiennent le chemin relatif du fichier sur le serveur.
- **Les réponses JSON renvoient l'URL complète** dans `logo` et `image` (ex. `http://localhost:6012/files/agences/3f2a9c.png`), ou `null` s'il n'y a pas d'image. Le front n'a qu'à afficher cette URL.
- Lecture publique, sans jeton : `GET /files/**`.
- Envoi : `multipart/form-data`, champ `fichier`. Formats jpg, png, webp ; 2 Mo maximum ; sinon 400 `VALIDATION`.
- Une image envoyée doit s'afficher dans l'interface (RG20).

---
## Public

### GET /api/villes?q=ya
`[ { "id": 1, "nom": "Yaoundé" } ]`

### GET /api/agences  (carte)
`[ { "id": 3, "nom": "Express Nord", "logo": "url", "slogan": "...", "ville": "Garoua", "latitude": 9.3, "longitude": 13.4 } ]`
Seulement les agences actives.

### GET /api/voyages
Paramètres (tous optionnels) : `depart`, `destination` (noms de villes), `dateDebut`, `dateFin`, `heure` (HH:mm),
`type` (SIMPLE|VIP), `clim`, `wifi`, `q` (texte libre + synonymes),
`latitude` et `longitude` (OPTION carte, pour départager par distance), `page`, `taille`.
Réponse paginée de :
```json
{
  "id": 12,
  "agence": { "id": 3, "nom": "Express Nord", "logo": "url", "slogan": "..." },
  "depart": { "ville": "Yaoundé", "gare": "Gare Mvan", "quartier": "Mvan" },
  "arrivee": { "ville": "Douala", "gare": "Gare Bonabéri", "quartier": "Bonabéri" },
  "dateHeureDepart": "2026-11-02T07:30:00",
  "dureeEstimeeMin": 300,
  "prix": 5000,
  "vehicule": { "type": "VIP", "clim": true, "wifi": true, "image": null }
}
```
Jamais de voyage passé, annulé ou sans place. **Pas de `placesRestantes`.**
Tri : proximité de l'heure demandée ; si rien ne correspond, les départs les plus proches ; puis la distance
si la position du voyageur est envoyée (jamais enregistrée, RG18).
`logo` et `image` sont des URL complètes (ou `null`).

### GET /api/voyages/{id}  -> même objet que ci-dessus

### POST /api/reservations
```json
{ "voyageId": 12, "nom": "Mballa", "prenom": "Jean", "telephone": "677000000", "email": "jean@mail.com" }
```
201 : `{ "id": 40, "voyageId": 12, "statut": "EN_ATTENTE", "montant": 5000, "expireA": "2026-11-01T10:05:00" }`
Erreurs : 409 `PLACES_INDISPONIBLES`, 429 `TROP_DE_RESERVATIONS`.

### GET /api/reservations/{id}  -> même objet (statut à jour : EN_ATTENTE, PAYEE, EXPIREE, ANNULEE)

### POST /api/paiements
```json
{ "reservationId": 40, "fournisseur": "SIMULE", "operateur": "MTN", "telephonePaiement": "677000000" }
```
200 : `{ "id": 7, "statut": "REUSSI", "reference": "SIM-...", "reservationStatut": "PAYEE", "codeBillet": "A1B2C3D4E5F6G7H8" }`
Erreur : 409 `RESERVATION_EXPIREE`.

### GET /api/billets/{code}
```json
{ "code": "A1B2C3D4E5F6G7H8", "statut": "VALIDE", "valideJusquA": "2026-11-02T07:30:00",
  "client": { "nom": "Mballa", "prenom": "Jean" }, "voyage": { "...": "comme /api/voyages/{id}" } }
```
### GET /api/billets/{code}/pdf   -> `application/pdf`
### POST /api/billets/{code}/annulation
200 : `{ "remboursement": { "montant": 4750, "statut": "DEMANDE" } }` - 422 `ANNULATION_TARDIVE` si moins de 2 h.
### POST /api/billets/{code}/reutilisation   (OPTION)  `{ "voyageId": 15 }`

---
## Authentification
### POST /api/auth/connexion
`{ "telephone": "699000000", "motDePasse": "..." }` -> `{ "token": "...", "role": "ADMIN_AGENCE", "agenceId": 3 }`
403 `INTERDIT` si l'agence du compte est désactivée (RG16).
### POST /api/auth/mot-de-passe   (connecté)  `{ "ancien": "...", "nouveau": "..." }` -> 204

---
## Super administrateur (`/api/admin`)
| Méthode | Route | Corps / remarque |
|---|---|---|
| GET | /agences | liste (actives et inactives) |
| POST | /agences | `{ nom, slogan, villeId, latitude, longitude, numeroMobileMoney }` (le logo s'envoie à part) |
| PUT | /agences/{id} | idem |
| DELETE | /agences/{id} | |
| PATCH | /agences/{id}/activation | `{ "actif": false }` |
| POST | /agences/{id}/compte | `{ telephone, email, motDePasseInitial }` -> crée l'admin de l'agence |
| POST | /agences/{id}/logo | `multipart/form-data`, champ `fichier` -> `{ "logo": "url" }` |
| POST | /comptes/{id}/reinitialisation-mot-de-passe | `{ "nouveauMotDePasse": "..." }` |
| GET | /commissions?mois=2026-11 | `[ { agenceId, agenceNom, totalVentes, commission, statutFacturation } ]` |
| GET | /parametres/commission | `{ "taux": 0.007 }` |
| PUT | /parametres/commission | `{ "taux": 0.01 }` -> nouveau taux, appliqué aux réservations payées ensuite (RG12) |

## Administrateur d'agence (`/api/agence`)
| Méthode | Route | Corps / remarque |
|---|---|---|
| GET/POST/PUT/DELETE | /gares | `{ villeId, nom, quartier, adresse, latitude, longitude }` |
| GET/POST/PUT/DELETE | /vehicules | `{ immatriculation, type, nbPlaces, clim, wifi }` (la photo s'envoie à part) |
| POST | /vehicules/{id}/image | `multipart/form-data`, champ `fichier` -> `{ "image": "url" }` |
| POST | /logo | `multipart/form-data`, champ `fichier` -> `{ "logo": "url" }` (logo de sa propre agence) |
| GET/POST/PUT/DELETE | /trajets | `{ gareDepartId, gareArriveeId, dureeEstimeeMin }` |
| GET/POST/PUT | /voyages | `{ trajetId, vehiculeId, dateHeureDepart, prix }` (places = nbPlaces du véhicule) |
| PATCH | /voyages/{id}/prix | `{ "prix": 6000 }` |
| POST | /voyages/{id}/annulation | annule + remboursements à 100 % |
| GET/POST/PUT/DELETE | /modeles | OPTION : `{ trajetId, vehiculeId, prix, joursSemaine, heure, dateDebut, dateFin }` |
| GET | /reservations?statut=&voyageId= | liste paginée avec client et billet |
| POST | /billets/verification | `{ "code": "..." }` -> `{ statut, client, voyage }` ; passe à UTILISE si VALIDE |
| GET | /billets/recherche?q= | par nom ou téléphone |
| GET/PUT | /parametres/rappels | `{ "delaisHeures": [24, 2] }` |
| GET | /remboursements | demandes et statuts |
| PATCH | /remboursements/{id} | `{ "statut": "TRAITE" }` |
