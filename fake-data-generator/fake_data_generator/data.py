from __future__ import annotations

from dataclasses import dataclass

@dataclass(frozen=True)
class LocationRow:
    code: str
    name: str
    country: str
    city: str


@dataclass(frozen=True)
class TransportationRow:
    origin_code: str
    destination_code: str
    transportation_type: str  
    operating_days: tuple[int, ...]


LOCATIONS: tuple[LocationRow, ...] = (
    # --- Turkey ---
    LocationRow("IST", "Istanbul Airport", "Turkey", "Istanbul"),
    LocationRow("SAW", "Sabiha Gökçen Airport", "Turkey", "Istanbul"),
    LocationRow("ESB", "Ankara Esenboga Airport", "Turkey", "Ankara"),
    LocationRow("TXM", "Taksim Square", "Turkey", "Istanbul"),
    LocationRow("CCIST", "Istanbul City Center", "Turkey", "Istanbul"),
    LocationRow("KBT", "Kabatas Pier", "Turkey", "Istanbul"),
    LocationRow("BST", "Besiktas Ferry Terminal", "Turkey", "Istanbul"),
    LocationRow("GLT", "Galata District", "Turkey", "Istanbul"),
    LocationRow("CCANK", "Ankara City Center", "Turkey", "Ankara"),
    # --- United Kingdom (London) ---
    LocationRow("LHR", "Heathrow Airport", "United Kingdom", "London"),
    LocationRow("LGW", "Gatwick Airport", "United Kingdom", "London"),
    LocationRow("STN", "Stansted Airport", "United Kingdom", "London"),
    LocationRow("WEM", "Wembley Stadium", "United Kingdom", "London"),
    LocationRow("LDNCC", "London City Center", "United Kingdom", "London"),
    # --- France (Paris) ---
    LocationRow("CDG", "Charles de Gaulle Airport", "France", "Paris"),
    LocationRow("ORY", "Orly Airport", "France", "Paris"),
    LocationRow("CCPAR", "Paris City Center", "France", "Paris"),
    # --- Italy (Rome) ---
    LocationRow("FCO", "Fiumicino Airport", "Italy", "Rome"),
    LocationRow("CCROM", "Rome City Center", "Italy", "Rome"),
    # --- Greece (Athens) ---
    LocationRow("ATH", "Athens International Airport", "Greece", "Athens"),
    LocationRow("CCATH", "Athens City Center", "Greece", "Athens"),
    # --- Germany (Munich) ---
    LocationRow("MUC", "Munich Airport", "Germany", "Munich"),
    LocationRow("CCMUC", "Munich City Center", "Germany", "Munich"),
    LocationRow("FRA", "Frankfurt Airport", "Germany", "Frankfurt"),
    # --- Spain ---
    LocationRow("MAD", "Madrid-Barajas Airport", "Spain", "Madrid"),
    LocationRow("BCN", "Barcelona-El Prat Airport", "Spain", "Barcelona"),
    LocationRow("CCMAD", "Madrid City Center", "Spain", "Madrid"),
    LocationRow("CCBCN", "Barcelona City Center", "Spain", "Barcelona"),
    # --- Other hubs (optional connections) ---
    LocationRow("AMS", "Schiphol Airport", "Netherlands", "Amsterdam"),
    LocationRow("JFK", "John F. Kennedy Airport", "United States", "New York"),
)

# Multiple options per leg => separate routes (case study)
TRANSPORTATIONS: tuple[TransportationRow, ...] = (
    # Istanbul area -> Istanbul airports
    TransportationRow("TXM", "IST", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("TXM", "IST", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("TXM", "IST", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("TXM", "SAW", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("TXM", "SAW", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("TXM", "SAW", "SUBWAY", (1, 3, 5, 7)),
    TransportationRow("CCIST", "IST", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCIST", "IST", "BUS", (1, 3, 5, 7)),
    TransportationRow("CCIST", "IST", "SUBWAY", (2, 4, 6)),
    TransportationRow("CCIST", "SAW", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCIST", "SAW", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("KBT", "TXM", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("KBT", "IST", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("KBT", "IST", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BST", "IST", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BST", "IST", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BST", "IST", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BST", "SAW", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BST", "SAW", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("GLT", "TXM", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("GLT", "TXM", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("GLT", "IST", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("GLT", "IST", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    # Ankara <-> airport & Istanbul
    TransportationRow("CCANK", "ESB", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCANK", "ESB", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ESB", "IST", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ESB", "SAW", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("IST", "ESB", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "ESB", "FLIGHT", (1, 3, 5, 7)),
    # Istanbul -> European capitals (flights)
    TransportationRow("IST", "LHR", "FLIGHT", (1, 3, 7)),
    TransportationRow("SAW", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("IST", "LGW", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "LGW", "FLIGHT", (2, 4, 6)),
    TransportationRow("IST", "STN", "FLIGHT", (1, 3, 5)),
    TransportationRow("SAW", "STN", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("IST", "CDG", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "CDG", "FLIGHT", (1, 3, 5, 7)),
    TransportationRow("IST", "ORY", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "ORY", "FLIGHT", (2, 4, 6)),
    TransportationRow("IST", "FCO", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "FCO", "FLIGHT", (1, 4, 7)),
    TransportationRow("IST", "ATH", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "ATH", "FLIGHT", (2, 5)),
    TransportationRow("IST", "MUC", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "MUC", "FLIGHT", (1, 3, 6)),
    TransportationRow("IST", "MAD", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "MAD", "FLIGHT", (3, 5, 7)),
    TransportationRow("IST", "BCN", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "BCN", "FLIGHT", (2, 4, 6)),
    TransportationRow("IST", "FRA", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "FRA", "FLIGHT", (1, 5)),
    TransportationRow("IST", "AMS", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("SAW", "AMS", "FLIGHT", (2, 4, 6)),
    TransportationRow("IST", "JFK", "FLIGHT", (1, 3, 5)),
    TransportationRow("SAW", "JFK", "FLIGHT", (2, 5)),
    # Ankara airport -> Europe
    TransportationRow("ESB", "MUC", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ESB", "FRA", "FLIGHT", (1, 3, 5, 7)),
    TransportationRow("ESB", "LHR", "FLIGHT", (2, 4, 6)),
    # Between major hubs (intra-Europe)
    TransportationRow("CDG", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ORY", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CDG", "FCO", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ORY", "FCO", "FLIGHT", (2, 4, 6)),
    TransportationRow("LHR", "FCO", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LGW", "FCO", "FLIGHT", (1, 3, 5, 7)),
    TransportationRow("CDG", "ATH", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LHR", "ATH", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("FCO", "ATH", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MUC", "FCO", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MUC", "CDG", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MUC", "LHR", "FLIGHT", (1, 3, 5, 7)),
    TransportationRow("FRA", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("FRA", "CDG", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MAD", "BCN", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MAD", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MAD", "CDG", "FLIGHT", (2, 4, 6)),
    TransportationRow("BCN", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BCN", "CDG", "FLIGHT", (1, 3, 5, 7)),
    TransportationRow("BCN", "FCO", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ATH", "IST", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ATH", "MUC", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("AMS", "LHR", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("AMS", "MUC", "FLIGHT", (1, 2, 3, 4, 5, 6, 7)),
    # London airport ground / links
    TransportationRow("LGW", "LHR", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("STN", "LHR", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LGW", "LDNCC", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LGW", "LDNCC", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("STN", "LDNCC", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("STN", "LDNCC", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LHR", "WEM", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LHR", "WEM", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LHR", "WEM", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LHR", "LDNCC", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LHR", "LDNCC", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LGW", "WEM", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LGW", "WEM", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("STN", "WEM", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("STN", "WEM", "UBER", (1, 3, 5, 7)),
    TransportationRow("LDNCC", "WEM", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LDNCC", "WEM", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("LDNCC", "WEM", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    # Paris airport <-> city
    TransportationRow("CDG", "CCPAR", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CDG", "CCPAR", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ORY", "CCPAR", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ORY", "CCPAR", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCPAR", "CDG", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCPAR", "CDG", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCPAR", "ORY", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    # Rome
    TransportationRow("FCO", "CCROM", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("FCO", "CCROM", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCROM", "FCO", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCROM", "FCO", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    # Athens
    TransportationRow("ATH", "CCATH", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("ATH", "CCATH", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCATH", "ATH", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCATH", "ATH", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    # Munich
    TransportationRow("MUC", "CCMUC", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MUC", "CCMUC", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCMUC", "MUC", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCMUC", "MUC", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    # Madrid & Barcelona
    TransportationRow("MAD", "CCMAD", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("MAD", "CCMAD", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCMAD", "MAD", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BCN", "CCBCN", "BUS", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("BCN", "CCBCN", "UBER", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCBCN", "BCN", "SUBWAY", (1, 2, 3, 4, 5, 6, 7)),
    TransportationRow("CCBCN", "BCN", "BUS", (1, 2, 3, 4, 5, 6, 7)),
)
