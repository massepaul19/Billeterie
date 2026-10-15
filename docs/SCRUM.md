# Organisation Scrum - 2 semaines jusqu'au déploiement

Le détail des tâches par personne et par sprint est dans `docs/DECOUPAGE_TRAVAIL.md`.

## Rôles
| Rôle | Personne | Responsabilités |
|---|---|---|
| Paulo | Paulo | Modélisation et structure (Sprint 0), **intégration**, **déploiement**, revues, arbitrage de l'API |
| Back A | Balla | Auth/JWT, upload de fichiers, agences, gares, véhicules, paramètres de rappel, remboursements, commissions (taux modifiable), emails, billet PDF + QR |
| Back B | Patricia | Recherche publique, trajets, voyages, réservation, paiement, billet, réservations agence, vérification, expiration |
| Front C | Abdias | Connexion, agences, trajets, voyages, réservations, vérification QR, commissions (taux), paramètres de rappel, remboursements |
| Front D | Bouba | Recherche, carte, réservation, paiement, billet, gares, véhicules |

## Calendrier
| Jours | Sprint | Contenu |
|---|---|---|
| J1-J2 | Sprint 0 | Paulo : schéma SQLite, entités, structure, guides, TODO, contrat d'API |
| J3-J7 | Sprint 1 | Auth, agences, gares, véhicules, trajets, voyages, recherche publique. **J7 : intégration + 1re version sur le serveur** |
| J8-J12 | Sprint 2 | Réservation 5 min, paiement simulé, billet PDF+QR, vérification, annulation, rappels email, commissions. **J12 : intégration** |
| J13-J14 | Sprint 3 (stabilisation) | Gel du code. Paulo : corrections, intégration finale, déploiement, tag `v1.0`. Toute l'équipe : tests de production, dont le test de charge sur les places |

Chaque sprint : planification (30 min) au début, point quotidien de 10 min (hier / aujourd'hui / blocages),
revue de fin de sprint (démo), rétrospective courte.

## Git / GitHub
- Branches : `main` (stable, déployée), `develop` (intégration), `feature/<module>-<nom>`
  (ex. `feature/auth-jwt`, `feature/recherche-voyages`).
- On ne pousse **jamais** directement sur `main` ni `develop`.
- Flux : brancher depuis `develop` -> coder -> tests -> Pull Request vers `develop` -> relue par un coéquipier du même domaine -> Paulo merge (sous 24 h).
- Commits : `feat(module): ...`, `fix(module): ...`, `test(module): ...`, `docs: ...`
- Récupérer `develop` tous les matins avant de coder.

## Définition de « terminé » (pour chaque tâche)
- [ ] Respecte `docs/API.md` (si le contrat doit changer : on prévient Paulo d'abord)
- [ ] Tests écrits et qui passent (back : `mvn test` ; front : `flutter test`)
- [ ] Pas de secret dans Git (mots de passe, jetons)
- [ ] Si la tâche gère des images : elles s'affichent dans l'interface
- [ ] Case cochée dans le `TODO` du module
- [ ] Pull Request ouverte, décrite en deux lignes

## Intégration (Paulo)
À J7 et J12 : récupérer `develop`, lancer le back, brancher le front dessus, corriger les écarts, vérifier que les images s'affichent, tagger
(`v0.1`, `v0.2`), publier sur le serveur.

## Tâches optionnelles (si le temps manque, on les coupe en premier)
Carte et proximité, modèles de voyage récurrents, réutilisation du billet.
Campay est abandonné (pas de compte) : le paiement reste simulé.
