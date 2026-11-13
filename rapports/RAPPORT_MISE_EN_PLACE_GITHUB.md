# Rapport : base de démo et organisation GitHub du projet Billetterie

**Date :** 15 octobre 2026
**Projet :** Billetterie multi-agences de transport (projet d'équipe, 5 personnes)
**Stack :** SQLite, back-end Java (Spring), front-end Flutter, rappels par e-mail

---

## 1. Résumé

Cette séance a eu deux objectifs : terminer la base de données de démonstration, puis mettre en place le suivi du projet sur GitHub (sans Trello), selon le découpage de `DECOUPAGE_TRAVAIL.md`.

| Objectif | Résultat |
|---|---|
| Base de démo complète | 17 tables, données de démo intactes, cas de test ajoutés |
| Outil `gh` installé et connecté | Version 2.102.0 (dépôt officiel), compte connecté avec le droit `project` |
| Suivi GitHub | Étiquettes, jalons, tableau, 55 issues, vue « Sprint en cours » |
| Documentation | Support de cours GitHub (document en ligne et PDF dans `docs/`) |
| Dépôt à jour | `main` et `develop` au même commit, accès en HTTPS |
| Invitations de l'équipe | **Pas encore faites** |

---

## 2. La base de données de démonstration

### 2.1 Ajout des cas de test (`database/ajout_demo.py`)

Le script a été lancé avec succès :

- un **voyage à 5 places** (id 4877, départ le 18 octobre 2026 à 18 h) pour le test de concurrence « 50 clients pour 5 places » ;
- un **client anti-abus** (téléphone 699999999) avec 3 réservations en attente, pour tester la limite de 3 (RG19) ;
- **13 logos et 40 photos** de démonstration dans `backend/uploads/` ;
- **0 clé étrangère invalide**.

Les images sont de simples carrés de couleur générés par le script. Aucune image n'a été téléchargée d'internet. Le dossier `backend/uploads/` est ignoré par Git : chaque membre de l'équipe doit relancer les scripts pour les obtenir.

### 2.2 La table `parametre`

La comparaison entre `.tables` et `schema.sql` a montré qu'**une seule table manquait** : `parametre`. Le générateur n'a pas été relancé (pour ne pas risquer de toucher aux 4 876 voyages existants). La table a été créée seule, avec une commande protégée contre les doublons :

```sql
CREATE TABLE IF NOT EXISTS parametre (cle TEXT PRIMARY KEY, valeur TEXT NOT NULL, modifie_le TEXT);
INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('commission_taux', '0.007');
```

Résultat : 17 tables, avec `commission_taux = 0.007`.

**Principe retenu :** regarder d'abord ce qui existe dans la base, puis ne compléter que ce qui manque, au lieu de relancer un générateur.

---

## 3. Mise en place de GitHub

### 3.1 Outil `gh`

- Le paquet snap proposé venait d'un particulier (« Casper ») et non de GitHub. Il a été **supprimé**.
- Installation de la **version officielle** depuis le dépôt de GitHub : `gh` 2.102.0.
- Connexion par navigateur au compte `massepaul19`, puis ajout de l'autorisation `project`.

### 3.2 Structure créée

| Élément | Détail |
|---|---|
| Dépôt | `massepaul19/Billeterie` (privé), branches `main` et `develop` |
| Étiquettes (9) | `back-a`, `back-b`, `front-c`, `front-d`, `paulo`, `api-change`, `config`, `bug-prod`, `option` |
| Jalons (3) | Sprint 1 (J3-J7), Sprint 2 (J8-J12), Sprint 3 (J13-J14) |
| Projet | « Billetterie - Suivi » (n°7), relié au dépôt |
| Statuts | Todo, In Progress, **En revue** (ajouté), Done |
| Vue | Board « Sprint en cours », filtrée sur le Sprint 1 |

### 3.3 Les 55 issues

| Issues | Contenu |
|---|---|
| #1 à #6 | Balla, Sprint 1 : A1 à A6 |
| #7 à #13 | Patricia, Sprint 1 : B1 à B7 |
| #14 à #17 | Abdias, Sprint 1 : C1 à C4 |
| #18 à #21 | Bouba, Sprint 1 : D1 à D4 (D4, la carte, porte l'étiquette `option`) |
| #22 à #34 | Paulo (P1, P2, P3, P5, P6 au Sprint 1 ; P7, P8, P9 au Sprint 2) et Balla Sprint 2 (A7 à A11) |
| #35 à #46 | Sprint 2 : B8 à B12, C5 à C8, D5 à D7 |
| #47 à #55 | Sprint 3 : gel du code, déploiement, tri des bugs, tests de production par personne, test croisé, tag v1.0 |

**Vérification finale :** 55 issues au total, soit 26 (Sprint 1), 20 (Sprint 2) et 9 (Sprint 3).

La tâche P4 (« mettre en place GitHub Projects ») n'a pas d'issue : elle correspond à ce qui a été fait pendant cette séance.

---

## 4. Décisions prises

| Sujet | Décision |
|---|---|
| Méthode | Scrum. Le tableau est un « tableau de sprint » (le mot « Kanban » a été retiré de `DECOUPAGE_TRAVAIL.md`, ligne 307) |
| Branches | Une branche par tâche, créée depuis `develop`. **Pas de branches `front-end` et `back-end` partagées** : les dossiers `frontend/` et `backend/` séparent déjà le code |
| Rôle de Paulo | Intégrateur. Peut travailler directement sur `develop` pour un petit changement de fichier partagé, après un `git pull` |
| Images de démo | Carrés de couleur suffisants pour vérifier l'affichage (RG20) ; de vraies images pourront être ajoutées dans `database/demo-images/` plus tard |
| Accès Git | HTTPS (voir section 5) |

---

## 5. Documentation et dépôt

- **Support de cours GitHub (Scrum)** : vocabulaire, cycle d'une tâche, branches, invitations, tableau, fin de sprint, aide-mémoire `gh`, pièges. Disponible en document en ligne et en PDF (`docs/support_cours_github_scrum.pdf`).
- **Commit** `a2c2603` « documentation ok » : le PDF et la correction de `DECOUPAGE_TRAVAIL.md`.
- **Problème de push :** la connexion SSH vers GitHub dépassait le délai (« Connection timed out during banner exchange »). Le dépôt a été **repassé en HTTPS**, et le push a réussi (`6c4e3c1..a2c2603`). `develop` a ensuite été mis à jour au même commit.
- **Contrôle avant partage :** aucun secret, base de données, dossier `build` ni image suivis par Git ; le `.gitignore` couvre `backend/uploads/`, les `.db`, les `.env` et `frontend/build/`. Les scripts de base (`schema.sql`, `seed.sql`, `gen_demo_data.py`, `ajout_demo.py`) sont bien versionnés. Seule image suivie : `favicon.png` de Flutter.

---

## 6. Points d'attention

1. **`Closes #n` ne ferme pas les issues tant que `main` est la branche par défaut.** GitHub ne ferme l'issue que si la PR est fusionnée dans la branche par défaut, alors que les PR visent `develop`. Solution proposée : passer `develop` en branche par défaut (`gh repo edit massepaul19/Billeterie --default-branch develop`). **Décision en attente.**
2. **Protection des branches :** le dépôt est privé, et cette protection est en principe réservée aux comptes payants dans ce cas. La commande n'a pas été lancée ; en cas d'échec, la règle « pas de push direct » reste une convention d'équipe.
3. **Collage de gros blocs dans le terminal :** l'affichage a été brouillé plusieurs fois sans abîmer les commandes (vérifié avec `gh issue view`). Pour les très gros blocs, mieux vaut passer par un fichier `.sh`.
4. **Relancer un bloc crée des doublons** (jalons, issues) : on lance une seule fois, puis on compte.

---

## 7. Reste à faire

- [ ] Récupérer les noms d'utilisateur GitHub de Balla, Abdias et Bouba. Pour Patricia, le compte `patriciasydney` a été indiqué, mais il n'a pas été vérifié ni invité.
- [ ] Inviter les 4 coéquipiers au dépôt (droit `push`) et attendre qu'ils acceptent.
- [ ] Assigner les issues à chacun.
- [ ] Décider de la branche par défaut (`develop` recommandée).
- [ ] Essayer de protéger `main` et `develop`.
- [ ] Annoncer à l'équipe les règles : une branche par tâche, PR vers `develop`, pas de push direct, revue sous 24 h.
- [ ] Vérifier dans **Workflows** du projet les automatisations (issue fermée vers Done).
- [ ] Sprint 1, tâches de Paulo : P2 (`API.md` v0.2 et schéma) et P3 (dépendances Spring Security et JWT dans `pom.xml`), à faire avant que Balla commence A1.

---

## 8. Commandes principales utilisées

| Besoin | Commande |
|---|---|
| Connexion | `gh auth login`, `gh auth refresh -s project` |
| Étiquettes | `gh label create NOM -R dépôt --color HEX --description '...' --force` |
| Jalons | `gh api repos/dépôt/milestones -f title='...'` |
| Projet | `gh project create`, `gh project link` |
| Issues | `gh issue create -R dépôt --title ... --label ... --milestone ... --project ...` |
| Vérification | `gh issue list`, `gh issue view`, `gh project item-list` |
| Accès HTTPS | `git remote set-url origin https://github.com/massepaul19/Billeterie.git` |
