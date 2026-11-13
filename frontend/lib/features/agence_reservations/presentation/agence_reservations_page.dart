import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/utils/format.dart';
import '../data/agence_reservations_api.dart';

class AgenceReservationsPage extends StatefulWidget {
  const AgenceReservationsPage({super.key});
  @override
  State<AgenceReservationsPage> createState() => _State();
}

class _State extends State<AgenceReservationsPage> {
  final _api = AgenceReservationsApi(ApiClient());
  final List<Map<String, dynamic>> _items = [];
  String? _statut;
  int _page = 0;
  bool _derniere = true, _chargement = false;
  String? _erreur;

  @override
  void initState() {
    super.initState();
    _charger(true);
  }

  Future<void> _charger(bool debut) async {
    if (debut) {
      _page = 0;
      _items.clear();
    }
    setState(() {
      _chargement = true;
      _erreur = null;
    });
    try {
      final r = await _api.lister(statut: _statut, page: _page);
      if (!mounted) return;
      setState(() {
        _items.addAll((r['contenu'] as List).map((e) => Map<String, dynamic>.from(e)));
        _derniere = r['derniere'] == true;
        _chargement = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _erreur = messageErreur(e);
        _chargement = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Réservations')),
      body: Column(children: [
        Padding(
          padding: const EdgeInsets.all(12),
          child: DropdownButtonFormField<String?>(
            value: _statut,
            decoration: const InputDecoration(labelText: 'Statut', isDense: true, border: OutlineInputBorder()),
            items: const [
              DropdownMenuItem(value: null, child: Text('Tous')),
              DropdownMenuItem(value: 'EN_ATTENTE', child: Text('En attente')),
              DropdownMenuItem(value: 'PAYEE', child: Text('Payées')),
              DropdownMenuItem(value: 'EXPIREE', child: Text('Expirées')),
              DropdownMenuItem(value: 'ANNULEE', child: Text('Annulées')),
            ],
            onChanged: (v) {
              _statut = v;
              _charger(true);
            },
          ),
        ),
        if (_chargement) const LinearProgressIndicator(),
        if (_erreur != null) Padding(padding: const EdgeInsets.all(12), child: Text(_erreur!)),
        Expanded(
          child: _items.isEmpty && !_chargement
              ? const Center(child: Text('Aucune réservation.'))
              : ListView.builder(
                  itemCount: _items.length + (_derniere ? 0 : 1),
                  itemBuilder: (c, i) {
                    if (i == _items.length) {
                      return TextButton(
                          onPressed: _chargement
                              ? null
                              : () {
                                  _page++;
                                  _charger(false);
                                },
                          child: const Text('Charger plus'));
                    }
                    final r = _items[i];
                    final cl = r['client'] as Map?;
                    final b = r['billet'] as Map?;
                    return Card(
                      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                      child: ListTile(
                        title: Text('${cl?['prenom'] ?? ''} ${cl?['nom'] ?? ''} · ${cl?['telephone'] ?? ''}'),
                        subtitle: Text('Voyage ${r['voyageId']} · ${r['statut']}'
                            '${b == null ? '' : '\nBillet ${b['code']} (${b['statut']})'}'),
                        isThreeLine: b != null,
                        trailing: Text(formatFcfa((r['montant'] as num).toInt())),
                      ),
                    );
                  },
                ),
        ),
      ]),
    );
  }
}
