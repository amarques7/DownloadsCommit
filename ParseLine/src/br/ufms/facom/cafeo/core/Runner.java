package br.ufms.facom.cafeo.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//import java.util.concurrent.TimeUnit;
import java.io.IOException;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
import java.io.FileWriter;
//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.util.Random;
//import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Scanner;
//import java.util.Stack;

import br.ufms.facom.cafeo.git.Commit;
import br.ufms.facom.cafeo.git.Repo;
import br.ufms.facom.cafeo.git.RepoFile;
//import br.ufms.facom.cafeo.utils.ManipulationUtils;
import br.ufms.facom.cafeo.utils.ManipulationUtils;

public class Runner {

	static File TEMP;
	static int TEMP2;

	static ArrayList<Repo> listofRepos = new ArrayList<Repo>();

	static String repoList = "";
	static String dir_projeto;
	static String dir_result;
	static public String localpath;
	static public String dir_Doxyfile = "C:\\Users\\amarq\\eclipse-workspace\\ParseLine\\";
	static long endFinal;
	static long dif;
	static long tempInicial;
	static long endFinalcommit;
	static long difCommit;
	static long tempInicialCommit;
	static long endFinalParser;
	static long difParser;
	static long tempInicialParser;
	static int i;
	private static BufferedReader leitorArquivo;

	public static void main(String[] args) throws IOException, InterruptedException {

		FileReader arquivoLeitura = new FileReader(dir_Doxyfile + "diretorios.txt");

		leitorArquivo = new BufferedReader(arquivoLeitura);
		dir_projeto = leitorArquivo.readLine();
		dir_result = leitorArquivo.readLine();
		localpath = leitorArquivo.readLine();

		// repoList = leitorArquivo.readLine();
		if (args.length == 0)
			repoList = leitorArquivo.readLine();
		else
			repoList = args[0];
		loadRepos(ManipulationUtils.loadRepos(repoList));
		generateVariabilities();

	}

//	public static void escreveDirAqui(String dirOndeSalvar, String oqueSalvar) {
//		try {
//			FileWriter cria = new FileWriter(dir_result + "modificado.txt", true);
//			PrintWriter grava = new PrintWriter(cria);
//
//			grava.print(oqueSalvar + " ");
//			grava.close();
//			cria.close();
//		} catch (IOException ex) {
//			System.out.println("Erro em manipular o arquivo \n Erro: " + ex.getMessage());
//		}
//	}

	public static void escreve(String dir_result, String nome_arquivo, String projeto, double hora, double minuto,
			double seg, String oqueSalvar, int a) {
		try {
			if (a == 1) {
				FileWriter cria = new FileWriter(dir_result + nome_arquivo + ".txt", true);
				PrintWriter grava = new PrintWriter(cria);
				grava.println("O tempo total de excucao do projeto " + projeto + " é : " + hora + " h " + " : " + minuto
						+ " m" + " : " + seg + " s");
				grava.close();
				cria.close();
			} else {
				FileWriter cria = new FileWriter(dir_result + "modificado.txt", true);
				PrintWriter grava = new PrintWriter(cria);
				grava.print(oqueSalvar + " ");
				grava.close();
				cria.close();

			}
		} catch (IOException ex) {
			System.out.println("Erro em manipular o arquivo \n Erro: " + ex.getMessage());
		}
	}

	public static void transforma(long segundos, String projeto, String nome_arquivo, boolean controle) {
		double hora = 0.0;
		double minuto = 0.0;
		double seg = 0.0;
		hora = java.util.concurrent.TimeUnit.SECONDS.toHours(segundos);
		segundos = (int) (segundos - (hora * 3600));
		minuto = java.util.concurrent.TimeUnit.SECONDS.toMinutes(segundos);
		segundos = (int) (segundos - (minuto * 60));
		seg = segundos;

		if (controle) {
			escreve(dir_result, projeto, nome_arquivo, hora, minuto, seg, null, 1);
		} else {
			escreve(dir_result, projeto, nome_arquivo, hora, minuto, seg, null, 1);
		}
	}

	public static void ChamaPython(String commit, String dir_projeto, String dir_result, String nome_projeto)
			throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		System.out.println("antes da main");

		processBuilder.command("python", localpath + "main.py", commit, dir_projeto, dir_result, localpath,
				nome_projeto);

		processBuilder.redirectOutput(new File(dir_result + nome_projeto + "2.txt"));
		Process process = processBuilder.start();
		processBuilder.redirectErrorStream(true);
		System.out.println("pos main");
		process.waitFor();
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().gc();
	}

	private static void generateVariabilities() throws IOException, InterruptedException {

		tempInicial = (long) 0.0;
		tempInicial = System.currentTimeMillis();

		new ConfiguraDoxyfileChamaDoxygen();
		int count = 0;
		String projeto = null;

		if (!listofRepos.isEmpty()) {
			for (Repo r : listofRepos) {

				count = 0;
				System.out.println();
				System.out.println("Analyzing " + r.getName() + "... ");
				projeto = r.getName();
				System.out.println();

				if (!r.getCommitList().isEmpty()) {
					File diretorio = new File((dir_result + System.getProperty("file.separator") + r.getName()
							+ System.getProperty("file.separator")));
					diretorio.mkdirs();
					tempInicialCommit = (long) 0.0;
					tempInicialCommit = System.currentTimeMillis();

					for (Commit c : r.getCommitList()) {
						count++;
						r.checkoutCommit(c.getId());
						System.out.println("Análise commit: " + count);
						boolean ok = false;
						String arquivoMod = null;
						// traz os arquivo modificados
						for (RepoFile f : c.getTouchedFiles()) {
							if (f.getExtension().equals("c")) {
								arquivoMod = f.getPath().replace("C:\\", "../../../../");
								System.out.println(arquivoMod);
								escreve(dir_result, null, null, 0, 0, 0, arquivoMod, 0);
								ok = true;
							}

						}
						endFinalcommit = System.currentTimeMillis();
						difCommit = endFinalcommit - tempInicialCommit;
						transforma((difCommit / 1000), "TimeCommit_" + projeto, "Analise_no_Commit: " + count + " ",
								true);

						if (ok) {
							tempInicialParser = (long) 0.0;
							tempInicialParser = System.currentTimeMillis();

							ConfiguraDoxyfileChamaDoxygen.DoxyfileDoxygen(localpath, dir_result, dir_Doxyfile,
									r.getName());
							ChamaPython(count + "-" + c.getId(), dir_projeto, dir_result, r.getName());

							endFinalParser = System.currentTimeMillis();
							difParser = endFinalParser - tempInicialParser;
							transforma((difParser / 1000), "TimeParser_" + projeto, "Analise_no_Parser: " + count + " ",
									true);

							Runtime.getRuntime().runFinalization();
							Runtime.getRuntime().gc();

						}
						if (count == 8) {
							System.exit(0);
						}
					}
				}

			}
		}
		endFinal = System.currentTimeMillis();
		dif = endFinal - tempInicial;

		transforma((dif / 1000), "TempoTotal" + projeto, projeto, false);
	}

	private static void loadRepos(ArrayList<String> repos) {
		for (String repoURI : repos)
			listofRepos.add(new Repo(repoURI, dir_projeto));
	}
}
