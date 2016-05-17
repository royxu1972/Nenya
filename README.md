# Nenya

The project Nenya aims to provide advanced algorithms for various 
applications of combinatorial testing, including test suite generation,
reduction and prioritization.

## Test Suite Generation

Generate a covering array with as few test cases as possible.

* AETG (a greedy one-test-at-a-time generation algorithm with constraint support)

Generate a random test suite.

* RT
* ART-FSCS

## Test Suite Reduction

Reduce the size of a given test suite while maintaining its combination coverage.
A refined randomized post-optimization algorithm is applied.

## Test Suite Prioritization

Reordering existing test suite to maximize combination coverage or minimize switching cost.

* Combination coverage based prioritization
    * Greedy Algorithm

* Switching cost based prioritization
	* Greedy Algorithm
	* Dynamic Programming (optimal)
	* Genetic Algorithm
	* LKH Solver for TSP (Windows only)

* Hybrid based prioritization (combination coverage + testing cost)
    * Greedy Algorithm

* Multi-objective optimization (combination coverage + testing cost)
	* NSGA-II

