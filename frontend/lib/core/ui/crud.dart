import 'package:flutter/material.dart';
import '../network/api_erreur.dart';

void toast(BuildContext c, String m) =>
    ScaffoldMessenger.of(c).showSnackBar(SnackBar(content: Text(m)));

Future<bool> confirmer(BuildContext c, String texte) async =>
    (await showDialog<bool>(
      context: c,
      builder: (d) => AlertDialog(
        content: Text(texte),
        actions: [
          TextButton(onPressed: () => Navigator.pop(d, false), child: const Text('Non')),
          FilledButton(onPressed: () => Navigator.pop(d, true), child: const Text('Oui')),
        ],
      ),
    )) ==
    true;

/// Exécute un appel API : affiche l'erreur de l'API en cas d'échec, sinon [apres].
Future<void> executer(BuildContext c, Future<void> Function() f,
    {VoidCallback? apres, String? succes}) async {
  try {
    await f();
    if (!c.mounted) return;
    if (succes != null) toast(c, succes);
    apres?.call();
  } catch (e) {
    if (!c.mounted) return;
    toast(c, messageErreur(e));
  }
}

class Champ {
  final String cle, label, initial;
  final TextInputType? clavier;
  final bool requis, dateHeure, secret;
  final Map<String, String>? options;
  const Champ(this.cle, this.label,
      {this.initial = '',
      this.clavier,
      this.requis = true,
      this.options,
      this.dateHeure = false,
      this.secret = false});
}

Future<Map<String, String>?> ouvrirFormulaire(
        BuildContext context, String titre, List<Champ> champs) =>
    showDialog<Map<String, String>>(
        context: context, builder: (_) => _FormDialog(titre, champs));

class _FormDialog extends StatefulWidget {
  final String titre;
  final List<Champ> champs;
  const _FormDialog(this.titre, this.champs);
  @override
  State<_FormDialog> createState() => _FormDialogState();
}

class _FormDialogState extends State<_FormDialog> {
  final _form = GlobalKey<FormState>();
  late final Map<String, TextEditingController> _c = {
    for (final ch in widget.champs)
      ch.cle: TextEditingController(
          text: (ch.options != null && !ch.options!.containsKey(ch.initial)) ? '' : ch.initial)
  };

  @override
  void dispose() {
    for (final c in _c.values) {
      c.dispose();
    }
    super.dispose();
  }

  Future<void> _choisirDate(Champ ch) async {
    final now = DateTime.now();
    final d = await showDatePicker(
        context: context, initialDate: now, firstDate: now, lastDate: now.add(const Duration(days: 365)));
    if (d == null || !mounted) return;
    final t = await showTimePicker(context: context, initialTime: const TimeOfDay(hour: 8, minute: 0));
    if (t == null) return;
    final x = DateTime(d.year, d.month, d.day, t.hour, t.minute);
    String p(int n) => n.toString().padLeft(2, '0');
    _c[ch.cle]!.text = '${x.year}-${p(x.month)}-${p(x.day)}T${p(x.hour)}:${p(x.minute)}:00';
  }

  Widget _champ(Champ ch) {
    final ctrl = _c[ch.cle]!;
    final valide = ch.requis ? (String? v) => (v == null || v.trim().isEmpty) ? 'Obligatoire' : null : null;
    if (ch.options != null) {
      return DropdownButtonFormField<String>(
        value: ctrl.text.isEmpty ? null : ctrl.text,
        isExpanded: true,
        decoration: InputDecoration(labelText: ch.label),
        items: ch.options!.entries
            .map((e) => DropdownMenuItem(value: e.key, child: Text(e.value, overflow: TextOverflow.ellipsis)))
            .toList(),
        onChanged: (v) => ctrl.text = v ?? '',
        validator: valide,
      );
    }
    return TextFormField(
      controller: ctrl,
      keyboardType: ch.clavier,
      obscureText: ch.secret,
      readOnly: ch.dateHeure,
      onTap: ch.dateHeure ? () => _choisirDate(ch) : null,
      decoration: InputDecoration(
          labelText: ch.label, suffixIcon: ch.dateHeure ? const Icon(Icons.event) : null),
      validator: valide,
    );
  }

  @override
  Widget build(BuildContext context) => AlertDialog(
        title: Text(widget.titre),
        content: SizedBox(
          width: 420,
          child: Form(
            key: _form,
            child: SingleChildScrollView(
              child: Column(mainAxisSize: MainAxisSize.min, children: [
                for (final ch in widget.champs)
                  Padding(padding: const EdgeInsets.only(bottom: 8), child: _champ(ch)),
              ]),
            ),
          ),
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Annuler')),
          FilledButton(
            onPressed: () {
              if (_form.currentState!.validate()) {
                Navigator.pop(context, {for (final e in _c.entries) e.key: e.value.text.trim()});
              }
            },
            child: const Text('Enregistrer'),
          ),
        ],
      );
}

/// Écran « liste » générique : chargement, erreur, actualisation, bouton d'ajout.
class EcranListe<T> extends StatefulWidget {
  final String titre;
  final Future<List<T>> Function() charger;
  final Widget Function(BuildContext, T, VoidCallback recharger) item;
  final void Function(BuildContext, VoidCallback recharger)? ajouter;
  final String vide;
  const EcranListe(
      {super.key,
      required this.titre,
      required this.charger,
      required this.item,
      this.ajouter,
      this.vide = 'Aucun élément.'});

  @override
  State<EcranListe<T>> createState() => _EcranListeState<T>();
}

class _EcranListeState<T> extends State<EcranListe<T>> {
  late Future<List<T>> _f = widget.charger();
  void _rech() => setState(() => _f = widget.charger());

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: Text(widget.titre)),
        floatingActionButton: widget.ajouter == null
            ? null
            : FloatingActionButton(
                onPressed: () => widget.ajouter!(context, _rech), child: const Icon(Icons.add)),
        body: FutureBuilder<List<T>>(
          future: _f,
          builder: (c, s) {
            if (s.connectionState != ConnectionState.done) {
              return const Center(child: CircularProgressIndicator());
            }
            if (s.hasError) {
              return Center(
                  child: Column(mainAxisSize: MainAxisSize.min, children: [
                Text(messageErreur(s.error!), textAlign: TextAlign.center),
                TextButton(onPressed: _rech, child: const Text('Réessayer')),
              ]));
            }
            final l = s.data!;
            if (l.isEmpty) return Center(child: Text(widget.vide));
            return RefreshIndicator(
              onRefresh: () async {
                _rech();
                await _f;
              },
              child: ListView.builder(
                padding: const EdgeInsets.fromLTRB(8, 8, 8, 88),
                itemCount: l.length,
                itemBuilder: (c, i) => widget.item(c, l[i], _rech),
              ),
            );
          },
        ),
      );
}
