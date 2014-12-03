DiceSimulator
=============

DiceSimulator is a small program for simulating and calculating different statistics using dice. I use it for various pen & paper role playing games. It is a small project I started to learn JavaFX and the functional API of Java8.

Rules
-----

A check is one roll with a number of dice (the level) of the same kind (e.g. d6) against a given difficulty. If some of the dice show the highest possible number of eyes (e.g. a 6 on a six sided die), these dice are rolled again (repeadetly if necessary) and the results are summed up. Each die that has a result greater than the difficulty counts as one success.

The rules are best explained by an example:
- Jack wants to execute a check with level 4 against difficulty 3. The system uses six sided dice.
- He takes four dice and rolls them. The results are: 4,6,3,5
- As the second die shows the highest number of eyes, he rolls it again and gets a 3. He adds the 3 to the previous result (6) and gets a 9. The new results are: 4,9,3,5
- He counts the number of dice with a result greater than the difficulty. There are 3 of them (the 4,9 and 5).
- Jack announces three successes on his check.

Notes
-----

- JRE version 8 or newer is required (heavy use of JavaFX and the new APIs)
- A set of precalculated results is loaded on start (no need to start the simulation part if you just want to view some results)
- Saves its data in the same directory the JAR is in as "data.gz"

ToDo (stuff I may or may not do in the future)
----------------------------------------------

- GUI i18n (German only at the moment)
- Parameter / Config Options for simulation steps and types of dice
- GUI icons (svg)
- (More) animations
- Touch and mouse-wheel support