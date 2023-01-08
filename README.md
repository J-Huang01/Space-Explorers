CSCI 1933 
Project4: Space Explorers
4/29/2022

## Group members’ names and x500s
Jiatan Huang, huan2460
Ziyue Zhuang, zhuan203

---
 
## Contributions of each partner (if working with a partner)

### Classes we created and edited:

• StudentStrategy.java (Ziyue, Jiatan)
• StudentStrategy.jar (Ziyue, Jiatan)

We collaborated on most of the work and discussed the details. Also the teaching assistant gave us some help.

---

## How to compile and run our program

1. Using the run function that comes with intelliJ, The game can be tested by clicking run in Driver.java and checking "StudentStrategy" on the left side to play with the strategy we created. 

2.There is also the option to generate the number of wins directly by clicking run in SpaceExplorers.java, and you can choose different opponents, such as RandomStrategy, AI1Strategy, AI2Strategy and AI3Strategy.

---
 ## A paragraph summarizing our strategy

	Our strategy is:
	1.Find neutral planets and hostile planets around the current planet, prioritize(sending 2 pop) the seizure of neutral planets; when the population of the planet is greater than 1.5 times that of the hostile planet sending ships with 2/3 of the population.
	2.Next, if the planet is not surrounded by hostile or neutral planets, we want him to attack towards the planet with the most enemies, first moving towards the shortest path and approaching step by step. To sum up, it means to gather troops to the place with the most enemies

---

##A second paragraph that lists the data structures you used in implementing your
strategy along with a brief justification why each data structure is appropriate for
the task in which you used it.

We use three data structure types: list, stack and queue.

Lists: store planets belong to different players, 
	For this part we don't care about the order, only the ability to loop all.
	eg: List<IVisiblePlanet> owned = new ArrayList<>(size); 

Queues: store a breadth firth search in the system
	We create a path about the planet with the most enemies with the help of the queue property，which return to the planet with the most enemies from near to far.
	eg: private Queue<IPlanet> breathFirstSearch(IVisiblePlanet mostContended, List<IPlanet> planets)

Stacks: store attack plans to attack neighbors
	this is an implement that crete a stack of storing events about potential planets for the myPlanet to attack to operate
	eg:private Stack<IEvent> attack(IVisiblePlanet myPlanet,Long planetPop ,List<IVisiblePlanet> unoccupiedPlanet, List<IVisiblePlanet> enemies, IPlanetOperations planetOperations)

---

## Additional features that you implemented (if applicable)

We use some helper functions:

• attack(IVisiblePlanet myPlanet,Long planetPop ,List<IVisiblePlanet> unoccupiedPlanet, List<IVisiblePlanet> enemies, IPlanetOperations planetOperations)

	this is an implement that create a stack of storing events about potential planets for the myPlanet to attack to operate

• findEnemyNum(IVisiblePlanet myPlanet, List<IVisiblePlanet> theirVisiblePlanets)

	this is a helper method to count the number of neighboring enemies a planet has.

• neighborPlanet(IVisiblePlanet myPlanet, List<IVisiblePlanet> otherList)

	this helper method creates a list of neighboring planets of a given type
* breathFirstSearch(IVisiblePlanet mostContended, List<IPlanet> planets)
	Use of breadth search, searches the planet system and adds planets to a queue in order of closeness to the most contended planet. Radiate with a central point and get all planets accessible

* findRoute(IVisiblePlanet myPlanet, Queue<IPlanet> mostContendMap) {
	this could find the shortest edge to proceed towards the most contended planet.

* getPlanet(int planetID, List<IPlanet> planets) {
	this could find the planet with a given planet ID.

---

## Any known bugs or defects in the program


--- ## Any outside sources (aside from course resources) consulted for ideas used in the project, in the format: 
We do not use any outside sources.

Statement: I certify that the information contained in this README file is complete and accurate. I have both read and followed the rules described in the “Course Policies” section of the course syllabus. (Jiatan Huang, Ziyue Zhuang)

---