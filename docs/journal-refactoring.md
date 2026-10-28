# Journal du refactoring

Un commit par étape, `mvn install` avant chaque commit. Les 68 tests de contrat passent à chaque étape.

## Étapes

- **Étape 0** : copie du legacy dans `fr.efrei.transfret`, adaptateur branché.
- **Étape 1** : diagnostic, voir [diagnostic.md](diagnostic.md).
- **Étape 2** : constantes nommées, enums `ModeTransport`, `StatutExpedition`, `TypeClient`, `TypeMarchandise`
  (le libellé est dans l'enum), `Montants.arrondir` et `Montants.ttc` pour les arrondis.
- **Étape 3** : `GenerateurFacture`, `EstimateurDelai`, `ValidateurExpedition`, `ValidateurClient`,
  `Notificateur`, `Chargement` (poids, volume, valeur, types des colis) et `TableDistances`. Les changements de
  statut sont dans `Expedition`. `Utils` et le code mort sont supprimés.
- **Étape 4** : interfaces `ExpeditionRepository`, `ClientRepository`, `CanalEmail`, `CanalSms` avec des
  implémentations en mémoire / console. Injection par constructeur. `TransFret` construit tout, et
  `reinitialiser()` fait juste `new TransFret()`.
- **Étape 5** : Strategy pour le tarif (`TarifTransport`), la remise (`PolitiqueRemise`) et le délai
  (`DelaiTransport`).
- **Étape 6** : Decorator pour les suppléments, l'urgence, la remise et l'assurance.
- **Étape 7** : `IVehicule` supprimée. Chaque véhicule dit s'il peut prendre un chargement
  (`peutPrendreEnCharge`) et comment il part (`partir`). Plus d'`instanceof`.
- **Étape 8** : mode `FLUVIAL` et supplément hors gabarit.
- **Relecture avec les slides du cours** : il restait des code smells, corrigés après coup.
  - Longues listes de paramètres : objet `Commande` (colis, villes, mode, urgent, assurance) pour
    `creerExpedition`, `calculerDevis`, `DemandeTarif` et `Expedition`. `ServiceClient.inscrire(Client)` au lieu de
    6 paramètres.
  - `orElse(null)` dans `ServiceExpedition` remplacés par des `Optional`.
  - Code mort : setters de `Client` et `Colis` supprimés (les deux sont immuables maintenant).
  - Code dupliqué : `TarifRoute`, `TarifRail`, `TarifMer` et `TarifFluvial` héritent de `TarifAuKmEtAuKg`.
  - `LocalDate.now()` en dur : une `Clock` est injectée dans `ServiceExpedition` et `GenerateurFacture`.
  - Les véhicules ne testent plus le type des colis : chacun donne la liste des marchandises qu'il refuse.

## Enum ou Map pour choisir la stratégie (étape 5)

Si l'enum porte sa stratégie (`ROUTE(new TarifRoute())`), c'est simple mais le modèle dépend des classes de
tarif, et changer de grille oblige à modifier l'enum.

Avec une `Map<ModeTransport, TarifTransport>` injectée, l'enum reste indépendant et c'est l'assemblage qui
choisit. Le risque c'est d'oublier un mode.

On a choisi la `Map`. Pour l'oubli, `Strategies.pourChaque` vérifie au démarrage qu'il y a une stratégie pour
chaque valeur de l'enum. Ça a servi à l'étape 8 : tant que le tarif et le délai fluviaux n'étaient pas
ajoutés, `TransFret` ne démarrait pas.

Les règles qui sont juste des données sont restées dans les enums : marchandises interdites et urgence
autorisée pour chaque mode, SIRET obligatoire et SMS pour chaque type de client. Ça enlève les derniers `if`
sur un mode ou un type de client.

Pour les décorateurs, la chaîne est construite une seule fois dans `TransFret`, et chaque décorateur regarde
lui-même s'il s'applique (colis fragile, urgent, etc.). L'ordre : suppléments, urgence, remise, assurance.

## Changements de comportement

Ces cas ne sont pas couverts par les tests de contrat :

- Un envoi par route avec des colis réfrigérés et non réfrigérés faisait planter le legacy au chargement. Maintenant
  il est refusé.
- Les ids sont `EXP-1`, `EXP-2`... au lieu d'avoir une partie aléatoire.
- Plus de faux SQL ni de clé d'API dans le code.
- Les messages d'erreur dans la console ne sont plus exactement les mêmes.

## Étape 8

Nouvelles classes : `TarifFluvial`, `DelaiFluvial`, `Peniche`, `SupplementHorsGabarit`.

Fichiers existants modifiés :
- `ModeTransport` : ajout de `FLUVIAL` (réfrigéré interdit, pas d'urgence) ;
- `TransFret` : tarif, délai, péniche `PEN-0001`, trajet Paris - Le Havre (365 km) et supplément hors gabarit.

Combien de fichiers existants modifiés ? 2, contre 5 pour `FLUVIAL` et 1 à 2 pour le hors gabarit à
l'étape 1. Les services, validateurs, véhicules et décorateurs existants n'ont pas bougé. Il a aussi fallu
corriger `DelaiTransportTest`, qui listait les 4 modes à la main.

Les 68 tests passent-ils toujours ? Oui, mais parce que le supplément hors gabarit n'est pas activé par
défaut. Quatre colis des tests de contrat font plus de 200 cm, et en activant le supplément 6 tests de contrat
cassent (on a testé). C'est un changement de règle métier, donc il faudrait mettre à jour le contrat, ce
qu'on n'a pas le droit de faire. En attendant, `new TransFret()` garde la grille actuelle et
`new TransFret(true)` ajoute le supplément.

`FLUVIAL` n'existe pas dans l'enum du contrat, donc il est testé uniquement par des tests unitaires.

Tests des nouvelles classes : `ExtensionTest` (tarif et minimum fluvial, délai, péniche, seuil des 200 cm,
expédition Paris - Le Havre complète, refus réfrigéré et urgent, supplément activé ou non).

## Grille d'auto-évaluation

| Critère | ✔ |
|---|---|
| 68 tests de contrat verts | oui (+ 41 tests unitaires) |
| Aucun nombre magique | oui, il reste juste les données de la flotte et des distances dans `TransFret` |
| Aucun `if` / `switch` sur un mode, un type de client ou un type de marchandise | oui, les véhicules déclarent juste les marchandises qu'ils refusent |
| Strategy et Decorator, chacun avec son test | oui |
| Aucun `instanceof`, aucune `UnsupportedOperationException` | oui |
| Aucun `static` modifiable, aucun singleton | oui |
| L'adaptateur ne fait que traduire | oui |
| FLUVIAL et hors gabarit = nouvelles classes + assemblage | oui, plus une constante dans l'enum |
