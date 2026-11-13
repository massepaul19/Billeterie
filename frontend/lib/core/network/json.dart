List<Map<String, dynamic>> versListe(dynamic d) =>
    (d as List).map((e) => Map<String, dynamic>.from(e as Map)).toList();

int? ent(String? s) => (s == null || s.trim().isEmpty) ? null : int.tryParse(s.trim());
double? dbl(String? s) =>
    (s == null || s.trim().isEmpty) ? null : double.tryParse(s.trim().replaceAll(',', '.'));
String? videNull(String? s) => (s == null || s.trim().isEmpty) ? null : s.trim();
