# Nenya

Nenya provides algorithms for various applications of
combinatorial testing (CT), including test suite generation,
reduction, prioritization and evaluation.

## Test Suite Generation

Conventional t-way covering array (CA) generation
with one-test-at-a-time framework.

- **AETG-like** (with constraints)
- **GA** (classic genetic algorithm)

Sequence covering array (permutation) generation 
for event-based software.

- **SCA**

Random and adaptive random test suite generation
with fixed size.

- **RT**
- **ART-FSCS**

## Test Suite Reduction

Reduce the size of a given test suite while maintaining 
its t-way combination coverage. A refined randomized 
post-optimization algorithm is provided.

## Test Suite Prioritization

Reordering existing test suite, especially focusing on
optimizing combination coverage and switching cost.

- Algorithms to maximize rate of combination coverage.
    * Greedy

- Algorithms to minimize total switching cost.
    * Greedy
    * Dynamic Programming (optimal)
    * GA
    * LKH Solver for TSP (Windows only)

- Algorithms to balance combination coverage and testing cost
    * Greedy (hybrid metric)
    * NSGA-II

## Test Suite Evaluation

Evaluate t-way combination coverage and fault profile
coverage for a given test suite.