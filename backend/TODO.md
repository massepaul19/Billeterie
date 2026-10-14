# TODO Backend

Cocher au fur et à mesure. Propriétaire et sprint indiqués.

## Sprint 1
- [ ] **Auth** (Back A) `/api/auth` : POST /connexion ; POST /mot-de-passe (changer)
- [ ] **Ville** (Back B) `/api/villes` : GET ?q= (suggestions)
- [ ] **AgencePublique** (Back B) `/api/agences` : GET (liste pour la carte) ; GET /{id}
- [ ] **VoyagePublic** (Back B) `/api/voyages` : GET (recherche dynamique, paginée) ; GET /{id}
- [ ] **SuperAdminAgence** (Back A) `/api/admin/agences` : GET, POST, PUT /{id}, DELETE /{id} ; PATCH /{id}/activation ; POST /{id}/compte (créer le compte admin) ; POST /comptes/{id}/reinitialisation-mot-de-passe
- [ ] **AgenceGare** (Back A) `/api/agence/gares` : CRUD complet (filtré par agenceId du jeton)
- [ ] **AgenceVehicule** (Back A) `/api/agence/vehicules` : CRUD complet
- [ ] **AgenceTrajet** (Back A) `/api/agence/trajets` : CRUD complet
- [ ] **AgenceVoyage** (Back A) `/api/agence/voyages` : GET, POST, PUT /{id} ; PATCH /{id}/prix ; POST /{id}/annulation (rembourse à 100 %)
- [ ] **AgenceModele** (Back A) `/api/agence/modeles` : CRUD + génération des voyages récurrents (OPTION)
- [ ] **JWT / sécurité** (Back A) : voir `security/TODO_JWT.md`
- [ ] **DemoDataLoader** (Back A) : agences et voyages de démonstration
- [ ] **GlobalExceptionHandler** (Back A) : validation, 403, 500

## Sprint 2
- [ ] **Reservation** (Back B) `/api/reservations` : POST (bloque 5 min) ; GET /{id}
- [ ] **Paiement** (Back B) `/api/paiements` : POST ; GET /{id}
- [ ] **Billet** (Back B) `/api/billets` : GET /{code} ; GET /{code}/pdf ; POST /{code}/annulation ; POST /{code}/reutilisation (OPTION)
- [ ] **AgenceReservation** (Back B) `/api/agence/reservations` : GET (liste filtrée)
- [ ] **AgenceVerification** (Back B) `/api/agence/billets` : POST /verification {code} ; GET /recherche?q= (nom ou téléphone)
- [ ] **AgenceParametres** (Back A) `/api/agence/parametres` : GET, PUT /rappels
- [ ] **AgenceRemboursement** (Back A) `/api/agence/remboursements` : GET ; PATCH /{id} (statut)
- [ ] **SuperAdminCommission** (Back A) `/api/admin/commissions` : GET ?mois=YYYY-MM
- [ ] **Expiration des réservations** (Back B) : `ExpirationReservationScheduler`
- [ ] **Rappels email Gmail** (Back A) : `RappelScheduler` + `EmailService`
- [ ] **Billet PDF + QR** (Back B) : `BilletPdfService`
- [ ] **Commission 0,7 %** (Back A) : calculée à chaque paiement réussi + relevé mensuel
- [ ] **Test de concurrence** (Back B) : `ReservationConcurrenceTest`
