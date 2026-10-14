import '../../../core/network/api_client.dart';

/// Appels HTTP du module Recherche de voyages (contrat : docs/API.md).
class RechercheApi {
  final ApiClient client;
  RechercheApi(this.client);

  // TODO : une méthode par route
  // GET /api/villes
  // GET /api/voyages
  // GET /api/voyages/{id}
}
