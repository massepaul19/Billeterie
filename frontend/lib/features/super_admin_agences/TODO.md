# Agences (super admin)

Propriétaire : Front C (Abdias) - Sprint 1

**À coder :** Liste des agences (activer/désactiver), formulaire avec logo, création du compte admin de l'agence.

**Routes :**
- `GET/POST/PUT/DELETE /api/admin/agences`
- `PATCH /api/admin/agences/{id}/activation`
- `POST /api/admin/agences/{id}/compte`
- `POST /api/admin/agences/{id}/logo (multipart, champ fichier)`
- `POST /api/admin/comptes/{id}/reinitialisation-mot-de-passe`