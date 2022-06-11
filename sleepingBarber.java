import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.Date;


public class sleepingBarber{
// main is here
	public static void main (String[] args){
// Input sleepTimeBarber and numChairs from command line
// Default sleepTimeBarber = 5, default numChairs = 3
// Print parameters.
// instantiate shop here.
		int sleepTimeBarber = 0;
		int numChairs = 0;
		
		System.out.println("Useage: <sleep time of barber> <number of chairs in waiting room>");
		
		if (args.length == 2){
			try {
				sleepTimeBarber = Integer.parseInt(args[0]);
				numChairs = Integer.parseInt(args[1]);
				
				if (sleepTimeBarber < 0 || numChairs < 0){
					System.out.println("Include proper input values");
					System.exit(0);
				}
				else if(sleepTimeBarber == 5 && numChairs == 3) {
					System.out.println("Using defaults");
				}
			}
			catch(Exception e){
				System.out.println("Include proper input values");
			}
		}
		else if (args.length == 0){
			sleepTimeBarber = 5;
			numChairs = 3;
			System.out.println("Using defaults");
		}
		else if(args.length > 2 || args.length == 1){
			System.out.println("Include proper input values");
			System.exit(0);
		}
		
		System.out.println("Sleep time of barber = " + sleepTimeBarber);
		System.out.println("Number of chairs in waiting room = " + numChairs + "\n");
		
		barberShop shop = new barberShop(numChairs, sleepTimeBarber);
		Barber barber = new Barber(shop);
		CustomerGenerator custGen = new CustomerGenerator(shop);
		Thread oneBarber = new Thread(barber);
		Thread multipleCustGen = new Thread(custGen);
		oneBarber.start();
		multipleCustGen.start();
	}
}

// Barber object that will become thread.
class Barber implements Runnable{
	barberShop shop;
	// Need access to shop object.
	public Barber(barberShop shop){
		this.shop = shop;
	}
	public void run(){
		//Simulate sleep by putting thread to sleep
		try{
			Thread.sleep(shop.sleepTimeBarber*1000);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		while(true){
			shop.cutHair();
		}
	}
}

//Customer object that will become thread.
class Customer implements Runnable{
	barberShop shop;
	Date enterTime;
	String name;
	
	//Need access to shop object.
	public Customer(barberShop shop){
		this.shop = shop;
	}
	public String getName(){
		return name;
	}
	public Date getEnterTime(){
		return enterTime;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setEnterTime(Date enterTime){
		this.enterTime = enterTime;
	}
	public void run(){
		goForHairCut();
	}
	private void goForHairCut(){
		shop.add(this);
	}
}

//CustomerGenerator that will become thread to start customer threads.
class CustomerGenerator implements Runnable{
	barberShop shop;
	int custID = 1; //Customer names start at 1
	//Need access to shop object.
	public CustomerGenerator(barberShop shop){
		this.shop = shop;
	}
	public void run(){
		while(true){
			//Create customers and pass object "shop"
			//Create thread
			//start threads
			//sleep random amount of time
			Random random = new Random();
			Customer customer = new Customer(shop);
			customer.setEnterTime(new Date());
			Thread custThread = new Thread(customer);
			customer.setName("Customer Thread " + custID);
			custThread.start();
			custID++;
			try{
				custThread.sleep(random.nextInt(2*1000)); //0-2 seconds for customer generation
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	} 
}

class barberShop{
	// Initialize variables
	// Initialize semaphores and mutex and waiting
	Queue<Customer> waitingRoom;
	static Semaphore customersReady = new Semaphore(0);
	static Semaphore barberReady = new Semaphore(0);
	static Semaphore mutex = new Semaphore(1);
	int waiting = 0;
	int nChairs = 0;
	int sleepTimeBarber;
	boolean asleep = true;
	
	public barberShop(int nChairs, int sleepTimeBarber) {
		this.nChairs = nChairs;
		this.sleepTimeBarber = sleepTimeBarber;
		waitingRoom = new LinkedList<Customer>();
	}
	
	public void cutHair(){
		// Wait on customer
		// Do things here like update number of customers waiting, signal to wake up barber, etc.
		// Simulate cutting hair with sleep
		try{
			if (asleep == true){
				System.out.println("Barber started..");
				asleep = false;
			}
			System.out.println("Barber is waiting for customer.");
			customersReady.acquire();
			System.out.println("Barber waiting for lock.");
			mutex.acquire();
			waiting = waiting - 1;
			barberReady.release();
			System.out.println("Barber found a customer in the queue.");
			mutex.release();
			System.out.println("Cutting hair of Customer : " + waitingRoom.peek().getName());
			waitingRoom.remove();
			try{
				Thread.sleep(3*1000); //3 seconds to cut hair
			}
			catch (InterruptedException e){
				e.printStackTrace();
			}
			if (waitingRoom.size() == 0){
				try{
					System.out.println("Barber went to sleep while waiting on customer.");
					asleep = true;
					Thread.sleep(sleepTimeBarber*1000);
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public void add(Customer customer){
		// Wait on entering the critical section
		// DO THINGS HERE like determine if there are enough chairs in the waiting room. Leave if waiting room is full. If waiting room isnot full, things must happen.
		try{
			System.out.println("Customer : " + customer.getName() + " entering the shop at " + customer.getEnterTime());
			mutex.acquire();
			if (waiting < nChairs){
				System.out.println("Customer : " + customer.getName() + " got a chair."); //check that customer doesnt go to waiting room if barber not busy
				waiting = waiting + 1;
				customersReady.release();
				mutex.release();
				waitingRoom.add(customer);
				barberReady.acquire();
			}
			else{
				System.out.println("*** No chair available for customer " + customer.getName());
				System.out.println("*** Customer " + customer.getName() + " Exits...");
				mutex.release();
			}
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}
}
