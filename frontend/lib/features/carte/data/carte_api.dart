import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class CarteApi {
  final ApiClient client;
  CarteApi(this.client);

  Future<List<Map<String, dynamic>>> agences() async =>
      versListe((await client.dio.get('/api/agences')).data);
}
