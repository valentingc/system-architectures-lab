Main Methode einfach ausführen, dann startet die CLI Eingabemöglichkeit.
Read/Write Seiten sind mit einem Thread getrennt (siehe write/repository/EventStore.java).
Es kann sein, dass sehr selten die CLI einmal hängen sollte (nach einiger Zeit) -> einfach Enter drücken und die Operation erneut ausführen (sorry!).