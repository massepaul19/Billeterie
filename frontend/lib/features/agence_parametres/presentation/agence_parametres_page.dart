import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/ui/crud.dart';
import '../data/agence_parametres_api.dart';

class AgenceParametresPage extends StatefulWidget {
  const AgenceParametresPage({super.key});
  @override
  State<AgenceParametresPage> createState() => _State();
}

class _State extends State<AgenceParametresPage> {
  final _api = AgenceParametresApi(ApiClient());
  final _delais = TextEditingController();
  bool _chargement = true;
  String? _erreur;

  @override
  void initState() {
    super.initState();
    _api.lireRappels().then((l) {
      if (!mounted) return;
      setState(() {
        _delais.text = l.join(', ');
        _chargement = false;
      });
    }).catchError((e) {
      if (!mounted) return;
      setState(() {
        _erreur = messageErreur(e as Object);
        _chargement = false;
      });
    });
  }

  @override
  void dispose() {
    _delais.dispose();
    super.dispose();
  }

  Future<void> _enregistrer() async {
    final l = RegExp(r'\d+').allMatches(_delais.text).map((m) => int.parse(m.group(0)!)).toList();
    try {
      final r = await _api.modifierRappels(l);
      if (!mounted) return;
      setState(() => _delais.text = r.join(', '));
      toast(context, 'Paramètres enregistrés (appliqués aux prochains billets).');
    } catch (e) {
      if (mounted) toast(context, messageErreur(e));
    }
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: const Text('Paramètres de rappel')),
        body: _chargement
            ? const Center(child: CircularProgressIndicator())
            : ListView(padding: const EdgeInsets.all(16), children: [
                if (_erreur != null) Text(_erreur!),
                const Text('Les clients reçoivent un e-mail de rappel avant le départ.'),
                const SizedBox(height: 12),
                TextField(
                  controller: _delais,
                  decoration: const InputDecoration(
                      labelText: 'Délais en heures avant le départ',
                      helperText: 'Ex. : 24, 2 (maximum 5 valeurs)',
                      border: OutlineInputBorder()),
                ),
                const SizedBox(height: 16),
                FilledButton(onPressed: _enregistrer, child: const Text('Enregistrer')),
              ]),
      );
}
