import '../../../core/network/api_client.dart';

class AgenceParametresApi {
  final ApiClient client;
  AgenceParametresApi(this.client);

  Future<List<int>> lireRappels() async {
    final r = await client.dio.get('/api/agence/parametres/rappels');
    return (r.data['delaisHeures'] as List).map((e) => (e as num).toInt()).toList();
  }

  Future<List<int>> modifierRappels(List<int> delais) async {
    final r = await client.dio.put('/api/agence/parametres/rappels', data: {'delaisHeures': delais});
    return (r.data['delaisHeures'] as List).map((e) => (e as num).toInt()).toList();
  }
}
