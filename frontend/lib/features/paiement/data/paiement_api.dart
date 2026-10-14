import '../../../core/network/api_client.dart';

/// Appels HTTP du module Paiement (contrat : docs/API.md).
class PaiementApi {
  final ApiClient client;
  PaiementApi(this.client);

  // TODO : une méthode par route
  // POST /api/paiements
  // GET /api/paiements/{id}
}
