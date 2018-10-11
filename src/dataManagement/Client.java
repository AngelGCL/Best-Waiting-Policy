package dataManagement;


/**
 * Client Class made to help store some statistic values for testing the simulations.
 * @author Angel G. Carrillo Laguna
 *
 */
public class Client implements Comparable<Client>{
   
   private int arrivalTime;    // arrival time of this job
   private int serviceTime;  // remaining service time for this job
   private int departureTime;  // time when the service for this job is completed
   private int waitingTime;
   private int id;//used to determine order of inputs inside a file
   private int remainingTime;
   
   public Client(int at, int rt) { 
	arrivalTime = at; 
	serviceTime = rt;
	this.id = 0;
	this.remainingTime = rt;
   }
   public int getDepartureTime() {
	return departureTime;
   }
   public void setDepartureTime(int currentTime) {
	this.departureTime = currentTime + serviceTime;
   }
   public int getRemainTime() {
	   return remainingTime;
   }
   public void updateRemainTime(int d) {
	   this.remainingTime = remainingTime - d;
   }
   public int getArrivalTime() {
	return arrivalTime;
   }
   public int getServiceTime() {
	return serviceTime;
   }
	
   public int getWaitingTime() {
	   return waitingTime;
   }
   /**
    * Registers an update of the time waited to be attended. 
    * @param currentTime the current time of the work day. 
    */
   public void isAttended(int currentTime) { 
	   waitingTime = currentTime - arrivalTime; 
   }
	
   /**
    * Generates a string that describes this job; useful for printing
    * information about the job.
    */
   public String toString() { 
	return arrivalTime +
		 " " + serviceTime;				
   }
   
   @Override
   public int compareTo(Client o) {

	   return Integer.compare(this.getArrivalTime(), o.getArrivalTime());
   }
   
   public int getId() {
	return id;
   }
   
   public void setId(int id) {
	this.id = id;
   }
   
}