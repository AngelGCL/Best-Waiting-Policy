package main_classes;

import java.io.FileNotFoundException;

import dataManagement.FileGenerator;

public class FileGeneratorMain {

	public static void main(String[] args) throws FileNotFoundException {
		FileGenerator genny = new FileGenerator(100, 500);
		genny.generateFiles(5);

	}

}
