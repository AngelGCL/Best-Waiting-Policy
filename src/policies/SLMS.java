package policies;



import java.util.ArrayList;

import dataManagement.Client;
import policies.MLMS.Clerks;
import useful_classes.Queue;
import useful_classes.SLLQueue;

/**
 * Single Line Multiple Servers waiting policy.
 * 
 * Under this policy, there is only one waiting line and one or more service posts. 
 * Whenever a post is available, the first person in line, if any, 
 * will start to be served by the service person at the post. 
 * In the case in which there are more than one server available at a moment, 
 * then the first person in line will go to the available post having  
 * min index value among those available.
 * 
 * @author Angel G. Carrillo Laguna
 *
 */
public class SLMS {
	
	private Client[] servers;
	protected int time; // current time unit
	private float avgWaitT;
	private int overpassClients;
	private SLLQueue<Client> waitingQueue;
	private SLLQueue<Client> arrivalQueue;
	private ArrayList<Client> terminatedList;
	

	/**
	 * Constructor method.
	 * @param serverNum number of server posts.
	 * @param file {@link Queue} created from a file that has been read.
	 */
	public SLMS(int serverNum, Queue<Client> file){
		servers = new Client[serverNum];
		time = 0;
		avgWaitT = (float) 0.00;
		try {
			arrivalQueue = ((SLLQueue<Client>) file).copy();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		waitingQueue = new SLLQueue<Client>();
		terminatedList = new ArrayList<Client>();
		overpassClients = 0;
	}
	

	/**
	 * Helper Method used by the {@code hasAvailable()} method to determine if there is any available servers.
	 * @return {@link Integer} between -1 and {@code (servers.length - 1)}. If -1, then there is no servers available.
	 */
	public int getAvailable(){
		for (int i=0; i<servers.length; i++) {
			if(servers[i] == null)
				return i;
		}
		return -1;
	}  
	
	/**
	 * Helper method to find all the available servers.
	 * @return {@link ArrayList} with the indexes of the available servers.
	 */
	public ArrayList<Integer> getAllAvailables(){
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int i=0; i<servers.length; i++) {
			if(servers[i] == null)
				indexes.add(i);
		}
		return indexes;
	}
	
	/**
	 * Boolean Method to determine if there is an available servers.
	 * @return True if there is an available server, false otherwise.
	 */
	public boolean hasAvailable() {
		if(getAvailable() == -1)
			return false;
		return true;
	}
	
	/**
	 * Method to start giving service to a first {@link Client} in a line whenever possible.
	 */
	public void getAttended() {
		ArrayList<Integer> index = getAllAvailables();
		while(!index.isEmpty()) {
			int server = index.get(0);
			if(!waitingQueue.isEmpty()) {
				Client jb = waitingQueue.dequeue();
				jb.isAttended(time);//saves waiting time of client
				avgWaitT += jb.getWaitingTime();
				jb.setDepartureTime(time);
				terminatedList.add(jb);//this will be used to calculate the over-passing clients. No matter what order of arrival
				servers[server] = jb;
			}
			index.remove(0);
		}
	}
	
	/**
	 * Helper method for when servers complete a service.
	 * @param server index of the server that completed a service.
	 */
	public void completeServ(int server) {
		servers[server] = null;
	}
	
	/**
	 * Iterates over all the servers and checks if they have completed a service.
	 */
	public void checkCompleted() {
		for(int i=0; i<servers.length; i++) {
			if(servers[i] != null)
				if(servers[i].getDepartureTime() == time)
					completeServ(i);
		}
	}
	
	/**
	 * Helper method for when a {@link Client} arrives, this will lead him into a line according to the this policy.
	 * @param client {@link Client} that arrived.
	 */
	public void arrive(Client client) {
		waitingQueue.enqueue(client);
	}
	
	/**
	 * Boolean method to determine if ALL servers are available.
	 * @return True if ALL clerks are available, false otherwise.
	 */
	public boolean serversEmpty() {
		for(int i = 0; i<servers.length; i++) {
			if(servers[i] != null)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if there are possible arrivals, if so then lets the clients arrive.
	 */
	public void checkArrival() {
		int size = arrivalQueue.size();
		for(int i=0; i<size; i++)
			if(arrivalQueue.first().getArrivalTime() == time)
				arrive(arrivalQueue.dequeue());
			else {
				return;
			}
	}
	
	public int getTime() {
		return time;
	}
	
	/**
	 * Sets the total number of clients that arriver after a certain client but completed their service earlier.
	 */
	public void setOverpassingClients() {
		for(int i=0; i<terminatedList.size() - 1; i++) {
			for(int j=i+1; j<terminatedList.size(); j++) {
				if(terminatedList.get(i).getArrivalTime() > terminatedList.get(j).getArrivalTime())
					overpassClients += 1;
			}
		}
	}
	
	/**
	 * Boolean method to determine if the process has been finished.
	 * @return True if all statements are true.
	 */
	public boolean done() {
		if(!arrivalQueue.isEmpty()) {
			return false;
		}
		else if(!waitingQueue.isEmpty()) {
			return false;
		}
		else if(!serversEmpty()) {
			return false;
		}
		else
			return true;
	}
	
	/**
	 * Skips time units when there is nothing to be done from unit t1 to unit t2.
	 */
	public void timeSkip() {
		int min = time;
		if(!arrivalQueue.isEmpty()) {
			min = arrivalQueue.first().getArrivalTime();
		}
		for(int i=0; i<servers.length; i++) {
			if(servers[i] != null && min > servers[i].getDepartureTime()) {
				min = servers[i].getDepartureTime();
			}
		}
		if(time < min)
			time = min;
	}
	
	/**
	 * Method used to process the data according to the waiting policy.
	 * @return String with calculated statistics.
	 */
	public String process() {
		while (!done()) {
			checkCompleted();			
			if(hasAvailable()) {
				getAttended();//always happens since they're in line	
			}
			checkArrival();
			time++;
			timeSkip();
		}

		int clients = terminatedList.size();//total number of clients
		float avgWaitperClient = (avgWaitT/clients); //avg waiting time per client
		setOverpassingClients();//ovrpass total
		
		return "SLMS " + servers.length + ": " + (time-1) + " " + String.format("%.2f",avgWaitperClient) + " " + overpassClients;
		
	}
}
