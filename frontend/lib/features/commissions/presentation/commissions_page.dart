import 'package:flutter/material.dart';

/// Commissions (super admin) - propriétaire : Front C (Abdias) - Sprint 2
/// À coder : Relevé mensuel des commissions par agence et modification du taux.
/// Voir TODO.md de ce dossier et docs/API.md.
class CommissionsPage extends StatelessWidget {
  const CommissionsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Commissions (super admin)")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Relevé mensuel des commissions par agence et modification du taux.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}