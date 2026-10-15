# TODO Backend

Cocher au fur et à mesure. Le contrat est `docs/API.md` ; base de démonstration : `python3 database/gen_demo_data.py`.

## Ordre de livraison
- Connexion : J4 (Balla)
- Recherche de voyages et envoi de fichiers : J5 (Patricia et Balla)

## Back A (Balla)
### Sprint 1
- [ ] **Auth** : POST /api/auth/connexion ; POST /api/auth/mot-de-passe
- [ ] **SuperAdminAgence** : GET /api/admin/agences ; POST /api/admin/agences ; PUT /api/admin/agences/{id} ; DELETE /api/admin/agences/{id} ; PATCH /api/admin/agences/{id}/activation ; POST /api/admin/agences/{id}/compte ; POST /api/admin/agences/{id}/logo ; POST /api/admin/comptes/{id}/reinitialisation-mot-de-passe
- [ ] **AgenceGare** : GET /api/agence/gares ; POST /api/agence/gares ; PUT /api/agence/gares/{id} ; DELETE /api/agence/gares/{id}
- [ ] **AgenceVehicule** : GET /api/agence/vehicules ; POST /api/agence/vehicules ; PUT /api/agence/vehicules/{id} ; DELETE /api/agence/vehicules/{id} ; POST /api/agence/vehicules/{id}/image
- [ ] **AgenceLogo** : POST /api/agence/logo
- [ ] **Fichiers** : `FichierStorageService` + `FichiersConfig` (GET /files/** public, images jpg/png/webp 2 Mo max, URL complète dans logo et image)
- [ ] **Sécurité JWT** : voir `security/TODO_JWT.md`
- [ ] **DemoDataLoader** et **GlobalExceptionHandler**

### Sprint 2
- [ ] **AgenceParametres** : GET /api/agence/parametres/rappels ; PUT /api/agence/parametres/rappels
- [ ] **AgenceRemboursement** : GET /api/agence/remboursements ; PATCH /api/agence/remboursements/{id}
- [ ] **SuperAdminCommission** : GET /api/admin/commissions?mois=YYYY-MM
- [ ] **SuperAdminParametre** : GET /api/admin/parametres/commission ; PUT /api/admin/parametres/commission
- [ ] **Rappels par email (Gmail)** : `RappelScheduler` + `EmailService`
- [ ] **Billet PDF + QR** : `BilletPdfService`

## Back B (Patricia)
### Sprint 1
- [ ] **Ville** : GET /api/villes?q=
- [ ] **AgencePublique** : GET /api/agences
- [ ] **VoyagePublic** : GET /api/voyages?depart=&destination=&dateDebut=&dateFin=&heure=&type=&clim=&wifi=&q=&page=&taille= ; GET /api/voyages/{id}
- [ ] **AgenceTrajet** : GET /api/agence/trajets ; POST /api/agence/trajets ; PUT /api/agence/trajets/{id} ; DELETE /api/agence/trajets/{id}
- [ ] **AgenceVoyage** : GET /api/agence/voyages ; POST /api/agence/voyages ; PUT /api/agence/voyages/{id} ; PATCH /api/agence/voyages/{id}/prix ; POST /api/agence/voyages/{id}/annulation
- [ ] **AgenceModele** (OPTION) : GET /api/agence/modeles ; POST /api/agence/modeles ; PUT /api/agence/modeles/{id} ; DELETE /api/agence/modeles/{id}
- [ ] **Test de concurrence** : `ReservationConcurrenceTest` (50 threads, 5 places)

### Sprint 2
- [ ] **Reservation** : POST /api/reservations ; GET /api/reservations/{id}
- [ ] **Paiement** : POST /api/paiements
- [ ] **Billet** : GET /api/billets/{code} ; GET /api/billets/{code}/pdf ; POST /api/billets/{code}/annulation ; POST /api/billets/{code}/reutilisation
- [ ] **AgenceReservation** : GET /api/agence/reservations?statut=&voyageId=
- [ ] **AgenceVerification** : POST /api/agence/billets/verification ; GET /api/agence/billets/recherche?q=
- [ ] **Expiration des réservations** : `ExpirationReservationScheduler`
