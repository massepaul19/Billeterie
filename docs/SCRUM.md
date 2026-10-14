# Organisation Scrum - 2 semaines jusqu'au déploiement

## Rôles
| Rôle | Personne | Responsabilités |
|---|---|---|
| Paulo | Paulo | Product Owner, modélisation et structure (Sprint 0), **intégration**, **déploiement**, arbitrage de l'API |
| Back A | ____ | Authentification/JWT, agences, gares, véhicules, trajets, voyages, commissions, email |
| Back B | ____ | Recherche publique, réservation, paiement, billet PDF+QR, vérification |
| Front C | ____ | Espace administration (super admin et agence) |
| Front D | ____ | Espace public (recherche, réservation, paiement, billet) |

## Calendrier
| Jours | Sprint | Contenu |
|---|---|---|
| J1-J2 | Sprint 0 | Paulo : schéma SQLite, entités, structure, guides, TODO, contrat d'API |
| J3-J7 | Sprint 1 | Auth, agences, gares, véhicules, trajets, voyages, recherche publique. **J7 : intégration + 1re version sur le serveur** |
| J8-J12 | Sprint 2 | Réservation 5 min, paiement simulé, billet PDF+QR, vérification, annulation, rappels email, commissions. **J12 : intégration** |
| J13-J14 | Stabilisation | Test de charge sur les places, corrections, déploiement |

Chaque sprint : planification (30 min) au début, point quotidien de 10 min (hier / aujourd'hui / blocages),
revue de fin de sprint (démo), rétrospective courte.

## Git / GitHub
- Branches : `main` (stable, déployée), `develop` (intégration), `feature/<module>-<nom>`
  (ex. `feature/auth-jwt`, `feature/recherche-voyages`).
- On ne pousse **jamais** directement sur `main` ni `develop`.
- Flux : brancher depuis `develop` -> coder -> tests -> Pull Request vers `develop` -> Paulo relit et merge.
- Commits : `feat(module): ...`, `fix(module): ...`, `test(module): ...`, `docs: ...`
- Récupérer `develop` tous les matins avant de coder.

## Définition de « terminé » (pour chaque tâche)
- [ ] Respecte `docs/API.md` (si le contrat doit changer : on prévient Paulo d'abord)
- [ ] Tests écrits et qui passent (back : `mvn test` ; front : `flutter test`)
- [ ] Pas de secret dans Git (mots de passe, jetons)
- [ ] Case cochée dans le `TODO` du module
- [ ] Pull Request ouverte, décrite en deux lignes

## Intégration (Paulo)
À J7 et J12 : récupérer `develop`, lancer le back, brancher le front dessus, corriger les écarts, tagger
(`v0.1`, `v0.2`), publier sur le serveur.

## Tâches optionnelles (si le temps manque, on les coupe en premier)
Carte et proximité, modèles de voyage récurrents, réutilisation du billet, Campay.
