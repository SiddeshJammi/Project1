
import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Elephant.
 * Elephants age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Elephant extends Animal
{
    // Characteristics shared by all Elephants (class variables).

    // The age at which a Elephant can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a Elephant can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a Elephant breeding.
    private static final double BREEDING_PROBABILITY = 1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //Nutritional value of a plant for the Elephant
    private static final int PLANT_FOOD_VALUE = 8;

    // Individual characteristics (instance fields).

    // The Elephant's age.
    private int age;
    //Gender.
    private boolean isMale = true;
    // The Elephant's current food level (hunger).
    private int foodLevel;

    /**
     * Create a new Elephant. A Elephant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Elephant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Elephant(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        isMale = rand.nextBoolean();
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = PLANT_FOOD_VALUE;
    }

    /**
     * This is what the Elephant does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newElephants A list to return newly born Elephants.
     */
    public void act(List<Animal> newElephants)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newElephants);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Elephant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Elephant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newElephants A list to return newly born Elephants.
     */
    private void giveBirth(List<Animal> newElephants)
    {
        // New Elephants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Elephant young = new Elephant(false, field, loc);
            newElephants.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Elephant)
                {Elephant Elephant = (Elephant)animal;
                if (Elephant.getGender() != this.isMale && this.canBreed() && Elephant.canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY){
                    births = rand.nextInt(MAX_LITTER_SIZE)+1;
                }
            }
        }
        return births;
        
    }
    

    /**
     * A Elephant can breed if it has reached the breeding age.
     * @return true if the Elephant can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    public boolean getGender()
    {
        return isMale;
    }
    
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Plant) {
                Plant Plant = (Plant) animal;
                if(Plant.isAlive()) { 
                    Plant.setDead();
                    foodLevel = PLANT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    public int getAge(){
     return age;
    }
}