# Étape 1 – Diagnostic

Les numéros de ligne correspondent au code de l'étape 0 (copie du legacy dans `transport-refactored`).

## Classes : rôle et dépendances

| Classe | Rôle | Dépend de |
|---|---|---|
| `IVehicule` | interface commune des véhicules (capacité, chargement, tous les modes de déplacement) | `Colis` |
| `Vehicule` | classe abstraite : immatriculation, capacité, colis chargés, plein | `IVehicule`, `Colis` |
| `Camion` | véhicule routier, avec ou sans ADR | `Vehicule` |
| `CamionFrigorifique` | camion qui ne prend que du réfrigéré | `Camion`, code type `3` |
| `Train` | train électrique | `Vehicule` |
| `Bateau` | navire, avec ou sans conteneurs frigo | `Vehicule` |
| `Avion` | avion cargo, pas de dangereux | `Vehicule` |
| `Flotte` | liste statique des 6 véhicules | les classes concrètes |
| `AffectationVehicule` | choisit le premier véhicule libre et compatible | `Flotte`, `Colis`, classes concrètes (`instanceof`), mode en `String` |
| `Colis` | données d'un colis, type en `int` | - |
| `Client` | données d'un client, type en `String` | - |
| `Expedition` | données d'une expédition (setters) et génération de la facture | `Client`, `Colis`, `Vehicule`, `Utils` |
| `CalculateurPrix` | prix HT : tarif par mode, suppléments, urgence, remise, assurance | `Colis`, mode et type client en `String` |
| `ServiceExpedition` | crée, chiffre, planifie, démarre, livre, annule et notifie | `Database`, `CalculateurPrix`, `AffectationVehicule`, `EmailService`, `SmsService`, `Utils` |
| `ServiceClient` | valide, enregistre et envoie l'email de bienvenue | `Database`, `EmailService` |
| `Database` | singleton, maps statiques, faux SQL | `Expedition`, `Client` |
| `EmailService` | envoi d'email en statique + journal statique | - |
| `SmsService` | envoi de SMS + journal statique, clé d'API en dur | - |
| `Utils` | arrondi, dates, prix, email, table des distances | - |
| `Main` | scénario de démo | presque tout |

## Défaut → emplacement → principe violé

### S – Responsabilité unique

| Défaut | Emplacement | Principe |
|---|---|---|
| L'entité fabrique sa propre facture | `Expedition.genererFacture()` l. 52-82 | S |
| 30 lignes de validation au début de la création | `ServiceExpedition.creerExpedition` l. 17-51 | S |
| Calcul des délais dans le service | `ServiceExpedition.calculerDelai` l. 101-127 | S |
| Le service envoie lui-même emails et SMS | `ServiceExpedition.notifier` l. 204-209 | S |
| Le service génère les ids et calcule la TVA | `ServiceExpedition` l. 63-64, 76, 97 | S |
| Le service fait rouler / naviguer / voler les véhicules | `ServiceExpedition.demarrerExpedition` l. 136-155 | S |
| Validation + enregistrement + email dans une méthode | `ServiceClient.creerClient` l. 5-26 | S |
| Une seule méthode pour tout le calcul du prix | `CalculateurPrix.calculerPrixHT` l. 7-67 | S |
| Classe fourre-tout | `Utils` | S |
| Stockage et logs SQL mélangés | `Database` l. 29-55 | S |

### O – Ouvert/fermé

| Défaut | Emplacement | Principe |
|---|---|---|
| `if/else` sur le mode pour le tarif | `CalculateurPrix` l. 25-41 | O |
| `if/else` sur le type de client pour la remise | `CalculateurPrix` l. 50-58 | O |
| Un `if` par supplément | `CalculateurPrix` l. 44-47 | O |
| `switch` sur le mode pour le délai | `ServiceExpedition.calculerDelai` l. 103-124 | O |
| `if/else` sur le mode au démarrage | `ServiceExpedition.demarrerExpedition` l. 142-149 | O |
| Règles par mode en dur (aérien/dangereux, réfrigéré, mer/urgent) | `ServiceExpedition.creerExpedition` l. 35-46 | O |
| Cascade de `if` sur le mode et d'`instanceof` | `AffectationVehicule.trouverVehicule` l. 20-52 | O |
| `switch` sur un entier pour le libellé | `Colis.getLibelleType` l. 33-46 | O |
| Un trajet = un `if` de plus | `Utils.getDistance` l. 33-49 | O |
| Cas particuliers selon le type de client | `ServiceExpedition.notifier` l. 206, `Expedition.genererFacture` l. 60 | O |

### L – Substitution de Liskov

| Défaut | Emplacement | Principe |
|---|---|---|
| `charger()` refuse les colis non réfrigérés alors qu'un `Camion` les accepte | `CamionFrigorifique.charger` l. 10-16 | L |
| Du coup un `continue` « sinon ça plante au chargement » | `AffectationVehicule` l. 26-28 | L |
| `faireLePlein()` lève une exception | `Train.faireLePlein` l. 29-32 | L |
| Du coup un `catch` vide | `ServiceExpedition.demarrerExpedition` l. 138-141 | L |
| `activerRefrigeration()` marche ou plante selon le bateau | `Bateau.activerRefrigeration` l. 24-29 | L |
| Cast en `Bateau` | `AffectationVehicule` l. 43 | L |

### I – Ségrégation des interfaces

| Défaut | Emplacement | Principe |
|---|---|---|
| `rouler`, `naviguer`, `voler`, `activerRefrigeration`, `faireLePlein` imposés à tous les véhicules | `IVehicule` l. 22-32 | I |
| `UnsupportedOperationException` un peu partout | `Camion` l. 16-26, `Train` l. 13-23 et 31, `Bateau` l. 12-21, `Avion` l. 14-24 | I |

### D – Inversion des dépendances

| Défaut | Emplacement | Principe |
|---|---|---|
| Singleton `Database.getInstance()` | `ServiceExpedition` l. 12, `ServiceClient` l. 23 | D |
| `new` de services en dur | `ServiceExpedition` l. 13-14, l. 207 | D |
| Appel statique `EmailService.envoyer` | `ServiceExpedition` l. 205, `ServiceClient` l. 24 | D |
| Flotte statique | `AffectationVehicule` l. 17 | D |
| Dépendance à la classe concrète `Vehicule` | `Expedition` l. 21, `AffectationVehicule` l. 8 | D |
| L'adaptateur doit vider les champs statiques pour tester | `NouveauSystemeAdapter.reinitialiser()` | D |

## Autres défauts

| Défaut | Emplacement |
|---|---|
| Nombres magiques (`1.2`, `0.10`, `0.8`, `0.07`, `150`, `0.5`, `0.03`, `500`, `167`, `0.6`, `4.5`, `1.15`, `1.30`, `1.20`, `1.5`, `5000`, `0.85`, `0.90`, `0.80`, `0.02`, `15`) | `CalculateurPrix` l. 26-63 |
| Nombres magiques (`70`, `18`, `9`, `1440`, `720`, `3000`, `1.2`, `0.5`, `1.852`, `35000`, `4`) | `ServiceExpedition` |
| Mode, statut et type client en `String`, type de colis en `int` | `Client`, `Expedition`, `Colis` et partout où ils sont utilisés |
| Arrondi `Math.round(x * 100.0) / 100.0` copié partout alors que `Utils.arrondir` existe | `CalculateurPrix` l. 66, `ServiceExpedition` l. 76, 97, 186 |
| Calcul du volume en double | `Colis.getVolume` et `Expedition.getVolumeTotal` |
| Validation d'email écrite 3 fois avec 3 règles différentes | `Utils.emailValide`, `ServiceClient` l. 10, `EmailService` l. 16 |
| Même boucle sur les colis (dangereux / réfrigéré) répétée 4 fois | `CalculateurPrix`, `AffectationVehicule`, `ServiceExpedition` (2 fois) |
| Code mort | `Expedition.getPoidsTotal/getVolumeTotal/getValeurTotale/getHistorique`, `Utils.arrondir/emailValide/formaterPrix`, `Database.toutesLesExpeditions`, `Client()` |
| Erreurs gérées avec `System.out` + `return null` ou `-1` | `ServiceExpedition`, `ServiceClient`, `Utils.getDistance` |
| Dépassement de capacité ignoré (le colis n'est pas chargé mais l'expédition est créée) | `Vehicule.charger` l. 40-43 |
| Exception lancée puis ignorée | `EmailService.envoyer` l. 15-26 |
| Champs statiques publics modifiables | `Database`, `EmailService`, `SmsService`, `ServiceExpedition.compteur`, `Flotte`, `Utils.FORMAT_DATE` |
| Injection SQL possible (requêtes concaténées) | `Database` l. 34, 39, 44, 48 |
| Clé d'API en dur | `SmsService` l. 11 |
| `Expedition` remplie avec des setters, statuts gérés par le service | `ServiceExpedition` |
| Id avec `Random` | `ServiceExpedition` l. 64 |
| `Date` / `Calendar` / `SimpleDateFormat` | `Expedition`, `ServiceExpedition`, `Utils` |

## Listes pour l'étape 8

### Mode `FLUVIAL`

| # | Fichier | Modification |
|---|---|---|
| 1 | `CalculateurPrix` | nouvelle branche pour le tarif et le minimum |
| 2 | `ServiceExpedition.calculerDelai` | nouveau `case` |
| 3 | `ServiceExpedition.creerExpedition` | règle « pas d'urgence » (le réfrigéré est déjà refusé puisque seuls ROUTE et MER passent le test l. 39) |
| 4 | `ServiceExpedition.demarrerExpedition` | nouvelle branche pour faire partir la péniche |
| 5 | `AffectationVehicule` | nouvelle branche + `instanceof Peniche` |
| 6 | `Peniche` (nouvelle) | doit implémenter `rouler`, `voler`, `activerRefrigeration` → 3 `UnsupportedOperationException` de plus |
| 7 | `Flotte.initialiser` | ajouter la péniche |
| 8 | `Utils.getDistance` | ajouter Paris - Le Havre |

Donc 5 fichiers existants à modifier + 1 nouveau. Le contrat ne connaît pas `FLUVIAL` et on ne peut pas le
modifier, donc il faudra le tester avec des tests unitaires.

### Supplément « hors gabarit »

| # | Fichier | Modification |
|---|---|---|
| 1 | `CalculateurPrix` | détecter le hors gabarit dans la boucle et ajouter un `if` dans les suppléments, avant la remise |
| 2 | `Colis` | éventuellement une méthode `estHorsGabarit()` |

Donc 1 à 2 fichiers, mais toujours dans `CalculateurPrix`.

Attention : quatre colis des tests de contrat dépassent 200 cm (`FUTS_SOLVANT`, `PRODUITS_LAITIERS`,
`BOBINES_ACIER`, `CONTENEUR`) et leurs prix attendus ne comptent pas ce supplément. Si on l'active pour
tout le monde, 6 tests de contrat cassent. Il faudra pouvoir l'activer ou non.
