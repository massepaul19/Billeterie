import 'package:flutter_test/flutter_test.dart';
import 'package:billetterie_app/main.dart';

void main() {
  testWidgets('Affiche l’accueil de développement', (tester) async {
    await tester.pumpWidget(const BilletterieApp());

    expect(
      find.text('Billetterie - accueil de développement'),
      findsOneWidget,
    );

    expect(find.text('Connexion (Front C)'), findsOneWidget);
  });
}
