package modelo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class JavaZip
{

	// Zip
	private ZipFile					archive;
	private BufferedInputStream		origin	= null;
	private FileOutputStream		dest;
	private ZipOutputStream			out;

	private FileInputStream			fi;
	private ZipEntry				entry;

	// Unzip
	private Enumeration<?>			e;
	private File					file;
	private InputStream				in;
	private BufferedOutputStream	outBuf;
	byte[]							buffer;
	int								read;

	public final void zip(String rutaBase, String nombreArchivoFinal, String archivosAComprimir[])
	{

		final int BUFFER = 2048;
		byte data[] = new byte[BUFFER];

		try
		{
			dest = new FileOutputStream(nombreArchivoFinal);

			out = new ZipOutputStream(new BufferedOutputStream(dest));

			for (int i = 0; i < archivosAComprimir.length; i++)
			{
				System.out.println("Adding: " + archivosAComprimir[i]);
				fi = new FileInputStream(rutaBase + archivosAComprimir[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				entry = new ZipEntry(archivosAComprimir[i]);
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1)
				{
					out.write(data, 0, count);
				}

				fi.close();
				origin.close();
			}

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (this.dest != null)
			{
				try
				{
					this.dest.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (this.out != null)
			{
				try
				{
					this.out.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public final void unzip(File zip, File extraerEn) throws IOException
	{
		archive = new ZipFile(zip);
		e = archive.entries();

		while (e.hasMoreElements())
		{
			entry = (ZipEntry) e.nextElement();
			file = new File(extraerEn, entry.getName());

			if (entry.isDirectory() && !file.exists())
			{
				file.mkdirs();
			}
			else
			{
				if (!file.getParentFile().exists())
				{
					file.getParentFile().mkdirs();
				}

				in = archive.getInputStream(entry);
				outBuf = new BufferedOutputStream(new FileOutputStream(file));

				buffer = new byte[8192];

				while (-1 != (read = in.read(buffer)))
				{
					outBuf.write(buffer, 0, read);
				}

				in.close();
				outBuf.close();

			}
		}

		archive.close();

	}

}
