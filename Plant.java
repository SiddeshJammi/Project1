import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Plant.
 * Plants age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Plant extends Animal
{
    // Characteristics shared by all Plants (class variables).

    // The age to which a Plant can live.
    private static final int MAX_AGE = 80;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //Age plant needs to be to be able to drop seeds
    private static final int BREEDING_AGE = 20;
    //Probability of plant dropping seeds
    private static final double BREEDING_PROBABILITY = 0.12;
    // Individual characteristics (instance fields).

    // The Plant's age.
    private int age;
    //Gender.
    private int waterLevel;
    // The Plant's current food level (hunger).
    private int sunlightLevel;

    /**
     * Create a new Plant. A Plant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        waterLevel = 25;
        sunlightLevel = 25;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            sunlightLevel = rand.nextInt(50);
            waterLevel = rand.nextInt(50);
        }    
    }

    /**
     * This is what the Plant does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newPlants A list to return newly born Plants.
     */
    public void act(List<Animal> newPlants)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newPlants);            
            // Try to move into a free location.
            
            }
        }
    

    /**
     * Increase the age.
     * This could result in the Plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlants A list to return newly born Plants.
     */
    private void giveBirth(List<Animal> newPlants)
    {
        // New Plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Plant(false, field, loc);
            newPlants.add(young);
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
        if (rand.nextDouble() <= BREEDING_PROBABILITY && this.age >= BREEDING_AGE){
            births = rand.nextInt(MAX_LITTER_SIZE)+1;
        }
        return births;
    }


    /**
     * A Plant can breed if it has reached the breeding age.
     * @return true if the Plant can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

}
