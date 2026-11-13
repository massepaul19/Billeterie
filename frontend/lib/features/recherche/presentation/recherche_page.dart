import 'dart:async';
import 'package:flutter/material.dart';

import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/utils/format.dart';
import '../../reservation/presentation/reservation_page.dart';
import '../data/recherche_api.dart';

/// Liste de voyages dès l'ouverture, filtrée en direct.
class RecherchePage extends StatefulWidget {
  const RecherchePage({super.key});

  @override
  State<RecherchePage> createState() => _RecherchePageState();
}

class _RecherchePageState extends State<RecherchePage> {
  final _api = RechercheApi(ApiClient());
  final _depart = TextEditingController();
  final _destination = TextEditingController();
  final _texte = TextEditingController();
  DateTime? _date;
  String? _type;
  bool _clim = false, _wifi = false;

  List<Voyage> _voyages = [];
  bool _chargement = true;
  String? _erreur;
  Timer? _delai;
  int _requete = 0;

  @override
  void initState() {
    super.initState();
    _charger();
  }

  @override
  void dispose() {
    _delai?.cancel();
    _depart.dispose();
    _destination.dispose();
    _texte.dispose();
    super.dispose();
  }

  void _planifier() {
    _delai?.cancel();
    _delai = Timer(const Duration(milliseconds: 350), _charger);
  }

  Future<void> _charger() async {
    final num = ++_requete;
    setState(() {
      _chargement = true;
      _erreur = null;
    });
    try {
      final v = await _api.voyages(
        depart: _depart.text.trim(),
        destination: _destination.text.trim(),
        date: _date,
        type: _type,
        clim: _clim,
        wifi: _wifi,
        q: _texte.text,
      );
      if (!mounted || num != _requete) return; // réponse périmée
      setState(() {
        _voyages = v;
        _chargement = false;
      });
    } catch (e) {
      if (!mounted || num != _requete) return;
      setState(() {
        _erreur = messageErreur(e);
        _chargement = false;
      });
    }
  }

  Future<void> _choisirDate() async {
    final now = DateTime.now();
    final d = await showDatePicker(
      context: context,
      initialDate: _date ?? now,
      firstDate: now,
      lastDate: now.add(const Duration(days: 120)),
    );
    if (d != null) {
      setState(() => _date = d);
      _charger();
    }
  }

  Widget _champ(TextEditingController c, String label) => Expanded(
        child: TextField(
          controller: c,
          onChanged: (_) => _planifier(),
          decoration: InputDecoration(labelText: label, isDense: true, border: const OutlineInputBorder()),
        ),
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Recherche de voyages')),
      body: Column(children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(12, 12, 12, 0),
          child: Column(children: [
            Row(children: [
              _champ(_depart, 'Départ'),
              const SizedBox(width: 8),
              _champ(_destination, 'Destination'),
            ]),
            const SizedBox(height: 8),
            Row(children: [_champ(_texte, 'Recherche libre (agence, quartier, clim…)')]),
            const SizedBox(height: 4),
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              child: Row(children: [
                ActionChip(
                  avatar: const Icon(Icons.event, size: 18),
                  label: Text(_date == null ? 'Date' : formatDateHeure(_date!).split(',').first),
                  onPressed: _choisirDate,
                ),
                if (_date != null)
                  IconButton(
                    icon: const Icon(Icons.close, size: 18),
                    onPressed: () {
                      setState(() => _date = null);
                      _charger();
                    },
                  ),
                const SizedBox(width: 6),
                FilterChip(
                  label: const Text('VIP'),
                  selected: _type == 'VIP',
                  onSelected: (s) {
                    setState(() => _type = s ? 'VIP' : null);
                    _charger();
                  },
                ),
                const SizedBox(width: 6),
                FilterChip(
                  label: const Text('Clim'),
                  selected: _clim,
                  onSelected: (s) {
                    setState(() => _clim = s);
                    _charger();
                  },
                ),
                const SizedBox(width: 6),
                FilterChip(
                  label: const Text('Wifi'),
                  selected: _wifi,
                  onSelected: (s) {
                    setState(() => _wifi = s);
                    _charger();
                  },
                ),
              ]),
            ),
          ]),
        ),
        if (_chargement) const LinearProgressIndicator(),
        Expanded(child: _corps()),
      ]),
    );
  }

  Widget _corps() {
    if (_erreur != null) {
      return Center(
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Text(_erreur!, textAlign: TextAlign.center),
          TextButton(onPressed: _charger, child: const Text('Réessayer')),
        ]),
      );
    }
    if (!_chargement && _voyages.isEmpty) {
      return const Center(child: Text('Aucun voyage ne correspond.'));
    }
    return RefreshIndicator(
      onRefresh: _charger,
      child: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: _voyages.length,
        itemBuilder: (_, i) => _carte(_voyages[i]),
      ),
    );
  }

  Widget _carte(Voyage v) {
    final equip = [v.type, if (v.clim) 'clim', if (v.wifi) 'wifi'].where((s) => s.isNotEmpty).join(' · ');
    return Card(
      child: ListTile(
        leading: v.logo == null
            ? const CircleAvatar(child: Icon(Icons.directions_bus))
            : CircleAvatar(backgroundImage: NetworkImage(v.logo!)),
        title: Text('${v.departVille} → ${v.arriveeVille}'),
        subtitle: Text('${v.agence}\n${formatDateHeure(v.dateHeure)}'
            '${v.dureeMin == null ? '' : ' · ${v.dureeMin! ~/ 60} h ${v.dureeMin! % 60} min'}\n$equip'),
        isThreeLine: true,
        trailing: Text(formatFcfa(v.prix), style: const TextStyle(fontWeight: FontWeight.bold)),
        onTap: () => Navigator.push(
            context, MaterialPageRoute(builder: (_) => ReservationPage(voyage: v))),
      ),
    );
  }
}
