//by Ziyue Zhuang zhuan203@umn.edu
// and Jiatan Huang huan2460@umn.edu
package spaceexplorers.strategies;

import spaceexplorers.publicapi.*;

import java.util.*;

public class StudentStrategy implements IStrategy {

    /**
     * Method where students can observe the state of the system and schedule events to be executed.
     *
     * @param planets          The current state of the system.
     * @param planetOperations Helper methods students can use to interact with the system.
     * @param eventsToExecute  Queue students will add to in order to schedule events.
     */

    private int size;


    @Override
    public void takeTurn(List<IPlanet> planets, IPlanetOperations planetOperations, Queue<IEvent> eventsToExecute) {
        size = planets.size() + 1;

        //Finds all the visible planets and add them to a list
        List<IVisiblePlanet> vPlanet = new ArrayList<>(size);
        for (IPlanet planet: planets) {
            if (planet instanceof IVisiblePlanet) {
                vPlanet.add((IVisiblePlanet) planet);
            }
        }
        //create lists of the belonging of planets
        List<IVisiblePlanet> owned = new ArrayList<>(size);
        List<IVisiblePlanet> unoccupiedPlanet = new ArrayList<>(size);//hold for empty planets
        List<IVisiblePlanet> enemies = new ArrayList<>(size);
        //add planets to lists owned by the given team.
        for (IVisiblePlanet planet : vPlanet) {
            if (planet.getOwner().equals(Owner.SELF)) {
                owned.add(planet);
            }
            else if (planet.getOwner().equals(Owner.NEUTRAL)){
                unoccupiedPlanet.add(planet);
            }
            else if (planet.getOwner().equals(Owner.OPPONENT)){
                enemies.add(planet);
            }
        }

        //find all planets owned that have neighboring enemies.
        //access to a helper function
        List<IVisiblePlanet> allEnemyNeighbor = new ArrayList<>(size);
        for (IVisiblePlanet myPlanet : owned) {
            int countEnemy = findEnemyNum(myPlanet, enemies);
            if (countEnemy > 0) {
                allEnemyNeighbor.add(myPlanet);
            }
        }

        //find the planets that have max enemies.
        int maxEnemies = -1;
        List<IVisiblePlanet> mostContended = new ArrayList<>(size);
        for (IVisiblePlanet myPlanet : allEnemyNeighbor) {
            int numEnemies = findEnemyNum(myPlanet, enemies);
            if (numEnemies > maxEnemies) {
                if (! mostContended.isEmpty()) {//overlap
                    mostContended.clear();
                }
                mostContended.add(myPlanet);
            } else if (numEnemies == maxEnemies) {
                mostContended.add(myPlanet);
            }
        }

        //Creates array of each planet's population, using the planet ID as an index.
        Long[] planetPopulations = new Long[size];
        for (IVisiblePlanet planet : owned) {//record each planet populations
            planetPopulations[planet.getId()] = planet.getTotalPopulation();
        }

        //Command start:
        for (IVisiblePlanet myPlanet : owned) {
            long planetPop = planetPopulations[myPlanet.getId()];// population of this planet
            int comingShuttle = 0;
            List<IShuttle> IShuttles = myPlanet.getIncomingShuttles();
            for(IShuttle shuttle :IShuttles){//for handing shuttles
                if(shuttle.getOwner()== Owner.OPPONENT){
                    comingShuttle+= shuttle.getNumberPeople();
                }//the number of processing enemy shuttles heading towards this planet
            }
            planetPop -= comingShuttle; //the number of pop we operate

            // start attacks
            Stack<IEvent> targets = attack(myPlanet,planetPop,unoccupiedPlanet,enemies,planetOperations);
            while (! targets.isEmpty()) {
                IEvent attackPlanet = targets.pop();
                eventsToExecute.add(attackPlanet);
            }

            //Mainly for the planet that is surrounded by our planets
            if (!allEnemyNeighbor.isEmpty()) {
                Queue<IPlanet> mostContendMap = breathFirstSearch(mostContended.get(0), planets);
                IEdge shortEdge = null;

                if(planetPop >= myPlanet.getSize()*(.85)){
                    shortEdge = findRoute(myPlanet,mostContendMap);
                }
                if (shortEdge != null) {
                    IPlanet closePlanet = getPlanet(shortEdge.getDestinationPlanetId(), planets);
                    long farriers = planetPopulations[myPlanet.getId()] /(long)(1.5);// considering the habitability, current population,and max population,we adjusted a suitable number
                    eventsToExecute.add(planetOperations.transferPeople(myPlanet, closePlanet, farriers));
                }
            }
        }
    }

    /**
     *    Stack<IEvent>: this is an implement that create a stack of storing events about potential planets for the myPlanet to attack to operate
     *       Find neutral planets and hostile planets around the current planet, prioritize the seizure of neutral planets;
     *       when the population of the planet is greater than 1.5 times that of the hostile planet launch a certain population to attack
     * @param myPlanet    My owned planet
     * @param planetPop      The population of the planet
     * @param unoccupiedPlanet      List of planets controlled by the enemy.
     * @param enemies     List of planets controlled by the enemy.
     * @param planetOperations      Helper methods students can use to interact with the system.
     * @return
     */

    private Stack<IEvent> attack(IVisiblePlanet myPlanet,Long planetPop ,List<IVisiblePlanet> unoccupiedPlanet, List<IVisiblePlanet> enemies, IPlanetOperations planetOperations){
        Stack<IEvent> attackPlanet = new Stack<>();
        List<IVisiblePlanet> enemyNeighbors = neighborPlanet(myPlanet, enemies);
        List<IVisiblePlanet> neutralNeighbors = neighborPlanet(myPlanet, unoccupiedPlanet);

        for (IVisiblePlanet neighbor : enemyNeighbors) {
            long enemiesPop = neighbor.getTotalPopulation();
            if (planetPop > enemiesPop * 1.5) {// considering the habitability, current population,and max population,we adjusted a suitable number
                long numAttackers = (long) (planetPop/(1.5));
                planetPop -= numAttackers;
                attackPlanet.add(planetOperations.transferPeople(myPlanet,neighbor,numAttackers));
            }
        }
        for (IVisiblePlanet neighbor : neutralNeighbors) {
              if (planetPop >= 2) {
                  long countAttacker = 1 + (planetPop/10);//ASAP to attack empty planets
                  planetPop -= countAttacker;
                  attackPlanet.add(planetOperations.transferPeople(myPlanet,neighbor,countAttacker));
              }
        }
         return attackPlanet;
    }

    /**
     * //Counts the number of neighboring enemies a planet has.
     * @param myPlanet      My owned planet.
     * @param enemies    Lists of enemies planets
     * @return       The number of neighboring enemies.
     */
    private int findEnemyNum(IVisiblePlanet myPlanet, List<IVisiblePlanet> enemies) {
        int numEnemies = 0;
        Set<IEdge> edges = myPlanet.getEdges();
        for (IEdge edge : edges) {
            for (IVisiblePlanet enemyPlanet : enemies) {
                if (edge.getDestinationPlanetId() == enemyPlanet.getId()) {
                    numEnemies++;
                }
            }
        }
        return numEnemies;
    }

    /**
     * Creates a list of neighboring planets of a given planet.
     *
     * @param myPlanet    My owned planet.
     * @param otherList   Lists of planets being searched for.
     * @return   List of neighboring planets.
     */
    private List<IVisiblePlanet> neighborPlanet(IVisiblePlanet myPlanet, List<IVisiblePlanet> otherList) {
        List<IVisiblePlanet> neighboring = new ArrayList<>(size);
        Set<IEdge> edges = myPlanet.getEdges();
        for (IEdge edge : edges) {
            for (IVisiblePlanet otherPlanet : otherList) {
                if (edge.getDestinationPlanetId() == otherPlanet.getId()) {
                    neighboring.add(otherPlanet);
                }
            }
        }
        return neighboring;
    }


    /**
     * Use of breadth search, searches the planet system and adds planets to a queue in order of closeness to the most contended planet.
     * Radiate with a central point and get all planets accessible
     * @param mostContended   The  most contended planet.
     * @param planets         all this graph planets
     * @return   A queue with planets listed in order of distance from the most contended planet.
     */
    private Queue<IPlanet> breathFirstSearch(IVisiblePlanet mostContended, List<IPlanet> planets) {
        Queue<IPlanet> mostContendMap = new LinkedList<>();
        mostContendMap.add(mostContended);

        while (mostContendMap.size() < planets.size()) {
            Queue<IPlanet> temp = new LinkedList<>();
            for (IPlanet planet : mostContendMap) {//Radiate with a central point and get all planets accessible
                for (IEdge edge : planet.getEdges()) {
                    IPlanet neighbor = getPlanet(edge.getDestinationPlanetId(), planets);
                    if (!mostContendMap.contains(neighbor) && !temp.contains(neighbor)) {
                        temp.add(neighbor);
                    }
                }
            }
            while (!temp.isEmpty()) {
                mostContendMap.add(temp.remove());
            }
        }//Return to the planet with the most enemies from near to far
        return mostContendMap;
    }


    /**
     * Finds the shortest edge to proceed towards the most contended planet.
     *
     * @param myPlanet      The student's planet.
     * @param mostContendMap   The queue of planets listed in order of distance from the most contended planet.
     * @return   The edge that provides the quickest route to the student's most contended planet.
     */
    private IEdge findRoute(IVisiblePlanet myPlanet, Queue<IPlanet> mostContendMap) {
        Set<IEdge> allEdges = myPlanet.getEdges();
        for (IPlanet planet : mostContendMap) {
            for (IEdge shortEdge : allEdges) {
                if (planet.getId() == shortEdge.getDestinationPlanetId()) {
                    return shortEdge;
                }
            }
        }
        return null;
    }

    /**
     * Finds the planet associated with a given planet ID.
     *
     * @param planetID   A planet's ID number.
     * @param planets    All this graph planets
     * @return   The planet associated with the given ID.
     */
    private IPlanet getPlanet(int planetID, List<IPlanet> planets) {
        for (IPlanet planet : planets) {
            if (planet.getId() == planetID) {
                return planet;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "Student";
    }

    @Override
    public boolean compete() {
        return false;
    }

}
