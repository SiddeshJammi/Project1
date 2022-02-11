import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Boar.
 * Boars age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Boar extends Animal
{
    // Characteristics shared by all Boars (class variables).

    // The age at which a Boar can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Boar can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a Boar breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //Nutritional value of a plant for the Boar
    private static final int PLANT_FOOD_VALUE = 8;

    // Individual characteristics (instance fields).

    // The Boar's age.
    private int age;
    //Gender.
    private boolean isMale = true;
    // The Boar's current food level (hunger).
    private int foodLevel;

    /**
     * Create a new Boar. A Boar may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Boar will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Boar(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        isMale = rand.nextBoolean();
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = PLANT_FOOD_VALUE;
    }
    /*
     * This is what th Boar does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * @param newBoars A list to return newly born Boars.
     */
    public void act(List<Animal> newBoars)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newBoars);            
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
     * This could result in the Boar's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Boar is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBoars A list to return newly born Boars.
     */
    private void giveBirth(List<Animal> newBoars)
    {
        // New Boars are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Boar young = new Boar(false, field, loc);
            newBoars.add(young);
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
            if(animal instanceof Boar)
                {Boar Boar = (Boar)animal;
                if (Boar.getGender() != this.isMale && this.canBreed() && Boar.canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY){
                    births = rand.nextInt(MAX_LITTER_SIZE)+1;
                }
            }
        }
        return births;
        
    }
    

    /**
     * A Boar can breed if it has reached the breeding age.
     * @return true if the Boar can breed, false otherwise.
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