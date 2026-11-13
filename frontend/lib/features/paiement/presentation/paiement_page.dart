import 'dart:async';
import 'package:flutter/material.dart';

import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/utils/format.dart';
import '../../billet/presentation/billet_page.dart';
import '../../recherche/data/recherche_api.dart';
import '../../reservation/data/reservation_api.dart';
import '../data/paiement_api.dart';

/// Choix MTN / Orange, numéro, paiement simulé, puis succès ou échec.
class PaiementPage extends StatefulWidget {
  final Reservation reservation;
  final Voyage voyage;
  const PaiementPage({super.key, required this.reservation, required this.voyage});

  @override
  State<PaiementPage> createState() => _PaiementPageState();
}

class _PaiementPageState extends State<PaiementPage> {
  final _form = GlobalKey<FormState>();
  final _tel = TextEditingController();
  String _operateur = 'MTN';
  bool _envoi = false;
  String? _erreur;
  Timer? _minuteur;
  Duration _reste = Duration.zero;

  bool get _expire => _reste == Duration.zero;

  @override
  void initState() {
    super.initState();
    _tick();
    _minuteur = Timer.periodic(const Duration(seconds: 1), (_) => _tick());
  }

  void _tick() {
    final d = widget.reservation.expireA.difference(DateTime.now());
    setState(() => _reste = d.isNegative ? Duration.zero : d);
  }

  @override
  void dispose() {
    _minuteur?.cancel();
    _tel.dispose();
    super.dispose();
  }

  Future<void> _payer() async {
    if (!_form.currentState!.validate()) return;
    setState(() {
      _envoi = true;
      _erreur = null;
    });
    try {
      final r = await PaiementApi(ApiClient()).payer(
        reservationId: widget.reservation.id,
        operateur: _operateur,
        telephonePaiement: _tel.text.trim(),
      );
      if (!mounted) return;
      if (r.reussi) {
        Navigator.pushReplacement(
            context, MaterialPageRoute(builder: (_) => BilletPage(code: r.codeBillet)));
      } else {
        setState(() {
          _erreur = 'Le paiement a échoué. Votre réservation est annulée : recommencez.';
          _envoi = false;
        });
      }
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _erreur = messageErreur(e); // ex. RESERVATION_EXPIREE
        _envoi = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final mm = _reste.inMinutes.toString().padLeft(2, '0');
    final ss = (_reste.inSeconds % 60).toString().padLeft(2, '0');
    return Scaffold(
      appBar: AppBar(title: const Text('Paiement')),
      body: ListView(padding: const EdgeInsets.all(16), children: [
        Card(
          color: _expire ? Theme.of(context).colorScheme.errorContainer : null,
          child: ListTile(
            leading: const Icon(Icons.timer),
            title: Text(_expire ? 'Réservation expirée' : 'Place retenue : $mm:$ss'),
            subtitle: Text('${widget.voyage.departVille} → ${widget.voyage.arriveeVille}'),
            trailing: Text(formatFcfa(widget.reservation.montant),
                style: const TextStyle(fontWeight: FontWeight.bold)),
          ),
        ),
        const SizedBox(height: 12),
        SegmentedButton<String>(
          segments: const [
            ButtonSegment(value: 'MTN', label: Text('MTN MoMo')),
            ButtonSegment(value: 'ORANGE', label: Text('Orange Money')),
          ],
          selected: {_operateur},
          onSelectionChanged: (s) => setState(() => _operateur = s.first),
        ),
        const SizedBox(height: 12),
        Form(
          key: _form,
          child: TextFormField(
            controller: _tel,
            keyboardType: TextInputType.phone,
            decoration: const InputDecoration(
                labelText: 'Numéro Mobile Money', border: OutlineInputBorder()),
            validator: (s) =>
                (s ?? '').replaceAll(RegExp(r'\D'), '').length < 9 ? 'Numéro invalide' : null,
          ),
        ),
        if (_erreur != null)
          Padding(
            padding: const EdgeInsets.only(top: 12),
            child: Text(_erreur!, style: TextStyle(color: Theme.of(context).colorScheme.error)),
          ),
        const SizedBox(height: 16),
        if (_expire)
          OutlinedButton(
              onPressed: () => Navigator.pop(context), child: const Text('Retour aux voyages'))
        else
          FilledButton(
            onPressed: _envoi ? null : _payer,
            child: _envoi
                ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
                : Text('Payer ${formatFcfa(widget.reservation.montant)}'),
          ),
        const SizedBox(height: 8),
        const Text('Paiement simulé : aucun argent réel n\'est débité.',
            textAlign: TextAlign.center),
      ]),
    );
  }
}
