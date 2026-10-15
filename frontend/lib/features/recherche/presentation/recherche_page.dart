import 'package:flutter/material.dart';

/// Recherche de voyages - propriétaire : Front D (Bouba) - Sprint 1
/// À coder : Liste de voyages dès l'ouverture, filtrée en direct (villes, date, heure, type, clim, wifi, texte libre). À livrer pour J5.
/// Voir TODO.md de ce dossier et docs/API.md.
class RecherchePage extends StatelessWidget {
  const RecherchePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Recherche de voyages")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste de voyages dès l'ouverture, filtrée en direct (villes, date, heure, type, clim, wifi, texte libre). À livrer pour J5.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}