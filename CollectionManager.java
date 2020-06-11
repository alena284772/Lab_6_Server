package ServerSide;


import Vehicle.*;
import Commands.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CollectionManager {

    private static String Type="LinkedHashMap";
    private static Logger logger=Logger.getLogger(CollectionManager.class.getName());

    private static Date date;
    public String answer;

    public LinkedHashMap<Integer, Vehicle> map;
    private File file_with_collection;

    Comparator<Vehicle> comparator = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle o1, Vehicle o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public CollectionManager(){
        this.map = new LinkedHashMap<Integer,Vehicle>();
        CollectionManager.date=new Date();
    }

    public void setFile_with_collection(String filename) {
        String substr = filename.substring(filename.length() - 3);
        if(substr.equals("xml")) {
            File filewithcollection = new File(filename);
            logger.info("Server checking file");
            if (filewithcollection.exists() == false) {
                logger.info("No matching file found");
            } else {
                this.file_with_collection = filewithcollection;
            }
        }else{
            logger.info("Wrong file format");
        }
    }

    public static Date getDate() {
        return date;
    }


    public static String getType() {
        return Type;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Type of collection: "+CollectionManager.getType()+" Creation date: "+CollectionManager.getDate()+" Map size: "+map.size();
    }

    public LinkedHashMap<Integer, Vehicle> getMap() {
        return map;
    }

    public void setMap(LinkedHashMap<Integer, Vehicle> map) {
        this.map = map;
    }

    public String getAnswer() {
        return answer;
    }
    public void clear(Command command){
        map.clear();
        command.setAnswer("Map cleared");
    }

    public void remove_greater(Vehicle vehicle, Command command){
        int a= (int) map.entrySet().stream().filter(Vehicle-> Vehicle.getValue().compareTo(vehicle)>0).count();
        List<Integer> k =new ArrayList<>();
        map.entrySet().stream().filter(Vehicle-> Vehicle.getValue().compareTo(vehicle)>0).forEach(Vehicle->k.add(Vehicle.getKey()));
        if(a>0) {
            k.stream().forEach(integer -> map.remove(integer));
            command.setAnswer("Objects exceeding this have been deleted.");
        }else{
            command.setAnswer("Elements exceeding the given one do not exist.");
        }

    }

    public void remove_key(Integer Key, Command command){
       int a= (int) map.entrySet().stream().filter(Vehicle -> Vehicle.getKey().equals(Key)).count();
       Integer[] k = new Integer[1];
        map.entrySet().stream().filter(Vehicle -> Vehicle.getKey().equals(Key)).forEach(Vehicle -> k[0] =Vehicle.getKey());
if(a>0) {
    map.remove(k[0]);
    command.setAnswer("Element was deleted");
}else{
    command.setAnswer("Element was not deleted, because it doesn't exist");
}

    }


    public void replace_if_greater(Integer Key, Vehicle vehicle, Command command){
        int a= (int) map.entrySet().stream().filter(Vehicle -> Vehicle.getKey().equals(Key)).filter(Vehicle-> Vehicle.getValue().compareTo(vehicle)>0).count();
        map.entrySet().stream().filter(Vehicle -> Vehicle.getKey().equals(Key)).filter(Vehicle-> Vehicle.getValue().compareTo(vehicle)>0).forEach(Vehicle -> map.remove(Vehicle.getKey()));
        if(a>0){
            command.setAnswer("Element value has been changed");
        }else{
            command.setAnswer("Element value has NOT been changed");
        }

    }


    public void remove_any_by_number_of_wheels(Long number, Command command){
       int a= (int) map.entrySet().stream().filter(Vehicle -> Vehicle.getValue().getNumberOfWheels().equals(number)).count();
       Integer[] l=new Integer[1];
        map.entrySet().stream().filter(Vehicle -> Vehicle.getValue().getNumberOfWheels().equals(number)).limit(1).forEach(Vehicle -> l[0]=Vehicle.getKey());
        if(a>0){
            map.remove(l[0]);
        command.setAnswer("Object is deleted");} else {
            command.setAnswer("The object was not deleted, because an object with this number of wheels does not exist.");
        }


    }


    public void count_less_than_engine_power(Float power, Command command){
        int count= (int) map.entrySet().stream().filter(Vehicle-> Vehicle.getValue().getEnginePower()<power).count();
        command.setAnswer("Number of objects is " + count);
    }

    public void update(Long ID, Vehicle vehicle, Command command){
        vehicle.setId(ID);
        int a= (int) map.entrySet().stream().filter(Vehicle-> map.get(Vehicle.getKey()).getId()==ID).count();
        map.entrySet().stream().filter(Vehicle-> map.get(Vehicle.getKey()).getId()==ID).forEach(Vehicle->map.put(Vehicle.getKey(),vehicle));

    if(a>0){
        command.setAnswer("Object with this id is update");
       }else {
        command.setAnswer("The item was not updated because the item with this ID does not exist.");
       }

    }


    public void insert(Integer Key, Vehicle vehicle, Command command) throws NumberFormatException {
        vehicle.setId(Id.makeID());
        map.put(Key, vehicle);
        command.setAnswer("Object was insert");

    }


    public void show(Command command){

        orderByValue(map,comparator);
        ArrayList<String> Str = new ArrayList<>();
        map.entrySet().stream().forEach(map->Str.add("Key="+map.getKey()+"Value="+map.getValue().toString()));
        command.setAnswer(String.valueOf(Str));


    }
    static <K, V> void orderByValue(
            LinkedHashMap<K, V> m, final Comparator<? super V> c) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(m.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> lhs, Map.Entry<K, V> rhs) {
                return c.compare(lhs.getValue(), rhs.getValue());
            }
        });

        m.clear();
        for(Map.Entry<K, V> e : entries) {
            m.put(e.getKey(), e.getValue());
        }
    }



    public void read_file(String filename, Command command){
        setFile_with_collection(filename);
        if(this.file_with_collection!=null) {
            ArrayList<Long> list = new ArrayList<>();
            list.add(Id.getID());
            try {
                Scanner Scanner = new Scanner(this.file_with_collection);
                Scanner.nextLine();
                while (Scanner.next().equals("<Key>")) {
                    String key = Scanner.next();
                    Scanner.nextLine();
                    Scanner.nextLine();
                    Scanner.skip("   <Id> ");
                    long id = Scanner.nextLong();
                    list.add(id);
                    Scanner.nextLine();
                    Scanner.skip("   <Name> ");
                    String name = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <Coordinate_x> ");
                    String x = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <Coordinate_y> ");
                    String y = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <creationDate> ");
                    String creationDate = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <enginePower> ");
                    String Engine = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <numberOfWheels> ");
                    String number = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <type> ");
                    String Type = Scanner.next();
                    Scanner.nextLine();
                    Scanner.skip("   <fuelType> ");
                    String type = Scanner.next();
                    Scanner.nextLine();
                    Scanner.nextLine();

                    try {
                        Vehicle vehicle = new Vehicle(id, name, Double.valueOf(x), Long.valueOf(y), creationDate, Float.valueOf(Engine), Long.valueOf(number), Type, type);
                        map.put(Integer.valueOf(key), vehicle);
                    } catch (VehicleTypeException e) {
                        e.printStackTrace();
                    } catch (FuelTypeException e) {
                        e.printStackTrace();
                    }
                }

            } catch (FileNotFoundException e) {
                e.getMessage();
            }
            Id.setID(Collections.max(list));
            command.setAnswer("Collection was read from file");
        }else{
            command.setAnswer("No matching file found or wrong file format");
        }


    }

    public void save(Command command){
        if (this.file_with_collection.canWrite()) {
            try (PrintWriter printWriter = new PrintWriter(this.file_with_collection)) {

                printWriter.printf("<%s> %n", "Collection");
                Set set = map.entrySet();
                Iterator<LinkedHashMap<Integer, Vehicle>> iterator = set.iterator();
                while (iterator.hasNext()) {
                    Map.Entry item = (Map.Entry) iterator.next();
                    printWriter.printf(" <Key> %s </Key> %n", item.getKey());
                    printWriter.printf("  <%s> %n", "Vehicle");
                    printWriter.printf("   <Id> %s </Id> %n", map.get(item.getKey()).getId());
                    printWriter.printf("   <Name> %s </Name> %n", map.get(item.getKey()).getName());
                    printWriter.printf("   <Coordinate_x> %s </Coordinates_x> %n", map.get(item.getKey()).c.getX());
                    printWriter.printf("   <Coordinate_y> %s </Coordinates_y> %n", map.get(item.getKey()).c.getY());
                    printWriter.printf("   <creationDate> %s </creationDate> %n", map.get(item.getKey()).getCreationDate());
                    printWriter.printf("   <enginePower> %s </enginePower> %n", map.get(item.getKey()).getEnginePower());
                    printWriter.printf("   <numberOfWheels> %s </numberOfWheels> %n", map.get(item.getKey()).getNumberOfWheels());
                    printWriter.printf("   <type> %s </type> %n", map.get(item.getKey()).getType());
                    printWriter.printf("   <fuelType> %s </fuelType> %n", map.get(item.getKey()).getFuelType());
                    printWriter.printf("  <%s> %n", "/Vehicle");
                }
                printWriter.printf("<%s>", "/Collection");
                printWriter.close();
                command.setAnswer("The collection was successfully saved to a file. The server stops working with this client.");

            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            System.out.println("Access Error: You cannot write values to this file. The server stops working with this client.");
        }

    }

}


