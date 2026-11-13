import 'package:flutter/material.dart';

import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/utils/format.dart';
import '../../paiement/presentation/paiement_page.dart';
import '../../recherche/data/recherche_api.dart';
import '../data/reservation_api.dart';

/// Formulaire de contact : la place est retenue 5 minutes après validation.
class ReservationPage extends StatefulWidget {
  final Voyage voyage;
  const ReservationPage({super.key, required this.voyage});

  @override
  State<ReservationPage> createState() => _ReservationPageState();
}

class _ReservationPageState extends State<ReservationPage> {
  final _form = GlobalKey<FormState>();
  final _nom = TextEditingController();
  final _prenom = TextEditingController();
  final _tel = TextEditingController();
  final _email = TextEditingController();
  bool _envoi = false;
  String? _erreur;

  @override
  void dispose() {
    for (final c in [_nom, _prenom, _tel, _email]) {
      c.dispose();
    }
    super.dispose();
  }

  Future<void> _valider() async {
    if (!_form.currentState!.validate()) return;
    setState(() {
      _envoi = true;
      _erreur = null;
    });
    try {
      final res = await ReservationApi(ApiClient()).creer(
        voyageId: widget.voyage.id,
        nom: _nom.text.trim(),
        prenom: _prenom.text.trim(),
        telephone: _tel.text.trim(),
        email: _email.text.trim(),
      );
      if (!mounted) return;
      Navigator.pushReplacement(context,
          MaterialPageRoute(builder: (_) => PaiementPage(reservation: res, voyage: widget.voyage)));
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _erreur = messageErreur(e); // PLACES_INDISPONIBLES, TROP_DE_RESERVATIONS...
        _envoi = false;
      });
    }
  }

  String? _requis(String? v) => (v == null || v.trim().isEmpty) ? 'Obligatoire' : null;

  Widget _champ(TextEditingController c, String label,
          {TextInputType? type, String? Function(String?)? valid}) =>
      Padding(
        padding: const EdgeInsets.only(bottom: 12),
        child: TextFormField(
          controller: c,
          keyboardType: type,
          validator: valid ?? _requis,
          decoration: InputDecoration(labelText: label, border: const OutlineInputBorder()),
        ),
      );

  @override
  Widget build(BuildContext context) {
    final v = widget.voyage;
    return Scaffold(
      appBar: AppBar(title: const Text('Réservation')),
      body: ListView(padding: const EdgeInsets.all(16), children: [
        Card(
          child: ListTile(
            title: Text('${v.departVille} → ${v.arriveeVille}'),
            subtitle: Text('${v.agence}\n${formatDateHeure(v.dateHeure)}'),
            isThreeLine: true,
            trailing: Text(formatFcfa(v.prix), style: const TextStyle(fontWeight: FontWeight.bold)),
          ),
        ),
        const SizedBox(height: 12),
        Form(
          key: _form,
          child: Column(children: [
            _champ(_nom, 'Nom'),
            _champ(_prenom, 'Prénom'),
            _champ(_tel, 'Téléphone', type: TextInputType.phone, valid: (s) {
              final d = (s ?? '').replaceAll(RegExp(r'\D'), '');
              return d.length < 9 ? 'Numéro invalide' : null;
            }),
            _champ(_email, 'E-mail (le billet y sera envoyé)', type: TextInputType.emailAddress,
                valid: (s) => (s == null || !s.contains('@')) ? 'E-mail invalide' : null),
          ]),
        ),
        if (_erreur != null)
          Padding(
            padding: const EdgeInsets.only(bottom: 12),
            child: Text(_erreur!, style: TextStyle(color: Theme.of(context).colorScheme.error)),
          ),
        const Text('Votre place sera retenue 5 minutes pour finaliser le paiement.'),
        const SizedBox(height: 12),
        FilledButton(
          onPressed: _envoi ? null : _valider,
          child: _envoi
              ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2))
              : const Text('Réserver et payer'),
        ),
      ]),
    );
  }
}
