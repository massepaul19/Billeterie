# TODO Frontend

Cocher au fur et à mesure. Chaque module a son `TODO.md` (une ligne + les routes) ; le contrat est `docs/API.md`.

## Ordre de livraison
- Connexion : J4 (Abdias)
- Recherche : J5 (Bouba)
- En attendant le back : données simulées (USE_MOCK=true).

## Front C (Abdias)
### Sprint 1
- [ ] **Connexion** (`lib/features/auth/`) : Écran de connexion (téléphone + mot de passe) : garde le jeton et redirige selon le rôle. À livrer pour J4.
- [ ] **Agences (super admin)** (`lib/features/super_admin_agences/`) : Liste des agences (activer/désactiver), formulaire avec logo, création du compte admin de l'agence.
- [ ] **Trajets** (`lib/features/agence_trajets/`) : Liste et formulaire des trajets (gare de départ, gare d'arrivée, durée).
- [ ] **Voyages** (`lib/features/agence_voyages/`) : Liste des voyages, création (trajet, véhicule, date, prix), changement de prix, annulation.

### Sprint 2
- [ ] **Réservations (agence)** (`lib/features/agence_reservations/`) : Liste filtrable des réservations de l'agence (client, voyage, montant, état).
- [ ] **Vérification des billets** (`lib/features/agence_verification/`) : Scan du QR (ou recherche nom/téléphone) et affichage immédiat du statut du billet.
- [ ] **Commissions (super admin)** (`lib/features/commissions/`) : Relevé mensuel des commissions par agence et modification du taux.
- [ ] **Paramètres de rappel** (`lib/features/agence_parametres/`) : Choix des délais de rappel par email (24 h et 2 h par défaut).
- [ ] **Remboursements (agence)** (`lib/features/agence_remboursements/`) : Liste des demandes de remboursement avec boutons Traité et Refusé.

## Front D (Bouba)
### Sprint 1
- [ ] **Recherche de voyages** (`lib/features/recherche/`) : Liste de voyages dès l'ouverture, filtrée en direct (villes, date, heure, type, clim, wifi, texte libre). À livrer pour J5.
- [ ] **Gares** (`lib/features/agence_gares/`) : Liste et formulaire des gares (ville, nom, quartier, adresse, position).
- [ ] **Véhicules** (`lib/features/agence_vehicules/`) : Liste et formulaire des véhicules, avec envoi de la photo.
- [ ] **Carte des agences** (`lib/features/carte/`) : Carte des agences actives, marqueurs cliquables (option).

### Sprint 2
- [ ] **Réservation** (`lib/features/reservation/`) : Formulaire de contact et décompte de 5 minutes pendant lequel la place est retenue.
- [ ] **Paiement** (`lib/features/paiement/`) : Choix MTN ou Orange, numéro, paiement simulé, puis succès ou échec.
- [ ] **Billet** (`lib/features/billet/`) : Affichage du billet, ouverture du PDF et annulation.
