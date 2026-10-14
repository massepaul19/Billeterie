import '../../../core/network/api_client.dart';

/// Appels HTTP du module Vérification des billets (contrat : docs/API.md).
class AgenceVerificationApi {
  final ApiClient client;
  AgenceVerificationApi(this.client);

  // TODO : une méthode par route
  // POST /api/agence/billets/verification
  // GET /api/agence/billets/recherche
}
