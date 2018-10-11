package dataManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import policies.MLMS;
import policies.MLMSBLL;
import policies.MLMSBWT;
import policies.SLMS;
import useful_classes.Queue;
import useful_classes.SLLQueue;

/**
 * Data Reader class made for testing purposes.
 * 
 * @author Angel G. Carrillo Laguna
 *
 */
public class DataReader {


	
	public DataReader(){}

	/**
	 * Reads the file dataFiles.txt 
	 * @return {@link ArrayList} of {@link String} with the names of the files to be read for testing.
	 * @throws IOException
	 */
	public ArrayList<String> readDataFiles() throws IOException {
		ArrayList<String> files = new ArrayList<String>();
		FileReader inputFILE = new FileReader("inputFiles/dataFiles.txt");
		BufferedReader buffRead = new BufferedReader(inputFILE);
		String line;

		while ((line = buffRead.readLine()) != null) {
			files.add(line);
		}

		buffRead.close();
		return files;
	}

	/**
	 * Reads files data_i.txt. It may write an outputFile when format of file is not correct.
	 * @param data_i
	 * @return {@link Queue} of {@link Client} with the data from file inside.
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws FileNotFoundException and creates an outputFile with the exception in it.
	 */
	public Queue<Client> readFile(String data_i) throws NumberFormatException, IOException, FileNotFoundException{

		try {
			FileReader inputFILE = new FileReader("inputFiles/" + data_i + ".txt");
			BufferedReader buffRead = new BufferedReader(inputFILE);
			String line;
			Pattern p = Pattern.compile("^([0-9]+)[\\s]([1-9])([0-9]*)$");
			SLLQueue<Client> inputQueue = new SLLQueue<Client>();
			int clientID = 0;//this is used so that MLMSBLL can do the transfers easier
			while ((line = buffRead.readLine()) != null) {
				Matcher m = p.matcher(line);
				if(m.matches()) {
					String[] numbers = line.split(" ");
					Client client = new Client(new Integer(numbers[0]), new Integer(numbers[1]));
					client.setId(clientID); 
					clientID++;
					inputQueue.enqueue(client);
				}
				else {
					PrintWriter out = new PrintWriter("outputFiles/"+ data_i + "_OUT.txt");
					out.println("Input file does not meet the expected format or it is empty.");
					out.close();
					return null;
				}
			}
			buffRead.close();
			return inputQueue;
		}catch(FileNotFoundException e) {
			PrintWriter out = new PrintWriter("outputFiles/"+ data_i + "_OUT.txt");
			out.println("Input file not found.");
			out.close();
		}
		return null;
	}

	/**
	 * Method used to run the simulation. It will write the outputFile corresponding to every correct file read.
	 * @throws IOException
	 */
	public void mainRead() throws IOException {

		ArrayList<String> s = readDataFiles();
		for(int i=0; i<s.size(); i++) {
			Queue<Client> file = readFile(s.get(i));


			if(file != null) {
				PrintWriter out = new PrintWriter("outputFiles/"+ s.get(i) + "_OUT.txt");
				for(int j=1; j<=5; j+=2) {
					SLMS policy1 = new SLMS(j, file);
					out.println(policy1.process());
				}
				for(int j=1; j<=5; j+=2) {
					MLMS policy2 = new MLMS(j, file);
					out.println(policy2.process());
				}
				for(int j=1; j<=5; j+=2) {
					MLMSBLL policy3 = new MLMSBLL(j, file);
					out.println(policy3.process());
				}
				for(int j=1; j<=5; j+=2) {
					MLMSBWT policy4 = new MLMSBWT(j, file);
					out.println(policy4.process());
				}

				out.close();
			}
		}
	}

}
