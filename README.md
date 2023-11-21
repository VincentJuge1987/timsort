Timsort minimalistic project
=======
This project contains two `Java` files:

1. **ComparableTimeSort.java**, which is just a copy of the **ComparableTimeSort.java** used in **Java 21**, with the following change:

   The **MIN_MERGE** constant, which was set to 32 (i.e., runs of length less than 32 might have to be extended before being merged) is here set to 1.
   This change is aimed to make testing easier.

2. **Evaluator.java**, which contains exactly two public methods:
   - its constructor, whose argument is the initial value of the threshold **t** used to switch from **naive** merging mode to **galopping** merging mode;
   
   - its method **evaluate**, whose argument consists in two lists of σ integers, say a<sub>1</sub>,a<sub>2</sub>,...,a<sub>σ</sub> and b<sub>1</sub>,b<sub>2</sub>,...,b<sub>σ</sub>, and whose result is the number of comparisons required by **ComparableTimeSort.java** to merge two ascending runs **A** and **B**, merge whose result should consist in a<sub>1</sub> elements from **A**, then b<sub>1</sub> elements from **B**, then a<sub>2</sub> elements from **A**, then b<sub>2</sub> elements from **B**, and so on.
