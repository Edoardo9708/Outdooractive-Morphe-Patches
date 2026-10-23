# Outdooractive — patch diagnostiche per Morphe

Repository indipendente per analizzare le chiamate ai controlli di abbonamento di Outdooractive.

**Pacchetto Android:** `com.outdooractive.Outdooractive` · **Versione analizzata:** `3.23.20`.

**Stato:** sperimentale. Non dichiarare la patch funzionante prima di compilazione e test su telefono.

## Patch

| Nome in Morphe | Funzione |
| --- | --- |
| **Outdooractive - Diagnostica abbonamento** | Stampa in Logcat il risultato originale di `Membership.isProUser()` e `Membership.getLevel()` e lo stack dei chiamanti. |

Non modifica i valori, le richieste di rete, gli acquisti o le autorizzazioni server.

## Utilizzo

Dopo la pubblicazione di una release di test, aggiungi la [sorgente a Morphe Manager](https://morphe.software/add-source?github=Edoardo9708/Outdooractive-Morphe-Patches).
Per le versioni `dev`, abilita **Pre-release patches**; la versione dell'app compatibile è **3.23.20**.

Per leggere i log:

```bash
adb logcat -s OA-Membership:I '*:S'
```

Esempio (dipende dall'account):

```text
OA-Membership: isProUser = false
OA-Membership: getLevel = 0
```

I messaggi includono uno stack trace per individuare il chiamante: usare la patch soltanto per un test breve e oscurare eventuali dati sensibili prima di condividere il log. La diagnostica può rallentare i percorsi che chiamano spesso i getter.

## Sviluppo

- Sviluppo sul ramo [dev](https://github.com/Edoardo9708/Outdooractive-Morphe-Patches/tree/dev).
- `main` riservato alle modifiche verificate; il template conserva i workflow ufficiali.
- Per compilare: `./gradlew :patches:buildAndroid`; l'archivio generato è nella cartella `patches/build/libs`.
- Non modificare manualmente `patches-list.json`, `patches-bundle.json` o `CHANGELOG.md`: li aggiorna semantic-release.
- Non caricare APK proprietari nel repository.

Analisi statica: [docs/ANALYSIS.md](docs/ANALYSIS.md).

## Elenco automatico delle patch

<!-- PATCHES_START EXPANDED -->

L'elenco viene compilato dal workflow della prima release.

<!-- PATCHES_END -->

Progetto indipendente, non affiliato a Outdooractive o Morphe. Licenza: [GPLv3](LICENSE) e [NOTICE](NOTICE).
