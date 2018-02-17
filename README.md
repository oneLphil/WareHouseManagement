# WareHouseManagement
javac Marshalling.java OrderHandler.java Pallet.java Picker.java PickRequest.java Simulator.java StockRoom.java Truck.java Warehouse.java
java project.Simulator
 

 Welcome to Warehouse Simulator 2017.
 
 You'll find detailed instructions below on how to operate your warehouse simulation.


 //////////////SETUP//////////////

 (-1) Once the files have pulled, please move TestFile1.txt, translation.csv, traversal_table.csv, initial.csv to the /src directory. These are the default files used by our software. Once this is done, run these commands from the src directory:

javac Marshalling.java OrderHandler.java Pallet.java Picker.java #PickRequest.java Simulator.java StockRoom.java Truck.java Warehouse.java
java project.Simulator

 (0) Run this from the src directory:

 javac src
 java project.Simulator <arg0>

 Replace <arg0> with any settings file you wish to read (NOT the orders.txt! See below)

 If you wish to have multiple warehouses in your simulation or wish to change the locations or names of the files you wish to reference, you may add modify the following line(s) in settings.txt as outlined below. Please ensure that all files specified are in the designated root folder (with the exception of <initial>), otherwise the Warehouse will not be generated.
 
 >> <orders> <initial> <traversal> <translation> <root>

 this line initializes a warehouse in the simulation. Warehouses are run in the order in which they are read in settings.txt. 
 <orders> - the file name of the orders for this warehouse (.txt)
 <initial> - the file name of the inital stock spreadsheet to be read (.csv)
 <traversal> - the traversal table for the warehouse (i.e. the warehouse layout) (.csv)
 <translation> - the translation table for incoming orders to the system (.csv)
 <root> - the location of the root folder in which the above files are located. Use '=' to check  from this program's root folder.

 (1) By default the racks of stock will be assumed to be full. If you wish to indicate that  specific stock is less than the default amount, please specify the stock at each position on the floor that is under the default stock limit in the <initial> spreadsheet. Note that a spreadsheet in the same format of <initial> will be output  will be output after each run; you may rename this file to that of <initial> if you wish to resume from where a previous simulation left off. Please organize each stock you wish to modify in <initial> as follows: "<zone>,<aisle>,<rack>,<level>,<stockOfItem>". Each row/line in the inital.csv will stand for one stock item on the warehouse floor. Please enter items in alphanumeric order. Skip to (2) if this has been completed or the simulation is to be run with full stock.
 
 (2) Confirm that <translation> reflect the products that are to be recieved as orders by the Warehouse. Please organize each order you wish to add or modify in <translation> as follows: "<colour>,<model>,<frontSKU>,<backSKU>". Each row/line in the <translation> will stand for product order. Please group colours together and enter all models in alphanumeric order.

 (3) Confirm that <traversal> provides the warehouse with the floor layout of the warehouse.
     a) Please organize each position you wish to add or modify in <traversal> as follows: "<zone>,<aisle>,<rack>,<level>". Each row/line in the <traversal> will stand for one item location on the warehouse floor and all rows should be entered in alphanumeric order.

 (4) Please enter the commands below in sequential order in each warehouse's respective <orders> file. Each event will be run sequentially by the simulation until all events have been run. The system will print out information to the user if the event succeeds or an event does not have the necessary conditions to be performed, including any instructions to be reviewed by employees on the floor and any actions taken by the system.

 ////////////COMMANDS/////////////
 
 >> Order <model> <colour>
 
 submits an order to the system via FAX. Currently, this program only supports minivan fascia. If the order combination does not exist in the translation table, the system will reject it with an error message.
 <make> - the model of the minivan
 <colour> - the minivan's colour
 
 >> Picker <name> ready
 
 creates a Picker (if the picker has not already been created in the system) and alerts the system that he/she is ready if the picker is not currently handling a Picking Request. If the picker is busy, the system will print an error message until the picker goes to marshaling and finishes their current job.
 <name> - the (new) picker's name
 >> Picker <name> pick <SKU>

 informs the stock room manager that the designated picker has picked the specified item, up to a maximum number of items specified by their Picking Request. If the SKU is not in the stock room registry, the bar code reader does not register the SKU for the picker. If the picker already has the maximum number of fascia that he or she can carry, this request is considered to be a 'discard' request; the picker will be asked by the system to discard the fascia.
 <name> - the picker's name
 <SKU> - the SKU number of the fascia a picker wishes to pick

 >> Picker <name> printout

 requests a printout of all location / sku that a Picker is supposed to pick. Useful in case a Picker's device powers out, for the purpose of confirming a particular SKU, or if a particular item is temporarially inaccessible.
 <name> - the picker's name

 >> Picker <name> to Marshaling
 
 sends the designated picker to the Marshaling station if the Picker. The items are then dropped off with the associated Picking Request identifier. Note that the sequencer on duty is in charge of ensuring that the pick request is fulfilled properly; if the picker has made a mistake or has not collected enough SKU, it is left in the hands of the sequencer to deal with the product.
 <name> - the name of the picker to send

 >> Sequencer <name> sequences

 confirms that the Sequencer has checked the current pick requests, and sequences picking request stock onto the active pallets in Marshalling in the order in which the orders received via FAX were initially submitted by the system. Any incorrect pick requests are discarded in their entirety.
 <name> - the sequencer's name

 >> Loader <name> load 
 
requests the specified loader to load the next layer of active pallets into the current available truck. If the truck is full, a spreadsheet is produced recording the truck's stock and a new truck takes its place.
<name> - the loader's name

>> Replenisher <name> replenish <zone> <aisle> <rack> <level>
 
sends a restock request for the SKU at the specified location, if it exists. If not, the replenish request is ignored.
<name> - the replenisher's name
<zone> - Capital letter designating the area in the warehouse
<aisle> - Integer designating the aisle of <zone>
<rack> - Integer designating the rack of <aisle>
<level> - Integer designating the level position on the <rack>

/////////////////////////////////

Note: Warehouse Simulator currently does not track the names of the sequencers, loaders and replenishers; your order.txt file can keep a record of this for you.

