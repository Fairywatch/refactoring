# Étape 1 – Diagnostic de TransFret

Les numéros de ligne renvoient à l'état du code à l'étape 0 (commit `830634b`), dans
`transport-refactored/src/main/java/fr/efrei/transfret`. Le code est alors identique au legacy.

## 1. Les classes : rôle et dépendances

| Classe | Ce qu'elle fait | Dépend de |
|---|---|---|
| `IVehicule` | Contrat commun à tous les véhicules : capacité, chargement et **tous** les modes de déplacement | `Colis` |
| `Vehicule` | Base abstraite : immatriculation, capacité, liste des colis chargés, plein | `IVehicule`, `Colis`, console |
| `Camion` | Véhicule routier, avec ou sans certification ADR (matières dangereuses) | `Vehicule` |
| `CamionFrigorifique` | Camion qui n'accepte **que** des colis réfrigérés | `Camion`, code type `3` |
| `Train` | Véhicule ferroviaire électrique | `Vehicule` |
| `Bateau` | Navire, avec ou sans conteneurs réfrigérés | `Vehicule` |
| `Avion` | Avion cargo, pas de matières dangereuses | `Vehicule` |
| `Flotte` | Liste **statique** des 6 véhicules de la société | les 5 classes concrètes |
| `AffectationVehicule` | Choisit le premier véhicule libre et compatible avec les colis et le mode | `Flotte` (statique), `Colis`, classes concrètes (`instanceof`), chaînes de mode |
| `Colis` | Objet de données : dimensions, poids, valeur, type en `int` | — |
| `Client` | Objet de données : coordonnées, type en `String`, SIRET | — |
| `Expedition` | Objet de données (17 champs + setters) **et** mise en forme de la facture | `Client`, `Colis`, `Vehicule`, `Utils` |
| `CalculateurPrix` | Calcule le prix HT : tarif par mode, suppléments, urgence, remise client, assurance | `Colis`, chaînes de mode et de type client |
| `ServiceExpedition` | Crée, chiffre, planifie, démarre, livre et annule les expéditions, et notifie | `Database`, `CalculateurPrix`, `AffectationVehicule`, `EmailService`, `SmsService`, `Utils`, `Vehicule` |
| `ServiceClient` | Valide, enregistre et accueille un client | `Database`, `EmailService` |
| `Database` | Singleton qui stocke en mémoire (maps `static`) et journalise du faux SQL | `Expedition`, `Client` |
| `EmailService` | Envoi d'email (méthode `static`) et journal `static` | console |
| `SmsService` | Envoi de SMS et journal `static`, clé d'API en dur | console |
| `Utils` | Fourre-tout : arrondi, format de date et de prix, validation d'email, **table des distances** | — |
| `Main` | Scénario de démonstration | presque tout |

## 2. Défaut → emplacement → principe violé

### S – Responsabilité unique

| Défaut | Emplacement | Principe |
|---|---|---|
| Une entité métier fabrique sa propre facture (texte, mise en page, TVA) | `Expedition.genererFacture()` l. 52-82 | S |
| 30 lignes de validation avant la création proprement dite | `ServiceExpedition.creerExpedition` l. 17-51 | S |
| Le calcul des délais est dans le service | `ServiceExpedition.calculerDelai` l. 101-127 | S |
| Le service choisit et envoie lui-même les emails et les SMS | `ServiceExpedition.notifier` l. 204-209 | S |
| Le service génère les identifiants et calcule la TVA | `ServiceExpedition` l. 63-64, 76, 97 | S |
| Le service pilote le déplacement des véhicules | `ServiceExpedition.demarrerExpedition` l. 136-155 | S |
| Validation, enregistrement **et** email de bienvenue dans une seule méthode | `ServiceClient.creerClient` l. 5-26 | S |
| Une seule méthode fait l'agrégation des colis, le tarif de base, les suppléments, l'urgence, la remise et l'assurance | `CalculateurPrix.calculerPrixHT` l. 7-67 | S |
| Classe fourre-tout : formats, arrondi, validation **et** référentiel des distances | `Utils` | S |
| Stockage en mémoire et journalisation SQL mélangés | `Database` l. 29-55 | S |

### O – Ouvert/fermé

| Défaut | Emplacement | Principe |
|---|---|---|
| `if/else` sur le mode pour le tarif de base | `CalculateurPrix` l. 25-41 | O |
| `if/else` sur le type de client pour la remise | `CalculateurPrix` l. 50-58 | O |
| Un `if` par supplément (fragile, dangereux, réfrigéré, urgent) | `CalculateurPrix` l. 44-47 | O |
| `switch` sur le mode pour le délai | `ServiceExpedition.calculerDelai` l. 103-124 | O |
| `if/else` sur le mode pour faire partir le véhicule | `ServiceExpedition.demarrerExpedition` l. 142-149 | O |
| Règles par mode codées en dur (aérien et dangereux, réfrigéré, urgent et mer) | `ServiceExpedition.creerExpedition` l. 35-46 | O |
| Cascade de `if` sur le mode et d'`instanceof` | `AffectationVehicule.trouverVehicule` l. 20-52 | O |
| `switch` sur un entier pour le libellé | `Colis.getLibelleType` l. 33-46 | O |
| Un trajet = une branche `if` de plus | `Utils.getDistance` l. 33-49 | O |
| Comportement spécial selon le type de client | `ServiceExpedition.notifier` l. 206, `Expedition.genererFacture` l. 60 | O |

### L – Substitution de Liskov

| Défaut | Emplacement | Principe |
|---|---|---|
| `charger()` renforce la précondition : il refuse les colis non réfrigérés alors qu'un `Camion` les accepte | `CamionFrigorifique.charger` l. 10-16 | L |
| Conséquence : un `continue` « sinon ça plante au chargement » | `AffectationVehicule` l. 26-28 | L |
| `faireLePlein()` lève une exception alors que `Vehicule` le supporte | `Train.faireLePlein` l. 29-32 | L |
| Conséquence : un `catch` vide pour masquer l'exception | `ServiceExpedition.demarrerExpedition` l. 138-141 | L |
| `activerRefrigeration()` marche ou lève une exception selon l'état interne | `Bateau.activerRefrigeration` l. 24-29 | L |
| Downcast vers `Bateau` pour connaître une capacité | `AffectationVehicule` l. 43 | L |

### I – Ségrégation des interfaces

| Défaut | Emplacement | Principe |
|---|---|---|
| Interface « obèse » : `rouler`, `naviguer`, `voler`, `activerRefrigeration` et `faireLePlein` imposés à tous | `IVehicule` l. 22-32 | I |
| `UnsupportedOperationException` pour ce qui ne s'applique pas | `Camion` l. 16-26 · `Train` l. 13-23, 31 · `Bateau` l. 12-21 · `Avion` l. 14-24 | I |

### D – Inversion des dépendances

| Défaut | Emplacement | Principe |
|---|---|---|
| Singleton `Database.getInstance()` | `ServiceExpedition` l. 12, `ServiceClient` l. 23 | D |
| `new` de services en dur | `ServiceExpedition` l. 13-14 (`CalculateurPrix`, `AffectationVehicule`), l. 207 (`SmsService`) | D |
| Appel `static` à `EmailService.envoyer` | `ServiceExpedition` l. 205, `ServiceClient` l. 24 | D |
| Accès à la flotte `static` | `AffectationVehicule` l. 17 | D |
| Dépendance à la classe concrète `Vehicule` plutôt qu'à une abstraction | `Expedition` l. 21, `AffectationVehicule` l. 8 | D |
| Pour tester, l'adaptateur doit vider 5 champs `static` et réinitialiser la flotte | `NouveauSystemeAdapter.reinitialiser()` | D |

## 3. Autres défauts (hors SOLID)

| Défaut | Emplacement |
|---|---|
| **Nombres magiques** : `1.2`, `0.10`, `0.8`, `0.07`, `150`, `0.5`, `0.03`, `500`, `167`, `0.6`, `4.5`, `1.15`, `1.30`, `1.20`, `1.5`, `5000`, `0.85`, `0.90`, `0.80`, `0.02`, `15` | `CalculateurPrix` l. 26-63 |
| Nombres magiques : `70`, `18`, `9`, `1440`, `720`, `3000`, TVA `1.2`, frais `0.5`, `1.852`, `35000`, `4` | `ServiceExpedition` l. 76, 97, 105-121, 145-152, 186 |
| **Codes type** : mode, statut et type client en `String`, type de colis en `int` (`getType() == 3`) | `Client` l. 11, `Expedition` l. 15, 18, `Colis` l. 15, partout ailleurs |
| L'arrondi `Math.round(x * 100.0) / 100.0` est dupliqué alors que `Utils.arrondir` existe (et n'est jamais utilisé) | `CalculateurPrix` l. 66, `ServiceExpedition` l. 76, 97, 186 |
| Calcul du volume dupliqué | `Colis.getVolume` l. 29-31 et `Expedition.getVolumeTotal` l. 39 |
| Validation d'email écrite 3 fois, avec 3 règles différentes | `Utils.emailValide`, `ServiceClient` l. 10, `EmailService` l. 16 |
| Même boucle « y a-t-il un colis dangereux / réfrigéré ? » répétée 4 fois | `CalculateurPrix` l. 14-21, `AffectationVehicule` l. 12-16, `ServiceExpedition` l. 27-34 et l. 150-155 |
| **Code mort** | `Expedition.getPoidsTotal/getVolumeTotal/getValeurTotale/getHistorique` (historique jamais rempli), `Utils.arrondir/emailValide/formaterPrix`, `Database.toutesLesExpeditions`, `Client()` |
| Erreurs signalées par `System.out` + `return null` (ou `-1`) | `ServiceExpedition`, `ServiceClient`, `Utils.getDistance` |
| Dépassement de capacité ignoré en silence : le colis n'est pas chargé, mais l'expédition est créée quand même | `Vehicule.charger` l. 40-43 |
| Exception utilisée comme `goto` puis avalée | `EmailService.envoyer` l. 15-26 |
| `static` modifiables et publics | `Database.expeditions/clients`, `EmailService.journal`, `SmsService.journal`, `ServiceExpedition.compteur`, `Flotte.vehicules`, `Utils.FORMAT_DATE` (un `SimpleDateFormat` partagé n'est pas thread-safe) |
| **Sécurité** : SQL construit par concaténation (injection SQL) | `Database` l. 34, 39, 44, 48 |
| **Sécurité** : clé d'API en dur dans le code | `SmsService` l. 11 |
| Modèle anémique : `Expedition` construite par 15 setters, transitions de statut gérées par le service | `ServiceExpedition` l. 62-81, 135, 169, 188 |
| Identifiant aléatoire (`Random`), donc non reproductible | `ServiceExpedition` l. 64 |
| API de dates obsolète (`Date`, `Calendar`, `SimpleDateFormat`) | `Expedition`, `ServiceExpedition`, `Utils` |

## 4. Listes à conserver pour l'étape 8

### Ajouter le mode `FLUVIAL`

0,40 €/km + 0,02 €/kg, minimum 300 €, délai = ⌈distance / 360⌉ + 1 jour, réfrigéré interdit, pas
d'urgence, péniche `PEN-0001` de 2 500 t.

| # | Fichier | Modification |
|---|---|---|
| 1 | `CalculateurPrix.calculerPrixHT` | nouvelle branche `else if (mode.equals("FLUVIAL"))` avec le tarif et le minimum |
| 2 | `ServiceExpedition.calculerDelai` | nouveau `case "FLUVIAL"` |
| 3 | `ServiceExpedition.creerExpedition` | règle « pas d'urgence » (l. 43). La règle « réfrigéré interdit » marche déjà par hasard, puisque seuls ROUTE et MER passent le test l. 39 : c'est un comportement implicite |
| 4 | `ServiceExpedition.demarrerExpedition` | nouvelle branche pour faire partir la péniche (l. 142-149) |
| 5 | `AffectationVehicule.trouverVehicule` | nouvelle branche de mode + `instanceof Peniche` |
| 6 | `Peniche` (nouvelle classe) | étend `Vehicule` mais doit implémenter `rouler`, `voler` et `activerRefrigeration` : 3 `UnsupportedOperationException` de plus ; décider pour les matières dangereuses |
| 7 | `Flotte.initialiser` | ajouter `new Peniche("PEN-0001", 2500000)` |
| 8 | `Utils.getDistance` | nouvelle branche Paris ↔ Le Havre (365 km) |
| 9 | `Expedition` / `Database` / `Main` | rien, puisque le mode est une `String` libre (c'est justement le problème : aucune vérification) |

Soit **5 fichiers existants modifiés** (`CalculateurPrix`, `ServiceExpedition` à 3 endroits, `AffectationVehicule`, `Flotte`, `Utils`) et 1 nouveau. Le contrat (`transport-contrat`) ne connaît pas
`FLUVIAL`, et on n'a pas le droit de le modifier : le nouveau mode se testera par des tests unitaires
dans `transport-refactored`.

### Ajouter le supplément « hors gabarit » (×1,10 si un colis dépasse 200 cm sur une dimension)

| # | Fichier | Modification |
|---|---|---|
| 1 | `CalculateurPrix.calculerPrixHT` | détecter le hors-gabarit dans la boucle (l. 14-21) **et** ajouter un `if` dans les suppléments (l. 44-47), **avant** la remise, puisque le seuil de 5 000 € de la remise entreprise dépend du prix après suppléments |
| 2 | `Colis` | idéalement une méthode `estHorsGabarit()` (sinon on calcule dans `CalculateurPrix`) |

Soit **1 à 2 fichiers modifiés**, mais toujours dans la classe centrale qu'on ne voudrait plus toucher.

> ⚠️ **À anticiper :** trois tests de contrat utilisent des colis de plus de 200 cm (`FUTS_SOLVANT`
> 240 cm, `BOBINES_ACIER` et `CONTENEUR` 1 200 cm). Leurs prix attendus n'incluent pas ce supplément.
> Activer « hors gabarit » sans condition ferait passer `route_entreprise_dangereux_urgent…`,
> `rail_entreprise_au_dela_de_5000…` et `mer_premium_avec_assurance…` au rouge. Il faudra donc
> pouvoir le brancher sans l'imposer à l'assemblage utilisé par les tests de contrat.
