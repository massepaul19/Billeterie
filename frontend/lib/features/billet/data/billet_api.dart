import '../../../core/network/api_client.dart';

/// Appels HTTP du module Billet (contrat : docs/API.md).
class BilletApi {
  final ApiClient client;
  BilletApi(this.client);

  // TODO : une méthode par route
  // GET /api/billets/{code}
  // GET /api/billets/{code}/pdf
  // POST /api/billets/{code}/annulation
}
