package Simulacion;
import java.util.ArrayList;
import java.util.Arrays;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import practicas.Leer;
public class PruebasNumeros
{
	public static void main(String[] args)
	{
		int total = 0,test=0;
		do
		{
			System.out.println("Cuantos Numeros se quieren generar? (34-100)");
			total = Leer.datoInt();
		}	
		while(total < 34 || total >100);
		double numeros [] = new double[total];
		System.out.println("Numeros generados");
		numeros = generaNumeros(numeros);
		for (double d : numeros)
			System.out.printf("%4.6f\n",d);
		do
		{
			System.out.println("\nQue validacion se quiere realizar?\n"
					+ "1-Chi Cuadrado\n"
					+ "2-Test de Kolmogorov\n"
					+ "3-Series\n"
					+ "4-Distancias o Huecos\n"
					+ "5-Poker\n"
					+ "Otro: Salir");
			test = Leer.datoInt();
			if(test==1) 	  ChiCuadrado(numeros);
			else if(test==2) Kolmogorov(numeros);
			else if(test==3) Series(numeros);
			else if(test==4) Distancias(numeros);
			else if(test==5) Poker(numeros);
		}while(test > 0 && test <=5);
		System.out.println("Se a salido del programa");
	}
	private static double[] generaNumeros(double[] numeros)
	{
		for (int i = 0; i < numeros.length; i++)
			numeros[i]=Math.random();
		return numeros;
	}
	private static void ChiCuadrado(double[] numeros)
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
		JFreeChart chart=ChartFactory.createBarChart(//genera el grafico 
			        "Chi-cuadrado", "", "", dataset, PlotOrientation.VERTICAL, true,true,true);
		ArrayList<Integer> obtenido = new ArrayList<Integer>();//se guardan los numeros obtenidos de cada intervalo
		int fallo = getPorcentajeFallo(), sumaObtenidos=0;
		double fallos5 [] = {3.8415,5.9915,7.8147,9.4877,11.0705,12.5916,14.0671,15.5073,16.9190,18.3070};
		double fallos10[] = {2.7055,4.6052,6.2514,7.7794,9.2363,10.6446,12.0170,13.3616,14.6837,15.9872};
		double intervalo = Math.sqrt(numeros.length),//se genera los K intervalos
			  esperados = numeros.length/intervalo,//los numeros esperados por intervalo
			  rangos = 1/intervalo,//el rango de cada intervalo
			  acumulado = 0,//el acumualdo de los rangos
			  distribucion=0,//la distribucion a tener en cuenta dependiendo la tabla de chi 
			  sumaEsperados=0,// la suma de los numeros esperados
			  sumaCHI =0;// suma de todos los valores de chi por cada intervalo
		distribucion = (fallo == 5) ? fallos5[(int) (intervalo-2)] : fallos10[(int) (intervalo-2)];
		System.out.printf("%5s %2s  %4s %5s %5s\n","I","O","E","(O-E)","CHI");
		for (int i = 0; i < intervalo; i++)
		{//se ejecuta por cada intervalo y inicia el contador por cada interaccion
			obtenido.add(0);
			for (int j = 0; j < numeros.length; j++)
			{//busca entre todos los numeros los que esten en cierto rango de cada intervalo
				if(numeros[j] > acumulado && numeros[j] <= (rangos+acumulado))
					obtenido.set(i, obtenido.get(i)+1);
			}	//en caso de que este, incrementa el contador de cada interraccion
			double chi = Math.pow(obtenido.get(i)-esperados, 2)/esperados;//calcula chi
			sumaCHI+=chi;
			sumaObtenidos+=obtenido.get(i);
			sumaEsperados+=esperados;
			acumulado+=rangos;//incrementa las sumas 
			System.out.printf("%4.3f %2d %5.2f %5.2f %5.2f\n"//imprime la tabla
					,acumulado,obtenido.get(i),esperados,obtenido.get(i)-esperados, chi);
			String dato = ""+acumulado;//ingresa los datos a la grafica
			if(dato.length()>4)
				dato=dato.substring(0,5);
			dataset.addValue(obtenido.get(i), dato, "");
		}
		System.out.printf("%5s %2d %3.2f %12.3f\n","total",sumaObtenidos,sumaEsperados,sumaCHI); 
		if(sumaCHI<=distribucion)//valida si los numeros son validos
			System.out.println("Los numeros son validos, estan distribuidos uniformemente");
		else
			System.out.println("Los numeros NO son validos,NO estan distribuidos uniformemente");
		muestraGrafica(chart);//muestra la grafica de barras
	}
	private static void muestraGrafica(JFreeChart chart)
	{
		ChartPanel panel = new ChartPanel(chart);
		JFrame ventana = new JFrame("El grafico");
		ventana.getContentPane().add(panel);
		ventana.setSize(700, 700);
		ventana.pack();
		ventana.setVisible(true);
	}
	private static void Kolmogorov(double[] numeros)
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart = ChartFactory.createLineChart("Kolmogorov","Numeros","esperado",dataset ); 
		double fallos5 [] = {0.22743, 0.22425, 0.22119, 0.21826, 0.21544, 0.21273, 0.21012, 0.20760, 0.20517, 0.20283, 0.20056, 0.19837, 0.19625, 0.19028 , 0.19221, 0.19028, 0.18841};
		double fallos10[] = {0.21472,0.20185, 0.19910, 0.19646, 0.19392, 0.19148, 0.18913, 0.18687, 0.18468, 0.18257, 0.18051, 0.17856, 0.17665, 0.17481, 0.17301, 0.17128, 0.16959};
		int fallo = getPorcentajeFallo();
		double ordenados [] = ordenaNumeros(numeros);
		double esperado=0.0, diferencia=0, mayor=0, distribucion=0;
		if(numeros.length>50)
			if (fallo == 5)
				distribucion = 1.36 / Math.sqrt((double) numeros.length);
			else
				distribucion = 1.22 / Math.sqrt((double) numeros.length);
		else 
			if (fallo == 5)
				distribucion = fallos5[numeros.length-34];
			else
				distribucion = fallos10[numeros.length-34];
		System.out.printf("%8s %8s %8s\n","obtenido","esperado","diferencia");
		for (int i = 0; i < ordenados.length; i++)
		{
			esperado=((1+i+(esperado*0))/(ordenados.length));
			diferencia=Math.abs(ordenados[i]-esperado);
			if(diferencia>mayor)
				mayor=diferencia;
			System.out.printf("%5f %5f %5f\n",ordenados[i],esperado,diferencia);
			dataset.addValue(esperado, "esperado", ""+(i+1));
			dataset.addValue(ordenados[i], "obtenidos", ""+(i+1));
		}
		System.out.println("\ndiferencia mayor: "+mayor);
		System.out.println("distribucion: "+distribucion);
		if(mayor<=distribucion)//valida si los numeros son validos
			System.out.println("Los numeros son validos, estan distribuidos uniformemente");
		else
			System.out.println("Los numeros NO son validos,NO estan distribuidos uniformemente");
		muestraGrafica(chart);//muestra la grafica
	}
	private static void Series(double[] numeros)
	{
		XYSeriesCollection dataset = new XYSeriesCollection();  
		JFreeChart chart = ChartFactory.createScatterPlot("pares ordenados", "X-Axis", "Y-Axis", dataset);  
		int fallo = getPorcentajeFallo(), total = numeros.length;
		double pares[][] = new double[2][total];
		XYSeries series1 = new XYSeries("pares");  
		for (int i = 0; i < total; i++)//guarda los pares
		{
			pares[0][i] = numeros[i];
			pares[1][i] = (i == total - 1)? numeros[i]: numeros[i + 1];
			series1.add(pares[0][i], pares[1][i]);
		}
		for (int i = 0; i < total; i++)// imprime los pares 
			System.out.printf("%5.5f - %5.5f\n",pares[0][i],pares[1][i]);
		double celdas = 5, distribucion=0, suma =0;
		double size = 1 / celdas;//calcula el tamaño de cada celda
		int contador [][]= new int[(int) celdas][(int) celdas];//inicia la matriz de contador
		for (int i = 0; i < celdas; i++)//eje X
			for (int j = 0; j < celdas; j++)// eje Y
				for (int k = 0; k < total; k++)//busca entre todos los numeros y todos los rangos
					if ((pares[0][k] > (size * j) && pares[0][k] <= (size * (j + 1))) &&
					    (pares[1][k] > (size * i) && pares[1][k] <= (size * (i + 1))))
						contador[i][j]++;//valida si los 2 puntos estan dentro de cada rango
		System.out.println("Obtenidos");
		for (int i = (int) celdas-1; i >=0; i--)//eje X
		{//imprime el contador de los numeros en cada rango
			System.out.printf("%3.2f |",size*(i+1));
			for (int j = 0; j < celdas; j++)// eje Y
				System.out.print(contador[i][j]+" ");
			System.out.println();
		}	
		System.out.println("Esperados");
		double esperados = total/(celdas*celdas);
		for (int i = (int) celdas-1; i >=0; i--)//eje X
		{//imprime los esperados
			System.out.printf("%3.2f |",size*(i+1));
			for (int j = 0; j < celdas; j++)// eje Y
				System.out.printf("%4.3f ",esperados);
			System.out.println();
		}
		System.out.println("Distribucion");
		for (int i = (int) celdas-1; i >=0; i--)//eje X
		{//imprime los esperados
			System.out.printf("%3.2f |",size*(i+1));
			for (int j = 0; j < celdas; j++)// eje Y
			{
				System.out.printf("%4.3f ",Math.pow(contador[i][j]-esperados,2)/esperados);
				suma+=Math.pow(contador[i][j]-esperados,2)/esperados;
			}
			System.out.println();
		}
		System.out.println("suma de las distribuciones: "+suma);
		distribucion = (fallo == 5)? 36.4150: 33.1962;
		if(suma<=distribucion)//valida si los numeros son validos
			System.out.println("Los numeros son validos, son independientes");
		else
			System.out.println("Los numeros NO son validos,NO son independientes");
		dataset.addSeries(series1);  
		muestraGrafica(chart);//muestra la grafica
	}
	private static void Distancias(double[] numeros)
	{
		double fallos5 [] = {3.8415,5.9915,7.8147,9.4877,11.0705,12.5916,14.0671,15.5073,16.9190,18.3070,19.6752,21.0261,22.3620};
		double fallos10[] = {2.7055,4.6052,6.2514,7.7794,9.2363,10.6446,12.0170,13.3616,14.6837,15.9872,17.2750,18.5493,19.8119};
		int fallo = getPorcentajeFallo();
		System.out.println("Que intervalo se va a usar?");
		System.out.println("Ingrese el valor (menor) de a");
		double a = Leer.datoDouble();
		System.out.println("Ingrese el valor (mayor) de b");
		double b = Leer.datoDouble();
		double theta = Math.abs(b-a);
		int pertenece[]=new int[numeros.length];
		for (int i = 0; i < numeros.length; i++)
			pertenece[i] = (numeros[i] > a && numeros[i] <= b)?1:0;
		int auxCambia=0;
		Object contador[]=new Object[numeros.length];
		int contadorContadores[]=new int[numeros.length];
		for (int i = 0; i < numeros.length; i++)
		{
			contador[i]=null;
			auxCambia=pertenece[i];
			int cont=0;
			while(i < numeros.length && auxCambia==pertenece[i])
			{
				if(auxCambia==0)
					cont++;
				i++;
			}
			i--;
			contadorContadores[cont]++;
			contador[i]=cont;
		}
		System.out.printf("%3s %7s %1s %5s\n","i","#alegen","E","hueco");
		int totalContadores=0, totalObtenidos=0;
		for (int i = 0; i < numeros.length; i++)
		{
			if(contadorContadores[i]!=0)
			{
				totalContadores++;
				totalObtenidos+=contadorContadores[i];
			}
			if(contador[i]!=null)
				System.out.printf("%3d %4.4f %2d %3d\n",(i+1),numeros[i],pertenece[i],contador[i]);
			else
				System.out.printf("%3d %4.4f %2d\n",(i+1),numeros[i],pertenece[i]);
		}
		double probabilidad=0,sumaProp=0,sumaEspe=0,sumaChi=0,distribucion=0;
		int j=0,sumaObte=0;
		System.out.printf("\n%3s %6s %2s %6s %6s %5s \n","i","Pi","Oi","Ei","Oi-Ei","chi");
		for (int i = 0; i < numeros.length; i++)
		{
			if (i > 0 && i < totalContadores-1) probabilidad = Math.pow(1-theta, i)*theta;
			else if (i == 0) probabilidad = theta;
			else if(j==totalContadores-1) probabilidad = Math.pow(1-theta, i);
			if(contadorContadores[i]!=0)
			{
				if (j == totalContadores)
				{
					j--;
					break;
				}
				double esperados = probabilidad*totalObtenidos;
				double chi = Math.pow(contadorContadores[i]-esperados,2)/esperados;
				sumaProp+=probabilidad;
				sumaObte+=contadorContadores[i];
				sumaEspe+=esperados;
				sumaChi +=chi;
				System.out.printf(" %2d %2.4f %2d %6.3f %6.3f %5.3f\n",i,probabilidad,contadorContadores[i],esperados,contadorContadores[i]-esperados,chi);
				j++;
			}
			else if(j<totalContadores)
			{
				probabilidad = Math.pow(1-theta, i);
				int restantes = totalContadores-i;
				double esperados = probabilidad*totalObtenidos;
				double chi = Math.pow(restantes-esperados,2)/esperados;
				sumaObte+=restantes;
				sumaProp+=probabilidad;
				sumaEspe+=esperados;
				sumaChi +=chi;
				System.out.printf(">=%1d %2.4f %2d %6.3f %6.3f %5.3f\n",i,probabilidad,restantes,esperados,restantes-esperados,chi);
				break;
			}
		}
		System.out.printf(" %8.3f  %2d %6.2f %12.3f\n",sumaProp,sumaObte,sumaEspe,sumaChi);
		distribucion = (fallo == 5)? fallos5[j]: fallos10[j];
		if(sumaChi<=distribucion)//valida si los numeros son validos
			System.out.println("Los numeros son validos, son independientes");
		else
			System.out.println("Los numeros NO son validos,NO son independientes");
	}
	private static void Poker(double[] numeros)
	{
		int contador[]= new int[7];
		int fallo = getPorcentajeFallo();
		for (int i = 0; i < numeros.length; i++)
		{
			String numero = ""+numeros[i],tipo="";//numero a string
			numero = numero.substring(2, 7);// quita el "0." del string
			char[] array = numero.toCharArray();//convierte el numero a un array
			int contador2[]=new int[10];//cuenta cuantas veces aparece un numero en el string
			for (char c : array)//convierte los 5 caracteres en 5 enteros
				contador2[Character.getNumericValue(c)]++;//incrementa el contador dependiendo el numero que sea
			String cadena="";
			for (int j : contador2)
				if(j!=0)//valida que no sean 0 y crea una cadena con las veces que cualquier numero se repite
					cadena+=j;//no importa cual sea el numero, importa cuantas veces aparece
			if(cadena.equals("11111"))//en este caso hay 5 numeros que se repiten solo una vez
			{
				contador[0]++;
				tipo= "Pachuca";
			}
			else if(cadena.equals("2111") || 
				   cadena.equals("1211") || 
			        cadena.equals("1121") ||
			        cadena.equals("1112"))
			{
				contador[1]++;
				tipo= "1 par";
			}	         
			else if(cadena.equals("221") || 
				   cadena.equals("122") || 
			        cadena.equals("212"))
			{
				contador[2]++;
				tipo= "2 par";
			}
			else if(cadena.equals("311") || 
				   cadena.equals("131") || 
				   cadena.equals("113"))
			{
				contador[3]++;
				tipo= "Tercia";
			}
			else if(cadena.equals("32") || cadena.equals("23"))
			{
				contador[4]++;
				tipo= "Full";
			}
			else if(cadena.contains("4"))
			{
				contador[5]++;
				tipo= "Poker";
			}
			else if(cadena.contains("5"))
			{
				contador[6]++;
				tipo= "Quintilla";
			}
			System.out.printf("0.%5s %9s\n",numero,tipo);
		}
		Object eventos [][]= {
				{"Pachuca",0.3024},
				{"1 par",0.5040},
				{"2 pares",0.1080},
				{"Tercia",0.0720},
				{"Full",0.0090},
				{"Poker",0.0045},
				{"Quintilla",0.0001}};
		System.out.println();
		double distribucion=0, sumaChi=0;
		distribucion = (fallo == 5)? 12.5016: 10.6446;
		for (int i = 0; i < 7; i++)
		{
			double probabilidad = (double)eventos[i][1];
			double esperados = probabilidad * numeros.length;
			double chi = Math.pow(contador[i]-esperados,2)/esperados;
			sumaChi +=chi;
			System.out.printf("%9s %2d %5.4f %7.4f %6.4f\n",eventos[i][0],contador[i],probabilidad,esperados,chi);
		}
		System.out.println("Suma chi:"+sumaChi);
		if(sumaChi<=distribucion)//valida si los numeros son validos
			System.out.println("Los numeros son validos, son independientes");
		else
			System.out.println("Los numeros NO son validos,NO son independientes");
	}
	private static double[] ordenaNumeros(double[] numeros)
	{
		double arr[] = Arrays.copyOf(numeros, numeros.length);
		for (int i = 0; i < arr.length; i++) 
			for (int j = 0; j < arr.length; j++) 
				if (arr[i] < arr[j]) 
				{
					double temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
		return arr;
	}
	private static int getPorcentajeFallo()
	{
		System.out.println("Cual sera el % de fallo?(5% o 10%)");
		int fallo = Leer.datoInt();
		if(fallo != 5)
			if(fallo !=10)
				getPorcentajeFallo();
		return fallo;
	}
}