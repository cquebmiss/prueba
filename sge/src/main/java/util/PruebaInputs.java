package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PruebaInputs
{

	public void calcula(String cadena)
	{
		System.out.println(cadena);
		System.out.println("Longitud: " + cadena.length());
		System.out.println("");

		String cadenaTrabajando = cadena;

		int tamañoBloque = 0;
		String mejorBloque = null;
		List<Integer> mejorBloquePosiciones;

		// recursivo
		double mitadLongitud = cadena.length() / 2;
		tamañoBloque = (int) mitadLongitud;
		if (mitadLongitud - tamañoBloque == 0)
		{
			tamañoBloque--;
		}

		do
		{

			mejorBloque = null;
			mejorBloquePosiciones = new ArrayList<>();

			String bloqueBuscando = null;
			List<Integer> bloquePosiciones = null;

			// Se va extrayendo todas las cadenas posibles del tamaño del bloque
			for (int x = 0; x < cadenaTrabajando.length(); x++)
			{
				if (tamañoBloque > (cadenaTrabajando.length() - x))
				{
					break;
				}

				bloqueBuscando = cadenaTrabajando.substring(x, (x + tamañoBloque));
				bloquePosiciones = new ArrayList<>();
				// Cada bloque se va buscando dentro de la cadena total

				int posCoincidencia = -1;

				do
				{

					// System.out.println("Buscando bloque " + bloqueBuscando +
					// " desde la posición " + posCoincidencia);
					posCoincidencia = cadenaTrabajando.indexOf(bloqueBuscando, posCoincidencia);

					if (posCoincidencia > -1)
					{
						bloquePosiciones.add(posCoincidencia);
						// System.out.println(
						// "Indice X " + x + " Bloque " + bloqueBuscando + "
						// posición: " + posCoincidencia);
						posCoincidencia++;
					}

					if (posCoincidencia > (cadenaTrabajando.length() - x))
					{
						break;
					}

				}
				while (posCoincidencia > -1);

				if (bloquePosiciones.size() > mejorBloquePosiciones.size())
				{
					mejorBloque = bloqueBuscando;
					mejorBloquePosiciones = bloquePosiciones;

					// si el bloque al menos cuenta con 2 repeticiones (Contando
					// la original) se podrá comprimir el fragmento
					if (mejorBloquePosiciones.size() > 1)
					{
						cadenaTrabajando = cadenaTrabajando.replaceAll(mejorBloque, "");

						System.out.print(mejorBloque + "=");

						for (Integer pos : mejorBloquePosiciones)
						{
							System.out.print("" + pos + "|");
						}

						System.out.println("");
						// System.out.println(cadenaTrabajando);

						mitadLongitud = cadenaTrabajando.length() / 2;
						tamañoBloque = (int) mitadLongitud;

						if (mitadLongitud - tamañoBloque == 0)
						{
							tamañoBloque--;
						}
					}

				}

			}

			// Se reduce el tamaño del bloque
			tamañoBloque--;

		}
		while (tamañoBloque > 0);

		System.out.println(cadenaTrabajando);
		System.out.println("Fin del algoritmo");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		PruebaInputs nuevo = new PruebaInputs();
		nuevo.calcula(
				"-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212gkgkjgkgkgkjg-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212ugiugiugigiu-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212kjhkhkgiug iugi7857t78 tu fy fifi7ri76ri7oguyghffhgkffiriuyfouy friy f7i r67r 7i6 r7ifuhfougigashoihadsldjoiwyhioweherioewhreioweyoiyoiewryorywoirehoiansklasbkjasgdkjasbdjka gdiuasgkbkjgkgkjgjkgjkgkj gkggjgjgjkgkjgj k -1-40-1-32016747073700110010100-1-370-1240967181818211918182121212gkgkjgkgkgkjg-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212ugiugiugigiu-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212kjhkhkgiug iugi7857t78 tu fy fifi7ri76ri7oguyghffhgkffiriuyfouy friy f7i r67r 7i6 r7ifuhfougigashoihadsldjoiwyhioweherioewhreioweyoiyoiewryorywoirehoiansklasbkjasgdkjasbdjka gdiuasgkjgkgkjgkgkgkjgkjgkjgjkgkj gig kj -1-40-1-32016747073700110010100-1-370-1240967181818211918182121212gkgkjgkgkgkjg-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212ugiugiugigiu-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212kjhkhkgiug iugi7857t78 tu fy fifi7ri76ri7oguyghffhgkffiriuyfouy friy f7i r67r 7i6 r7ifuhfougigashoihadsldjoiwyhioweherioewhreioweyoiyoiewryorywoirehoiansklasbkjasgdkjasbdjka gdiuasgkjhkghkj sk gkjsgksg ius gus oi 892t87teo2gdoiuagdojabdhja vjhx vahd voiDHIWHDIUO	HJPR32’23-R23-3423R.2M3-.4,2-34,-.24MÑL23K4Ñ2KÑ42-3.4M2-.34.-23,4-32,-,-2,4-.23M4.-23MC-R .W-E FLEQW JVÑLWQ JÑ WEI HOI GUI GUO OIY OI-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212gkgkjgkgkgkjg-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212ugiugiugigiu-1-40-1-32016747073700110010100-1-370-1240967181818211918182121212kjhkhkgiug iugi7857t78 tu fy fifi7ri76ri7oguyghffhgkffiriuyfouy friy f7i r67r 7i6 r7ifuhfougigashoihadsldjoiwyhioweherioewhreioweyoiyoiewryorywoirehoiansklasbkjasgdkjasbdjka gdiuasgAIOWUOIBAYDI SIOUG OIASD9P0 A87Y 89P2	 398 9h9 80 g87 g087 gaiush dpiusagdihaldn dajskgdfoiuc hpiuq2873669870’29erugoihknsd.f-a,d-.,.mnaw.qs,-a,nmnbdvbn.,_;LKJGHFJyu09yt6767082390938738279ueprhwvjherkbknjslñdjhfkgjvwsmbf,dn.s, fd.-.sf-m,-s`p3pjoer`’ew90q98rt0389wèropans,mf zmnm-dansbmnd fmsn,m-a.mdv, s,.-,DV.mnd, kjoiòìsopieutryuq`98tr73w8y9qru0rmyoiutwe sofip`gkwsjvdzkñsldmnbv,n.m-f.a,msfzs.m-mf,msgdhuafiopuiufgysoiayow gfuiygwe oif boewug fuywa gfou gsuyf gaouyw bfwgiuoweio fiueshfiojweoifhwsjkfnmñasmfljgdlbnkñsfkwop3uehrgkiyt98erohigskldfasjpuofhvbxd,znsjcvbmsf,zdj ñhgvbsdm,z.sklñajrhgi2iuoq08’973r8675321697082’937865764138º69730’239478u2y4hjgk3hrjltñkrejkbmntrw mf,s.eseAFTRJYTUIUYITURYETWRFSDXBFCGHKYTURYETRWAEGRHFJYTRUY%E$W%·;rweli5kjwnkfsldñvfhjkre.twrQESGDHf-jgktUR%YE$TWrqkdoafskndmlñtlùçy5etgs¨FDFG+uy`lkTY¨RYÑETWç´gñrdlhijfitreg");

		if (1 == 1)
		{
			System.exit(0);
		}

		InputStream is = null;
		byte[] buffer = new byte[5];
		char c;

		String bufferCadena = "";

		try
		{
			// new input stream created
			is = new FileInputStream("/Users/DesarrolloYC/Downloads/images.jpeg");

			System.out.println("Characters printed:");

			while (is.read(buffer) > -1)
			{
				// read stream data into buffer

				// for each byte in the buffer
				for (byte b : buffer)
				{
					// convert byte to character
					// c = (char) b;

					// prints character
					// System.out.print(c);
					System.out.print(b);
					bufferCadena += b + "|";

				}

			}

			// System.out.println(bufferCadena);

			String ruta = "/Users/DesarrolloYC/Downloads/imagesCopia.jpeg";
			File archivo = new File(ruta);
			BufferedWriter bw;

			if (archivo.exists())
			{
				bw = new BufferedWriter(new FileWriter(archivo));
			}
			else
			{
				bw = new BufferedWriter(new FileWriter(archivo));
			}

			is.close();

			is = new FileInputStream("/Users/DesarrolloYC/Downloads/images.jpeg");
			OutputStream out = new FileOutputStream(ruta);

			// We use a buffer for the copy (Usamos un buffer para la <span
			// id="IL_AD12" class="IL_AD">copia</span>).

			byte[] bloque = null;

			String[] bytes = bufferCadena.split("\\|");

			for (String byte1 : bytes)
			{
				bloque = new byte[1];
				bloque[0] = new Byte(byte1);
				out.write(bloque, 0, 1);

			}

			/*
			 * int pos = 0; int maxPos = bufferCadena.length();
			 * 
			 * int posBloque = 0;
			 * 
			 * 
			 * 
			 * while ((pos += (1 * 2)) < maxPos) { posBloque += 1; bloque = new
			 * byte[1]; bloque[0] = new Byte(bufferCadena.substring(pos, pos +
			 * 2)); pos += 2;
			 * 
			 * out.write(bloque, 0, 1);
			 * 
			 * }
			 */
			//
			// byte[] buf = new byte[5];
			// int len;
			// while ((len = is.read(buf)) > 0)
			// {
			// out.write(buf, 0, len);
			// }
			is.close();
			out.close();

			bw.close();

		} catch (Exception e)
		{

			// if any I/O error occurs
			e.printStackTrace();
		}
		finally
		{

			// releases system resources associated with this stream
			if (is != null)
			{
				try
				{
					is.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

}
