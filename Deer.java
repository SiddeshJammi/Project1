import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Deer.
 * Deers age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Deer extends Animal
{
    // Characteristics shared by all Deers (class variables).

    // The age at which a Deer can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Deer can live.
    private static final int MAX_AGE = 180;
    // The likelihood of a Deer breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //Nutritional value of a plant for the Deer
    private static final int PLANT_FOOD_VALUE = 8;

    // Individual characteristics (instance fields).

    // The Deer's age.
    private int age;
    //Gender.
    private boolean isMale = true;
    // The deer's current food level (hunger).
    private int foodLevel;

    /**
     * Create a new Deer. A Deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
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
     * This is what the Deer does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newDeers A list to return newly born Deers.
     */
    public void act(List<Animal> newDeers)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newDeers);            
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
     * This could result in the Deer's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Deer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDeers A list to return newly born Deers.
     */
    private void giveBirth(List<Animal> newDeers)
    {
        // New Deers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Deer young = new Deer(false, field, loc);
            newDeers.add(young);
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
            if(animal instanceof Deer)
                {Deer deer = (Deer)animal;
                if (deer.getGender() != this.isMale && this.canBreed() && deer.canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY){
                    births = rand.nextInt(MAX_LITTER_SIZE)+1;
                }
            }
        }
        return births;
        
    }
    

    /**
     * A Deer can breed if it has reached the breeding age.
     * @return true if the Deer can breed, false otherwise.
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
}
