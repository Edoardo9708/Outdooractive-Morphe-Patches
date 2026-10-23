# Analisi statica di Outdooractive 3.23.20

## Modello Membership

In `classes4.dex` è presente `Lcom/outdooractive/sdk/objects/ooi/Membership;`.
I metodi di lettura sono:

| Metodo | Firma DEX | Istruzioni originali |
| --- | --- | --- |
| `isProUser` | `()Z` | `iget-boolean v0, p0`, `return v0` |
| `getLevel` | `()I` | `iget v0, p0`, `return v0` |

Entrambi usano un registro (il parametro `p0` coincide inizialmente con `v0`); le istruzioni di ritorno sono conservate e ricevono lo stesso valore originale.

## Provenienza e uso dei dati (analisi statica)

```text
Backend /sync/user/profile ─> User ─> User.getMembership()
Backend /user/profile/membership ─> MembershipAnswer.getMembership()
                                             │
                                    Membership (tipo)
                                             │
                             isProUser() e getLevel()
                                             │
                          Interfaccia, profilo, mappe
```

I due percorsi usano lo stesso tipo Java, non necessariamente la stessa istanza.
La verifica degli accessi premium può avvenire ulteriormente lato backend.

## Patch diagnostica

Il logger stampa valore e traccia di chiamata con tag `OA-Membership`, senza cambiare stato o return dei getter.
Il fingerprint è ristretto esattamente al bytecode della versione 3.23.20 e fallisce se le istruzioni non coincidono.

## Da verificare su dispositivo

- Compilazione della patch Morphe con il template aggiornato.
- Corretta applicazione alla versione supportata.
- Quali schermate chiamano i due getter.
- Frequenza, eventuali ritardi e comportamento delle mappe.
- Quali funzionalità richiedono autorizzazioni remote.

Non archiviare credenziali, cookie, posizioni o risposte API nei log.
