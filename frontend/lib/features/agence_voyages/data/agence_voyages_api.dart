import '../../../core/network/api_client.dart';

/// Appels HTTP du module Voyages (contrat : docs/API.md).
class AgenceVoyagesApi {
  final ApiClient client;
  AgenceVoyagesApi(this.client);

  // TODO : une méthode par route
  // CRUD /api/agence/voyages
  // PATCH /api/agence/voyages/{id}/prix
  // POST /api/agence/voyages/{id}/annulation
}
