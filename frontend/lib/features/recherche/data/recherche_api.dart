import '../../../core/network/api_client.dart';

/// Voyage tel que renvoyé par GET /api/voyages (lecture tolérante).
class Voyage {
  final int id;
  final String agence;
  final String? logo;
  final String departVille, departGare, arriveeVille, arriveeGare;
  final DateTime dateHeure;
  final int? dureeMin;
  final int prix;
  final String type;
  final bool clim, wifi;
  final String? image;

  Voyage.fromJson(Map<String, dynamic> j)
      : id = j['id'] as int,
        agence = (j['agence']?['nom'] ?? '').toString(),
        logo = j['agence']?['logo'] as String?,
        departVille = (j['depart']?['ville'] ?? '').toString(),
        departGare = (j['depart']?['gare'] ?? '').toString(),
        arriveeVille = (j['arrivee']?['ville'] ?? '').toString(),
        arriveeGare = (j['arrivee']?['gare'] ?? '').toString(),
        dateHeure = DateTime.parse(j['dateHeureDepart'] as String),
        dureeMin = j['dureeEstimeeMin'] as int?,
        prix = (j['prix'] as num).toInt(),
        type = (j['vehicule']?['type'] ?? '').toString(),
        clim = j['vehicule']?['clim'] == true,
        wifi = j['vehicule']?['wifi'] == true,
        image = j['vehicule']?['image'] as String?;
}

class RechercheApi {
  final ApiClient client;
  RechercheApi(this.client);

  Future<List<String>> villes([String q = '']) async {
    final r = await client.dio.get('/api/villes', queryParameters: {if (q.isNotEmpty) 'q': q});
    return (r.data as List).map((v) => v['nom'].toString()).toList();
  }

  Future<List<Voyage>> voyages({
    String? depart,
    String? destination,
    DateTime? date,
    String? type,
    bool clim = false,
    bool wifi = false,
    String? q,
  }) async {
    String d(DateTime x) => x.toIso8601String().substring(0, 10);
    final r = await client.dio.get('/api/voyages', queryParameters: {
      if (depart != null && depart.isNotEmpty) 'depart': depart,
      if (destination != null && destination.isNotEmpty) 'destination': destination,
      if (date != null) 'dateDebut': d(date),
      if (date != null) 'dateFin': d(date),
      if (type != null) 'type': type,
      if (clim) 'clim': true,
      if (wifi) 'wifi': true,
      if (q != null && q.trim().isNotEmpty) 'q': q.trim(),
      'taille': 50,
    });
    return (r.data['contenu'] as List)
        .map((e) => Voyage.fromJson(Map<String, dynamic>.from(e)))
        .toList();
  }
}
