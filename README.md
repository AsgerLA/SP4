
Funktionelle krav til Hotelbookingsystem
Dette system understøtter booking af hotelsuiter og tilknyttede tjenester gennem et menudrevet brugerinterface. De funktionelle krav er opdelt efter nøglefunktionalitet og brugerscenarier.

1. Menu- og valgmuligheder
Systemet skal tilbyde en menuinterface via Menu-klassen, som viser forskellige Option-objekter, som brugeren kan vælge.

- Brugeren skal kunne tilføje nye valgmuligheder til menuen og angive en specifik exit-valgmulighed (f.eks. “Afslut”).

- Når brugeren vælger en valgmulighed, skal run()-metoden for det valgte Option-objekt kaldes. F.eks. OptionBook, der håndterer bookinger.

2. Booking af suiter
Systemet skal tillade en kunde at booke en eller flere Suit-objekter via Booking-klassen.

- Under bookingen skal brugeren angive:

- Navn

- Betalingsmetode (Online, Fysisk kort, Fysisk kontant)

- Antal personer

- Start- og slutdato for ophold

- Systemet skal validere, at den valgte suite ikke allerede er booket, og at der er tilstrækkelig kapacitet i de tilknyttede værelser.

3. Kundeinformation og validering
En kunde skal kunne oprettes med relevante oplysninger som navn, betalingsmetode, og ønskede suiter.

- Systemet skal kontrollere, at Room-kapaciteten matcher antallet af personer.

4. Suiter og tilvalg
Hver suite har attributter såsom pris, værelsesliste, ekstra services (som morgenmad), balkonstatus og type (standard eller luksus).

- Brugeren skal kunne se detaljer om en suite og vælge ekstra services under bookingprocessen.

5. Databasefunktionalitet
Systemet skal gemme og hente suiteinformation fra en database (Database eller DatabaseSQLlite).

- Det skal være muligt at tilføje og fjerne suiter dynamisk.

6. Logging og fejlrapportering
Systemet skal understøtte logging gennem Log-klassen.

- Det skal være muligt at logge advarsler og fejl via Log.warn() og Log.error(), og outputstrømmen kan konfigureres.
