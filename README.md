
This project depends on Java and Maven 3.3. 

###### Run it:

```
mvn clean install exec:java -Dexec.args="PATH_TO_FILE"

#(e.g mvn clean install exec:java -Dexec.args="D:\testInput.txt")
```

###### Algorithm:

  
  This is a greedy algorithm that puts all the data in a queue, then it pops
  each element verifying that it complies the restrictions. If the element
  does not comply the restrictions , the algorithm removes it as a solution
  for the current iteration and tries a new solution with the next element. 
  
  This is way to test every possible combination, but the algorithm
  ends when the first solution is found.
  
  The steps are:
 ```  
   1. Put all the elements in a queue of unscheduled elements.
     2. Create a track.
     3. For each element in the queue:
     	
       2.1 If the morning session is not filled up:
     		2.1.1 Verify that the element can be put in the session.
     			2.1.1.a If possible: put the element in the session and remove it from the queue. 
     			2.1.1.b If not possible: rule out the current element and try with the next one in the queue, the current element is put at the tail of the queue.
     				2.1.1.b.1 If all the elements were already tested: remove the last inserted element, rule it out  as a solution for that iteration and  put it at the tail of the queue. 	
     		2.1.2 Go to step 3. 
     				
    	2.2 If the afternoon session is not completely filled up:
     		2.2.1 Verify that the element can be put in the session. 
     			2.2.1.a If possible: put the element in the session and remove it from the queue.     				
     			2.2.1.b If not possible: rule out the current element and try with the next one in the queue, the current element is put at the tail of the queue.          
           		2.2.1.b.1 If all the element were tested
           				2.2.1.b.1.a If the current state allows the Meet Event to start: create a new track.
           				2.2.1.b.1.b If the current state does not allow the Meet Event to start: remove the last inserted element, rule it out  as a solution for that iteration and  put it at the tail of the queue. 	
           	2.2.2 Go to step 3.
  
  		2.3 If the afternoon session is completely filled up:
  			2.3.1 Create a new track
  			2.3.2 Go to step 3.   
 ```