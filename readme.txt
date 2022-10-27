Sleeping Barber Problem:

The goal of the sleeping barber program is to solve the Sleeping Barber Problem as given in the assignment
instructions using semaphores, mutex, and threads to properly implement concurrency. The code is split into
5 classes (sleepingBarber, Barber, Customer, CustomerGenerator, and barberShop) and each part's description
and role is shown below:

sleepingBarber class:

We create the program's main here to allow us to run the program as a whole. Within the main we initalizise and
print our input variables and catch any input errors to avoid mis-inputs (make sure proper input values are 
provided). An example of a case we'd catch is when only 1 input is given. In this case we ask for proper input 
values andexit the system since that does not meet the given usage. The main also instantiates our shop, barber,
and customer generator so that we can create and start the 1 barber thread and multiple customer threads.

---------------------------------------------------------------------------------------------------------------

Barber class:

The Barber class contains a barberShop object so that we can put our 1 barber to sleep for their intitial 
inputted sleep time and call cutHair() for the shop. We also implement Runnable and override run() since the 
Barber object becomes a thread.

---------------------------------------------------------------------------------------------------------------

Customer class:

Similar to the Barber class, we have a barberShop object so that we can add our customers to the waitingRoom
queue in the barberShop class. We also implement Runnable and override run() since the Customer object becomes 
a thread. Each customer has a specific name and date for when they enter the shop.

---------------------------------------------------------------------------------------------------------------

CustomerGenerator class:

Similar to the previous 2 classes we implement Runnable and override run() since CustomerGenerator becomes a
thread that starts the multiple customer threads. We create and assign names/dates to customers in a while loop
that generates new customer objects w/ random sleep times between 0 and 2 seconds.

---------------------------------------------------------------------------------------------------------------

barberShop class:

In this class we create our semaphores and the queue that contains the waiting customers so that we can do the 
concurrency part of the project. The constructor takes in the user-inputs so that we can use those values within
our functions. 

cutHair function:

The cutHair() function updates the state of the barber (awake or asleep) and uses the semaphores and mutex 
to access/manilpulate the customers, barber, and waiting (# of customers in waitingRoom). We also remove the 
oldest element (FIFO) of the queue (customer leaves the shop) after a 3 second sleep which simulates hair 
cutting. Lastly, we put the barber to sleep for the inputted sleep time if he finishes cutting a customer's
hair and there are no remaining customers in the shop/waitingRoom.

add function:

The add(Customer customer) function uses the semaphores and mutex to access/manilpulate the customers, barber, 
and waiting (# of customers in waitingRoom). If there are empty chairs in the waitingRoom we add the customer
to the queue. Otherwise the customer leaves the shop.
