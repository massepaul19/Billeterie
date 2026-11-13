import '../../../core/network/api_client.dart';

class Reservation {
  final int id;
  final int montant;
  final DateTime expireA;
  Reservation.fromJson(Map<String, dynamic> j)
      : id = j['id'] as int,
        montant = (j['montant'] as num).toInt(),
        expireA = DateTime.parse(j['expireA'] as String);
}

class ReservationApi {
  final ApiClient client;
  ReservationApi(this.client);

  Future<Reservation> creer({
    required int voyageId,
    required String nom,
    required String prenom,
    required String telephone,
    required String email,
  }) async {
    final r = await client.dio.post('/api/reservations', data: {
      'voyageId': voyageId,
      'nom': nom,
      'prenom': prenom,
      'telephone': telephone,
      'email': email,
    });
    return Reservation.fromJson(Map<String, dynamic>.from(r.data));
  }
}
