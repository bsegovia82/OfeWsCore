package pruebas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pruebas {

	public static void main(String[] args) throws Throwable {
		List<String> lClavesArchivoSRI = obtenerListaClaves("D:/0990022011001_Recibidos (6).txt");
		List<String> lClavesArchivoSEED = obtenerListaClavesSEED("D:/claves_febrero.txt");

		List<String> lResultadoFinal = restarListas(lClavesArchivoSRI, lClavesArchivoSEED);

		System.out.println("Total NO encontrados : " + lResultadoFinal.size());

		for (String lClavesNoEncontradas : lResultadoFinal) 
			System.out.println(lClavesNoEncontradas);
		
	}

	private static List<String> restarListas(List<String> lMinuendo, List<String> lSustraendo) {
		List<String> lResultadoFinal = new ArrayList<>();

		boolean encontro = false;

		for (String lClaveM : lMinuendo) {
			encontro = false;
			for (String lClaveS : lSustraendo) {
				if (lClaveM.equals(lClaveS)) {
					encontro = true;
					break;
				}
			}
			if (!encontro) {
				lResultadoFinal.add(lClaveM);
			}
		}

		return lResultadoFinal;

	}

	private static List<String> obtenerListaClavesSEED(String file) throws IOException, FileNotFoundException {
		List<String> lLista = new ArrayList<>();
		try (BufferedReader lLector = new BufferedReader(new FileReader(file));) {
			String line = "";
			StringBuilder lResultado = new StringBuilder();
			Integer lTotal = 0;
			while ((line = lLector.readLine()) != null) {

				lLista.add(line);
				lResultado.append(line + "\n");
				lTotal++;
			}
			System.out.println(lResultado);
			System.out.println("Total " + lTotal);
		}
		return lLista;
	}

	private static List<String> obtenerListaClaves(String lFile) throws IOException, FileNotFoundException {
		List<String> lLista = new ArrayList<>();
		try (BufferedReader lLector = new BufferedReader(new FileReader(lFile));) {
			String line = "";
			StringBuilder lResultado = new StringBuilder();
			String lClave = "";
			Integer lTotal = 0;
			while ((line = lLector.readLine()) != null) {
				if (line.length() > 1) {
					Integer pos = 0;
					for (Integer tamanio = line.length() - 1; tamanio > 0; tamanio--) {
						String lValor = Character.toString(line.charAt(tamanio));
						if (lValor.trim().length() < 1) {
							pos = tamanio;
							break;
						}
					}
					lClave = line.substring(pos - 49, pos);
					lLista.add(lClave);
					lResultado.append(lClave + "\n");
					lTotal++;
				}

			}
			System.out.println(lResultado);
			System.out.println("Total " + lTotal);
		}
		return lLista;
	}

}
