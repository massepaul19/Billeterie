# Règles de gestion

- **RG1** Une réservation = une place = un billet. Pas de réservation de groupe.
- **RG2** Les places sont identiques et attribuées automatiquement.
- **RG3** Décompte atomique : `UPDATE voyage SET places_restantes = places_restantes - 1 WHERE id = ? AND places_restantes >= 1` (déjà dans `VoyageRepository.reserverUnePlace`). Si 0 ligne modifiée = plus de place.
- **RG4** Les places sont retenues 5 minutes. Passé ce délai ou en cas d'échec : réservation EXPIREE, place libérée.
- **RG5** Un voyage sans place, passé ou annulé n'apparaît jamais dans les résultats publics. Le nombre de places restantes n'est jamais montré au public.
- **RG6** Le prix est fixé par l'agence par voyage ; une modification ne change pas les réservations déjà payées.
- **RG7** Le billet porte le logo de l'agence et un QR (code unique). Statuts : VALIDE, UTILISE, EXPIRE, ANNULE.
- **RG8** (OPTION) Voyage raté : billet réutilisable une seule fois pendant 2 jours, même trajet, même agence, selon les places ; ensuite EXPIRE.
- **RG9** Annulation par le client jusqu'à 2 h avant le départ : remboursement moins 5 %.
- **RG10** Annulation du voyage par l'agence : remboursement à 100 %.
- **RG11** Les remboursements sont faits par l'agence hors système ; la plateforme enregistre seulement la demande et son statut.
- **RG12** Commission de 0,7 % calculée et enregistrée sur chaque réservation payée (entier FCFA, arrondi à l'entier le plus proche : à valider), facturée chaque mois.
- **RG13** À l'embarquement : scan du QR ou recherche nom/téléphone ; le billet passe à UTILISE à la première validation.
- **RG14** Un administrateur d'agence ne voit et ne modifie que les données de son agence (agenceId pris dans le jeton).
- **RG15** Connexion par téléphone + mot de passe. Deux rôles : SUPER_ADMIN et ADMIN_AGENCE.
- **RG16** Une agence désactivée n'apparaît plus dans les résultats publics ; réactivable sans perte de données.
- **RG17** Les voyages partent toujours d'une gare de l'agence.
- **RG18** La position du voyageur n'est jamais enregistrée.
- **RG19** (anti-abus) Maximum de réservations EN_ATTENTE par téléphone et par IP : 3 (à valider).

## Points à valider en équipe
Limite de réservations en attente, tri par défaut (heure puis distance), blocage de la connexion d'une agence
désactivée, arrondi de la commission, compte Campay, répartition des modules.
