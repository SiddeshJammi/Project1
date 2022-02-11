import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Lion.
 * Liones age, move, eat Deers, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Lion extends Animal
{
    // Characteristics shared by all Lions (class variables).

    // The age at which a Lion can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a Lion can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single Deer. In effect, this is the
    // number of steps a Lion can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //Food value of elephant calf
    private static final int CALF_FOOD_VALUE = 9;
    //Maximum age of elephant the lion can eat
    private static final int ELEPHANT_MAX_AGE = 20;

    // Individual characteristics (instance fields).
    // The Lion's age.
    private int age;
    // The Lion's food level, which is increased by eating Deers.
    private int foodLevel;
    //The Lion's gender
    private boolean isMale;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEER_FOOD_VALUE);
            isMale = rand.nextBoolean();
        }
        else {
            age = 0;
            foodLevel = DEER_FOOD_VALUE;
            isMale = rand.nextBoolean();
        }
    }

    /**
     * This is what the Lion does most of the time: it hunts for
     * Deers. In the process, it might breed, die of hunger,
     * or die of old age.
     */
    public void act(List<Animal> newLions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLions);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Increase the age. This could result in the Lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this Lion more hungry. This could result in the Lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for Deers adjacent to the current location.
     * Only the first live Deer is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Deer) {
                Deer Deer = (Deer) animal;
                if(Deer.isAlive()) { 
                    Deer.setDead();
                    foodLevel = DEER_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Elephant) {
                Elephant Elephant = (Elephant) animal;
                if(Elephant.isAlive() && Elephant.getAge() <= ELEPHANT_MAX_AGE) { 
                    Elephant.setDead();
                    foodLevel = CALF_FOOD_VALUE;
                    return where;
                }

            }}
        return null;

    }

    /**
     * Check whether or not this Lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLiones A list to return newly born Liones.
     */
    private void giveBirth(List<Animal> newLiones)
    {
        // New Liones are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc);
            newLiones.add(young);
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
            if(animal instanceof Lion)
            {Lion lion = (Lion)animal;
                if (lion.getGender() != this.isMale && this.canBreed() && lion.canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY){
                    births = rand.nextInt(MAX_LITTER_SIZE)+1;
                }
            }
        }
        return births;
    }

    /**
     * A Lion can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
