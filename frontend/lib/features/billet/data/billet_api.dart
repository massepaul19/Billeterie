import '../../../core/config/api_config.dart';
import '../../../core/network/api_client.dart';

class BilletInfo {
  final String code, statut;
  final String? passager;
  final Map<String, dynamic>? voyage;
  BilletInfo.fromJson(Map<String, dynamic> j)
      : code = j['code'].toString(),
        statut = j['statut'].toString(),
        passager = j['client'] == null
            ? null
            : '${j['client']['prenom'] ?? ''} ${j['client']['nom'] ?? ''}'.trim(),
        voyage = j['voyage'] is Map ? Map<String, dynamic>.from(j['voyage']) : null;
}

class BilletApi {
  final ApiClient client;
  BilletApi(this.client);

  Future<BilletInfo> obtenir(String code) async {
    final r = await client.dio.get('/api/billets/$code');
    return BilletInfo.fromJson(Map<String, dynamic>.from(r.data));
  }

  /// URL du PDF, à ouvrir dans le navigateur / lecteur PDF.
  String urlPdf(String code) => '${ApiConfig.baseUrl}/api/billets/$code/pdf';

  /// Retourne le montant remboursé (ou null si absent de la réponse).
  Future<int?> annuler(String code) async {
    final r = await client.dio.post('/api/billets/$code/annulation');
    final m = r.data is Map ? r.data['remboursement'] : null;
    return m is Map ? (m['montant'] as num?)?.toInt() : null;
  }
}
