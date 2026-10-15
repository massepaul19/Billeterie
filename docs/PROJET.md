# Projet - résumé du cahier des charges (v1.0, adapté)

## Problème
Pour voyager en bus, il faut se déplacer à l'agence pour connaître horaires, prix et places. Les agences
n'ont pas toutes d'outil de vente en ligne ni de moyen simple de vérifier un billet.

## Solution
Plateforme unique : les agences publient leurs voyages et vérifient les billets ; les voyageurs cherchent,
réservent et paient **sans compte** et reçoivent un billet PDF avec QR code ; le super administrateur crée
les agences et suit la commission de 0,7 %.

## Changements par rapport au cahier des charges v1.0
| v1.0 | Maintenant |
|---|---|
| FastAPI | **Java (Spring Boot)** |
| MySQL | **SQLite** (un fichier portable) |
| Interface Jinja2 + PWA | **Flutter** |
| Notifications push + email | **Email (Gmail) uniquement** |

## Acteurs
Super administrateur (SUPER_ADMIN), administrateur d'agence (ADMIN_AGENCE, un par agence), client sans compte.

## Exclus de cette version
Réservation de groupe, pièce d'identité, choix de la place, bagages, litiges, exécution des remboursements
(faite par l'agence), ramassage, rôles multiples par agence, réductions, SMS/WhatsApp, autres langues,
prélèvement automatique de la commission.

## Priorités
- **Obligatoire** : agences, voyages, recherche, réservation 5 min, paiement simulé, billet PDF+QR,
  vérification, annulation, email de rappel, commissions.
- **Option si le temps le permet** : carte et proximité, modèles de voyage récurrents,
  réutilisation d'un billet.
  Campay est abandonné (pas de compte) : le paiement reste simulé.
