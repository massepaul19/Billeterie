import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class CommissionsApi {
  final ApiClient client;
  CommissionsApi(this.client);

  Future<List<Map<String, dynamic>>> lister(String mois) async => versListe(
      (await client.dio.get('/api/admin/commissions', queryParameters: {'mois': mois})).data);

  Future<double> taux() async =>
      ((await client.dio.get('/api/admin/parametres/commission')).data['taux'] as num).toDouble();

  Future<double> modifierTaux(double taux) async =>
      ((await client.dio.put('/api/admin/parametres/commission', data: {'taux': taux})).data['taux']
              as num)
          .toDouble();
}
