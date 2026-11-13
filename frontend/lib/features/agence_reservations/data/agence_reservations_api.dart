import '../../../core/network/api_client.dart';

class AgenceReservationsApi {
  final ApiClient client;
  AgenceReservationsApi(this.client);

  /// Page { contenu, page, taille, total, derniere }.
  Future<Map<String, dynamic>> lister({String? statut, int page = 0}) async =>
      Map<String, dynamic>.from((await client.dio.get('/api/agence/reservations',
              queryParameters: {if (statut != null) 'statut': statut, 'page': page, 'taille': 20}))
          .data);
}
