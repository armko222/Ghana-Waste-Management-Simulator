# Hash Table and QuickSort Test Evidence 
 
## Hash Table Collision Evidence 
 
| Load Factor | Capacity | Entries | Collisions | Probes | 
|-------------|----------|---------|------------|--------| 
| 0.25 | 16 | 4 | 6 | 6 | 
| 0.50 | 16 | 8 | 22 | 22 | 
| 0.75 | 16 | 12 | 38 | 38 | 
 
## Tests Performed 
 
### CustomHashTable 
 
- Empty table 
- Single entry 
- Updating an existing key 
- Removing an existing key 
- Removing a non-existent key 
- Forced hash collision 
- Collision statistics 
- Collision-statistics reset 
 
### CustomSet 
 
- Adding values 
- Duplicate values 
- Contains 
- Removal 
- Size 
- Empty state 
 
### CustomMap 
 
- Put 
- Get 
- Contains key 
- Remove 
- Size 
- Empty state 
 
### QuickSort 
 
- Unordered array 
- Already sorted array 
- Duplicate values 
- Single-element array 
- Empty array 
- Null array 
 
## Test Execution 
 
Command: 
 
mvn test 
 
Result: 
 
BUILD SUCCESS