import 'package:flutter/material.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/utils/format.dart';
import '../data/billet_api.dart';

/// Affichage du billet (QR), ouverture du PDF et annulation.
/// Sans [code], l'écran demande de saisir le code du billet.
class BilletPage extends StatefulWidget {
  final String? code;
  const BilletPage({super.key, this.code});

  @override
  State<BilletPage> createState() => _BilletPageState();
}

class _BilletPageState extends State<BilletPage> {
  final _api = BilletApi(ApiClient());
  final _saisie = TextEditingController();
  BilletInfo? _billet;
  bool _chargement = false;
  String? _erreur;

  @override
  void initState() {
    super.initState();
    if (widget.code != null) _charger(widget.code!);
  }

  @override
  void dispose() {
    _saisie.dispose();
    super.dispose();
  }

  Future<void> _charger(String code) async {
    setState(() {
      _chargement = true;
      _erreur = null;
    });
    try {
      final b = await _api.obtenir(code.trim().toUpperCase());
      if (!mounted) return;
      setState(() {
        _billet = b;
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

  Future<void> _ouvrirPdf() async {
    final ok = await launchUrl(Uri.parse(_api.urlPdf(_billet!.code)),
        mode: LaunchMode.externalApplication);
    if (!ok && mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Impossible d\'ouvrir le PDF.')));
    }
  }

  Future<void> _annuler() async {
    final confirme = await showDialog<bool>(
      context: context,
      builder: (c) => AlertDialog(
        title: const Text('Annuler ce billet ?'),
        content: const Text(
            'Possible jusqu\'à 2 h avant le départ. 5 % du prix sont retenus sur le remboursement.'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(c, false), child: const Text('Non')),
          FilledButton(onPressed: () => Navigator.pop(c, true), child: const Text('Oui, annuler')),
        ],
      ),
    );
    if (confirme != true) return;
    try {
      final montant = await _api.annuler(_billet!.code);
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(montant == null
              ? 'Billet annulé.'
              : 'Billet annulé. Remboursement : ${formatFcfa(montant)}')));
      _charger(_billet!.code);
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(messageErreur(e))));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Mon billet')),
      body: _chargement
          ? const Center(child: CircularProgressIndicator())
          : _billet == null
              ? _recherche()
              : _affichage(_billet!),
    );
  }

  Widget _recherche() => Padding(
        padding: const EdgeInsets.all(16),
        child: Column(children: [
          TextField(
            controller: _saisie,
            textCapitalization: TextCapitalization.characters,
            decoration: const InputDecoration(
                labelText: 'Code du billet', border: OutlineInputBorder()),
            onSubmitted: _charger,
          ),
          if (_erreur != null)
            Padding(
              padding: const EdgeInsets.only(top: 12),
              child: Text(_erreur!, style: TextStyle(color: Theme.of(context).colorScheme.error)),
            ),
          const SizedBox(height: 12),
          FilledButton(onPressed: () => _charger(_saisie.text), child: const Text('Afficher')),
        ]),
      );

  Widget _affichage(BilletInfo b) {
    final valide = b.statut == 'VALIDE';
    final v = b.voyage;
    final trajet = v == null ? null : '${v['depart']?['ville']} → ${v['arrivee']?['ville']}';
    final date = v?['dateHeureDepart'] == null
        ? null
        : formatDateHeure(DateTime.parse(v!['dateHeureDepart'] as String));
    return ListView(padding: const EdgeInsets.all(16), children: [
      Center(child: QrImageView(data: b.code, size: 220)),
      const SizedBox(height: 8),
      Center(
          child: SelectableText(b.code,
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold))),
      const SizedBox(height: 8),
      Center(
          child: Chip(
              label: Text(b.statut),
              backgroundColor: valide ? Colors.green.shade100 : Colors.red.shade100)),
      if (b.passager != null) ListTile(leading: const Icon(Icons.person), title: Text(b.passager!)),
      if (trajet != null) ListTile(leading: const Icon(Icons.route), title: Text(trajet)),
      if (date != null) ListTile(leading: const Icon(Icons.event), title: Text(date)),
      const SizedBox(height: 8),
      FilledButton.icon(
          onPressed: valide ? _ouvrirPdf : null,
          icon: const Icon(Icons.picture_as_pdf),
          label: const Text('Ouvrir le PDF')),
      const SizedBox(height: 8),
      OutlinedButton(onPressed: valide ? _annuler : null, child: const Text('Annuler le billet')),
    ]);
  }
}
