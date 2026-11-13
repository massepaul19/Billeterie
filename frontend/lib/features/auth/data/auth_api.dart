import '../../../core/network/api_client.dart';

/// Authentification selon docs/API.md
class AuthApi {
  final ApiClient client;

  AuthApi(this.client);

  Future<Map<String, dynamic>> connexion({
    required String telephone,
    required String motDePasse,
  }) async {
    final response = await client.dio.post(
      '/api/auth/connexion',
      data: {
        'telephone': telephone,
        'motDePasse': motDePasse,
      },
    );

    final data = Map<String, dynamic>.from(response.data);

    final token = data['token'] as String?;
    final role = data['role'] as String?;

    if (token == null || role == null) {
      throw Exception('Réponse du serveur invalide.');
    }

    await client.sauverSession(
      token: token,
      role: role,
      agenceId: data['agenceId'] as int?,
    );

    return data;
  }

  Future<void> changerMotDePasse({
    required String ancien,
    required String nouveau,
  }) async {
    await client.dio.post(
      '/api/auth/mot-de-passe',
      data: {
        'ancien': ancien,
        'nouveau': nouveau,
      },
    );
  }
}
