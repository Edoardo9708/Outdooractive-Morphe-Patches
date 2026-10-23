# Piano di test — Outdooractive 3.23.20

Questa è una patch **diagnostica**, non uno sblocco delle funzioni premium.

## Prima dell'installazione

1. Usare l'app Outdooractive `3.23.20` con package `com.outdooractive.Outdooractive`.
2. Conservare un backup prima di sostituire un'app firmata con una versione patchata.
3. Applicare solo **Outdooractive - Diagnostica abbonamento** tramite Morphe Manager.
4. Se il manager non supporta l'XAPK o gli split APK, non forzare installazioni incompatibili.

## Verifica

1. Avviare l'app ed effettuare l'accesso al proprio account, se necessario.
2. Aprire la schermata profilo e verificare che l'app non vada in crash.
3. Aprire il profilo altimetrico di un percorso.
4. Aprire una schermata mappe e, se accessibile, BuddyBeacon.
5. Raccogliere le righe di Logcat con tag `OA-Membership`:

```bash
adb logcat -s OA-Membership:I '*:S'
```

Esempio indicativo: `isProUser = false` o `getLevel = 0`; i risultati effettivi dipendono dall'account.
La patch nuova scrive **una riga per combinazione distinta** di metodo, valore e chiamante. Crea uno stack temporaneo in memoria, ma non stampa più eccezioni; le ripetizioni sono ignorate fino al riavvio del processo.

## Criteri di successo

- Patch applicata senza errori.
- L'app si apre e le schermate abituali continuano a funzionare.
- Il tag `OA-Membership` compare nel log quando i getter sono chiamati.
- I valori restituiti dall'app non sono alterati.
- Le righe contenenti il chiamante diretto permettono di individuare i componenti che consultano `Membership`.

## Limiti

Non effettuare test con dati di terzi e non pubblicare account ID, token, percorsi privati o coordinate personali nei log.
Un log che non appare su una schermata non prova che la verifica non esista: il codice potrebbe usare altri campi, cache o controlli server.
