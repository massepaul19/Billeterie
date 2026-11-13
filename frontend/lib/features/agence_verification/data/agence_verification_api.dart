import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class AgenceVerificationApi {
  final ApiClient client;
  AgenceVerificationApi(this.client);

  /// Passe le billet à UTILISE s'il est VALIDE. Réponse : { statut, accepte, client, voyage }.
  Future<Map<String, dynamic>> verifier(String code) async => Map<String, dynamic>.from(
      (await client.dio.post('/api/agence/billets/verification', data: {'code': code})).data);

  Future<List<Map<String, dynamic>>> rechercher(String q) async => versListe(
      (await client.dio.get('/api/agence/billets/recherche', queryParameters: {'q': q})).data);
}
