import '../../../core/network/api_client.dart';

/// Appels HTTP du module Agences (super admin) (contrat : docs/API.md).
class SuperAdminAgencesApi {
  final ApiClient client;
  SuperAdminAgencesApi(this.client);

  // TODO : une méthode par route
  // GET/POST/PUT/DELETE /api/admin/agences
  // PATCH /api/admin/agences/{id}/activation
  // POST /api/admin/agences/{id}/compte
}
