# ExamenCivique Android

Application Android de préparation à l'**examen civique** français (formation civique obligatoire dans le cadre du Contrat d'Intégration Républicaine).

---

## Fonctionnalités

| Onglet | Description |
|--------|-------------|
| **Réviser** | Parcourez les questions par thème, questions faibles ou nouvelles. Réponses avec explications immédiates. |
| **Examen blanc** | Simulation officielle : 40 questions (28 connaissance + 12 situation), chronomètre 45 min, seuil 32/40. |
| **Progrès** | Vue globale de la précision, questions maîtrisées, historique des examens. |

### Format officiel
- **40 questions** : 28 connaissance + 12 situation
- **45 minutes** maximum
- **32/40** (80 %) pour réussir
- Deux niveaux : **CSP** et **CR**

---

## Stack technique

- **Kotlin** + **Jetpack Compose** + **Material 3**
- `kotlinx.serialization` pour le parsing JSON
- `SharedPreferences` pour la persistance (léger, pas de BDD SQLite nécessaire)
- Architecture : Repository + ViewModel, navigation via `navigation-compose`
- Min SDK 26 (Android 8.0), Target SDK 35

---

## Ouvrir dans Android Studio

1. Ouvrir Android Studio → **Open** → sélectionner `ExamenCivique-android/`
2. Attendre la synchronisation Gradle
3. Lancer sur émulateur ou appareil physique

---

## Synchronisation du fichier questions.json (iOS ↔ Android)

Les deux apps (iOS et Android) partagent **le même format JSON** (`questions.json`).

### Stratégie de mise à jour

```
ExamenCivique-android/
├── shared/
│   └── questions.json          ← SOURCE DE VÉRITÉ
├── app/src/main/assets/
│   └── questions.json          ← copie pour Android (auto-sync via Gradle)

ExamenCivique-iOS/
├── ExamenCivique/Resources/
│   └── questions.json          ← copie pour iOS (auto-sync via Gradle)
```

**Pour mettre à jour la banque de questions :**

1. Modifier **`shared/questions.json`** (source unique)
2. Exécuter la tâche Gradle de synchronisation :
   ```bash
   cd ExamenCivique-android
   ./gradlew syncQuestions
   ```
   Cela copie automatiquement vers :
   - `app/src/main/assets/questions.json` (Android)
   - `../ExamenCivique-iOS/ExamenCivique/Resources/questions.json` (iOS)

### Pourquoi JSON ?

| Critère | JSON | SQLite | CSV | Protobuf |
|---------|------|--------|-----|----------|
| Lisible par humain | ✅ | ❌ | ✅ | ❌ |
| Natif iOS (Codable) | ✅ | ❌ | ❌ | ❌ |
| Natif Android (kotlinx.serialization) | ✅ | ❌ | ❌ | ❌ |
| Git diff lisible | ✅ | ❌ | ✅ | ❌ |
| Pas de dépendance externe | ✅ | ❌ | ✅ | ❌ |
| Structure hiérarchique | ✅ | ❌ | ❌ | ✅ |

**JSON est le format idéal** pour synchroniser les deux plateformes : natif sur iOS (Swift `Codable`) et Android (kotlinx.serialization), lisible, et facile à versionner avec Git.

---

## Format d'une question

```json
{
  "id": "pv_001",
  "category": "principes_valeurs",
  "levels": ["CSP", "CR"],
  "type": "connaissance",
  "question": "Quelle est la devise de la République française ?",
  "options": ["Option A", "Option B", "Option C", "Option D"],
  "correct_index": 1,
  "explanation": "Explication détaillée."
}
```

| Champ | Valeurs possibles |
|-------|-------------------|
| `category` | `principes_valeurs` · `institutions` · `droits_devoirs` · `histoire_geo_culture` · `vie_en_france` |
| `levels` | `["CSP"]` · `["CR"]` · `["CSP","CR"]` |
| `type` | `connaissance` · `situation` |
| `correct_index` | `0` à `3` |

---

## Architecture

```
MainActivity
└── AppNavigation (NavHost + BottomBar)
    ├── StudyScreen → StudyCardScreen (StudyViewModel)
    ├── ExamSetupScreen → ExamScreen (ExamViewModel) → ResultsScreen
    └── ProgressScreen (ProgressViewModel)

Data layer:
├── QuestionRepository  — charge questions.json depuis assets
└── ProgressRepository  — persiste réponses + examens (SharedPreferences)
```

---

## Sources officielles

- [formation-civique.interieur.gouv.fr](https://formation-civique.interieur.gouv.fr)
- [Questions CSP](https://formation-civique.interieur.gouv.fr/examen-civique/liste-officielle-des-questions-de-connaissance-csp)
- [Questions CR](https://formation-civique.interieur.gouv.fr/examen-civique/liste-officielle-des-questions-de-connaissance-cr)
