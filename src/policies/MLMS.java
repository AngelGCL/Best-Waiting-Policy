package policies;

import java.util.ArrayList;

import dataManagement.Client;
import policies.MLMSBLL.Clerks;
import useful_classes.Queue;
import useful_classes.SLLQueue;

/**
 * Multiple Lines Multiple Servers waiting policy
 * 
 * Each service post has its own waiting line (one line per server). 
 * Once a person enters a waiting line, that person cannot transfer to another line,
 * even if one becomes empty. When a new person arrives looking for service, the person will 
 * choose the first line that has minimum number of persons waiting, 
 * as per the indexes identifying the corresponding service posts. If a line becomes empty, 
 * then the server there remains idle until a new customer arrives and selects that particular line.
 * 
 * @author Angel G. Carrillo Laguna
 *
 */
public class MLMS {

	private Clerks[] servers;
	protected int time;//Current time unit
	private float avgWaitT;
	private int overpassClients;

	private SLLQueue<Client> arrivalQueue;
	private ArrayList<Client> terminatedList;
	

	/**
	 * Constructor method.
	 * @param serverNum number of server posts.
	 * @param file {@link Queue} created from a file that has been read.
	 */
	public MLMS(int serverNum, Queue<Client> file){
		servers = new Clerks[serverNum];
		time = 0;
		avgWaitT = 0.00f;
		try {
			arrivalQueue = ((SLLQueue<Client>) file).copy();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		setClerks();
		terminatedList = new ArrayList<Client>();
		overpassClients = 0;
	}
	
	/**
	 * Initializes and assigns a line for the {@link Clerks} inside the {@code servers} array. 
	 */
	public void setClerks() {
		for(int i=0; i<servers.length; i++) {
			servers[i] = new Clerks(new SLLQueue<Client>(), null);
		}
	}
	
	/**
	 * Helper Method used by the {@code isIdle()} method to determine if there is any available {@link Clerks}.
	 * @return {@link Integer} between -1 and {@code (servers.length - 1)}. If -1, then there is no {@link Clerks} available.
	 */
	public int getAvailable(){
		for (int i=0; i<servers.length; i++) {
			if(servers[i].isAvailable())
				return i;
		}
		return -1;
	}  
	
	/**
	 * Boolean Method to determine if there is an available {@linkplain Clerks}.
	 * @return True if there is an available Clerk, false otherwise.
	 */
	public boolean isIdle() {
		if(getAvailable() == -1)
			return false;
		return true;
	}
	
	/**
	 * Helper method to find all the available {@link Clerks}.
	 * @return {@link ArrayList} with the indexes of the available Clerks.
	 */
	public ArrayList<Integer> getAllAvailables(){
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int i=0; i<servers.length; i++) {
			if(servers[i].isAvailable())
				indexes.add(i);
		}
		return indexes;
	}
	
	/**
	 * Boolean method to determine if all lines are empty.
	 * @return True if all lines are empty, false otherwise.
	 */
	public boolean allLinesEmpty() {
		for(int i=0; i<servers.length; i++) {
			if(!servers[i].getCurrentLine().isEmpty())
				return false;
		}
		
		return true;
	}
	
	/**
	 * Method to start giving service to a first {@link Client} in a line whenever possible.
	 */
	public void getAttended() {
		ArrayList<Integer> index = getAllAvailables();
		while(!index.isEmpty()) {
			int server = index.get(0);
			if(!servers[server].getCurrentLine().isEmpty()) {
				Client jb = servers[server].getCurrentLine().dequeue();
				jb.isAttended(time);//saves waiting time of client
				avgWaitT += jb.getWaitingTime();
				jb.setDepartureTime(time);
				terminatedList.add(jb);//this will be used to calculate the over-passing clients. No matter what order of arrival
				servers[server].setCurrentClient(jb);
			}
			index.remove(0);
		}
	}
	
	/**
	 * Helper method for when {@link Clerks} complete a service.
	 * @param server index of the clerk that completed a service.
	 */
	public void completeServ(int server) {
		servers[server].setCurrentClient(null);
	}
	
	/**
	 * Iterates over all the {@link Clerks} and checks if they have completed a service.
	 */
	public void checkCompleted() {
		for(int i=0; i<servers.length; i++) {
			if(servers[i].getCurrentClient() != null)
				if(servers[i].getCurrentClient().getDepartureTime() == time)
					completeServ(i);
		}
	}
	
	/**
	 * Helper method for when a {@link Client} arrives, this will lead him into a line according to the this policy.
	 * @param client {@link Client} that arrived.
	 */
	public void arrive(Client client) {
		int min = servers[0].getCurrentLine().size();
		int index = 0;
		if(servers.length > 1) {	
			for(int i=1; i<servers.length; i++) {
				if(servers[i].getCurrentLine().size() < min) {
					min = servers[i].getCurrentLine().size();
					index = i;
				}
			}
		}
		servers[index].getCurrentLine().enqueue(client);
	}
	
	/**
	 * Boolean method to determine if ALL {@link Clerks} are available.
	 * @return True if ALL clerks are available, false otherwise.
	 */
	public boolean serversEmpty() {
		for(int i = 0; i<servers.length; i++) {
			if(!servers[i].isAvailable())
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
			else
				return;
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
		else if(!allLinesEmpty()) {
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
			if(servers[i].getCurrentClient() != null && min > servers[i].getCurrentClient().getDepartureTime()) {
				min = servers[i].getCurrentClient().getDepartureTime();
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
			
			if(isIdle()) {
				getAttended();//always happens since they're in line	
			}
			
			checkArrival();
			time++;
			timeSkip();
		}
		
		int clients = terminatedList.size();//total number of clients
		float avgWaitperClient = (avgWaitT/clients); //avg waiting time per client
		setOverpassingClients();//ovrpass total
		
		return "MLMS " + servers.length + ": " + (time-1) + " " + String.format("%.2f",avgWaitperClient) + " " + overpassClients;
		
	}
	
	/**
	 * Clerk Class made to facilitate the accessing and arrangement of data.
	 * @author Angel G. Carrillo Laguna
	 *
	 */
	protected class Clerks {
		
		private SLLQueue<Client> currentLine;
		private Client currentClient;
		
		public Clerks(Queue<Client> line, Client client){
			this.currentClient = client;
			this.currentLine = (SLLQueue<Client>) line;
		}

		public SLLQueue<Client> getCurrentLine() {
			return currentLine;
		}

		public void setCurrentLine(Queue<Client> currentLine) {
			this.currentLine = (SLLQueue<Client>) currentLine;
		}

		public Client getCurrentClient() {
			return currentClient;
		}

		public void setCurrentClient(Client currentClient) {
			this.currentClient = currentClient;
		}
		
		public boolean isAvailable() {
			if(getCurrentClient() == null)
				return true;
			
			return false;
		}
		
	}
}
