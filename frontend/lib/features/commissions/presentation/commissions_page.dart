import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/network/json.dart';
import '../../../core/ui/crud.dart';
import '../../../core/utils/format.dart';
import '../data/commissions_api.dart';

class CommissionsPage extends StatefulWidget {
  const CommissionsPage({super.key});
  @override
  State<CommissionsPage> createState() => _State();
}

class _State extends State<CommissionsPage> {
  final _api = CommissionsApi(ApiClient());
  DateTime _mois = DateTime(DateTime.now().year, DateTime.now().month);
  List<Map<String, dynamic>> _lignes = [];
  double? _taux;
  bool _chargement = true;
  String? _erreur;

  String get _cle => '${_mois.year}-${_mois.month.toString().padLeft(2, '0')}';

  @override
  void initState() {
    super.initState();
    _charger();
  }

  Future<void> _charger() async {
    setState(() {
      _chargement = true;
      _erreur = null;
    });
    try {
      final l = await _api.lister(_cle);
      final t = await _api.taux();
      if (!mounted) return;
      setState(() {
        _lignes = l;
        _taux = t;
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

  void _decaler(int n) {
    _mois = DateTime(_mois.year, _mois.month + n);
    _charger();
  }

  Future<void> _changerTaux() async {
    final r = await ouvrirFormulaire(context, 'Taux de commission', [
      Champ('taux', 'Taux en % (ex. 0,7)',
          initial: ((_taux ?? 0.007) * 100).toString(),
          clavier: const TextInputType.numberWithOptions(decimal: true)),
    ]);
    final pct = dbl(r?['taux']);
    if (pct == null || !mounted) return;
    await executer(context, () async => _taux = await _api.modifierTaux(pct / 100),
        apres: () => setState(() {}), succes: 'Taux modifié (appliqué aux prochains paiements).');
  }

  @override
  Widget build(BuildContext context) {
    final total = _lignes.fold<int>(0, (s, l) => s + (l['commission'] as num).toInt());
    return Scaffold(
      appBar: AppBar(title: const Text('Commissions')),
      body: Column(children: [
        Row(mainAxisAlignment: MainAxisAlignment.center, children: [
          IconButton(icon: const Icon(Icons.chevron_left), onPressed: () => _decaler(-1)),
          Text(_cle, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          IconButton(icon: const Icon(Icons.chevron_right), onPressed: () => _decaler(1)),
        ]),
        ListTile(
          title: Text('Taux actuel : ${_taux == null ? '…' : '${(_taux! * 100).toStringAsFixed(2)} %'}'),
          subtitle: Text('Total des commissions du mois : ${formatFcfa(total)}'),
          trailing: TextButton(onPressed: _changerTaux, child: const Text('Modifier')),
        ),
        if (_chargement) const LinearProgressIndicator(),
        if (_erreur != null) Padding(padding: const EdgeInsets.all(12), child: Text(_erreur!)),
        Expanded(
          child: _lignes.isEmpty && !_chargement
              ? const Center(child: Text('Aucune vente payée ce mois-ci.'))
              : ListView(children: [
                  for (final l in _lignes)
                    Card(
                      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                      child: ListTile(
                        title: Text('${l['agenceNom']}'),
                        subtitle: Text('Ventes : ${formatFcfa((l['totalVentes'] as num).toInt())} · ${l['statutFacturation']}'),
                        trailing: Text(formatFcfa((l['commission'] as num).toInt()),
                            style: const TextStyle(fontWeight: FontWeight.bold)),
                      ),
                    ),
                ]),
        ),
      ]),
    );
  }
}
