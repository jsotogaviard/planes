use dbgraph flights into 'flights.dex'
LOAD NODES '${dataset}/airplanes.csv'  
COLUMNS aircraftType,
		capacityBusiness,
		capacityEconomy,
		weightCapacity
INTO Airplanes
FIELDS TERMINATED ';'
from 1
mode rows