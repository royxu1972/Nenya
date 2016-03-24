# C(ombinatorial) T(esting) lib

CTlib provides some algorithms for combinatorial testing.

## Test Suite Generation

Generate a covering array with as few test cases as possible.

* AETG (with support for constraint)

Generate a random test suite.

* RT
* ART-FSCS

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

