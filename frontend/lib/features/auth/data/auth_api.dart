import '../../../core/network/api_client.dart';

/// Appels HTTP du module Connexion (contrat : docs/API.md).
class AuthApi {
  final ApiClient client;
  AuthApi(this.client);

  // TODO : une méthode par route
  // POST /api/auth/connexion
  // POST /api/auth/mot-de-passe
}
