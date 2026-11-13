import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../data/agence_verification_api.dart';

class AgenceVerificationPage extends StatefulWidget {
  const AgenceVerificationPage({super.key});
  @override
  State<AgenceVerificationPage> createState() => _State();
}

class _State extends State<AgenceVerificationPage> {
  final _api = AgenceVerificationApi(ApiClient());
  final _code = TextEditingController();
  final _q = TextEditingController();
  Map<String, dynamic>? _resultat;
  List<Map<String, dynamic>> _trouves = [];
  String? _erreur;
  bool _chargement = false;

  @override
  void dispose() {
    _code.dispose();
    _q.dispose();
    super.dispose();
  }

  Future<void> _verifier() async {
    final code = _code.text.trim().toUpperCase();
    if (code.isEmpty) return;
    setState(() {
      _chargement = true;
      _erreur = null;
      _resultat = null;
    });
    try {
      final r = await _api.verifier(code);
      if (!mounted) return;
      setState(() {
        _resultat = r;
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

  Future<void> _chercher() async {
    try {
      final l = await _api.rechercher(_q.text.trim());
      if (mounted) setState(() => _trouves = l);
    } catch (e) {
      if (mounted) setState(() => _erreur = messageErreur(e));
    }
  }

  @override
  Widget build(BuildContext context) {
    final r = _resultat;
    final accepte = r?['accepte'] == true;
    final cl = r?['client'] as Map?;
    final v = r?['voyage'] as Map?;
    return Scaffold(
      appBar: AppBar(title: const Text('Vérification des billets')),
      body: ListView(padding: const EdgeInsets.all(16), children: [
        TextField(
          controller: _code,
          textCapitalization: TextCapitalization.characters,
          onSubmitted: (_) => _verifier(),
          decoration: const InputDecoration(
              labelText: 'Code du billet (ou contenu du QR)', border: OutlineInputBorder()),
        ),
        const SizedBox(height: 8),
        FilledButton.icon(
            onPressed: _chargement ? null : _verifier,
            icon: const Icon(Icons.qr_code_scanner),
            label: const Text('Vérifier et valider l\'embarquement')),
        if (_erreur != null)
          Padding(
              padding: const EdgeInsets.only(top: 12),
              child: Text(_erreur!, style: TextStyle(color: Theme.of(context).colorScheme.error))),
        if (r != null)
          Card(
            color: accepte ? Colors.green.shade100 : Colors.red.shade100,
            margin: const EdgeInsets.only(top: 12),
            child: ListTile(
              leading: Icon(accepte ? Icons.check_circle : Icons.cancel, size: 36),
              title: Text(accepte ? 'Embarquement autorisé' : 'Refusé : billet ${r['statut']}'),
              subtitle: Text('${cl?['prenom'] ?? ''} ${cl?['nom'] ?? ''} · ${cl?['telephone'] ?? ''}\n'
                  '${v?['depart']?['ville'] ?? ''} → ${v?['arrivee']?['ville'] ?? ''}'),
              isThreeLine: true,
            ),
          ),
        const Divider(height: 32),
        TextField(
          controller: _q,
          onSubmitted: (_) => _chercher(),
          decoration: InputDecoration(
              labelText: 'Chercher par nom ou téléphone',
              border: const OutlineInputBorder(),
              suffixIcon: IconButton(icon: const Icon(Icons.search), onPressed: _chercher)),
        ),
        for (final b in _trouves)
          ListTile(
            title: Text('${b['prenom']} ${b['nom']} · ${b['telephone']}'),
            subtitle: Text('${b['code']} · ${b['statut']}'),
            onTap: () => setState(() => _code.text = '${b['code']}'),
          ),
      ]),
    );
  }
}
