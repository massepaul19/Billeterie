# Guide de programmation - Frontend (Flutter)

Pour : Front C (administration) et Front D (public). Lire aussi `docs/SCRUM.md` et `docs/API.md`.

## Lancer
```
cd frontend
flutter create . --project-name billetterie_app   # une seule fois (génère android/, web/, ...)
flutter pub get
flutter run --dart-define=API_URL=http://localhost:8080
```
Émulateur Android : `API_URL=http://10.0.2.2:8080`.

## Architecture (`lib/`)
| Dossier | Rôle |
|---|---|
| `core/` | Configuration, client HTTP (`ApiClient`), thème, utilitaires - partagé |
| `features/<module>/data/` | Appels HTTP du module (`<module>_api.dart`) |
| `features/<module>/presentation/` | Pages et widgets du module |
| `features/<module>/TODO.md` | Ce qu'il faut faire, routes utilisées, tests attendus |

## Règles
1. Un module = un dossier. On ne modifie pas le dossier d'un autre sans le prévenir.
2. Les appels réseau passent **uniquement** par `ApiClient` et les classes `*Api`. Pas de `http` dans les widgets.
3. Respecter **exactement** `docs/API.md`. En cas de doute ou de besoin d'un nouveau champ : demander à Paulo.
4. Chaque écran gère 4 états : chargement, vide, erreur, données.
5. **Mobile d'abord** : tester sur petit écran. Textes en français, montants en FCFA (`formatFcfa`).
6. Le public ne voit jamais le nombre de places restantes.
7. Pas de secret ni d'adresse de serveur en dur : `ApiConfig`.
8. Ne pas toucher à `main.dart` sauf pour ajouter votre page à l'accueil (Paulo fait la vraie navigation).

## Tests attendus
Test de widget par page (états chargement / vide / erreur / données) et test de la classe `*Api`
avec un `ApiClient` simulé. Commande : `flutter test`.

## Suivi
Global : `frontend/TODO.md`. Par module : `lib/features/<module>/TODO.md`.
