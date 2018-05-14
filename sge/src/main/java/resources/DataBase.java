/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.sql.Connection;
import java.sql.Statement;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@ManagedBean(name = "database")
@SessionScoped

public class DataBase
{

	private Context		context;
	private DataSource	dataSource;

	private DataSource	dataSourceArchivoDigital;
	private DataSource	dataSourceSIRI;
	private DataSource	dataSourceNominas;
	private DataSource	dataSourceTimbrado;
	private DataSource	dataSourceSpoolerPdf;
	private DataSource	dataSourceMinutas;
	private DataSource	dataSourceGestiones;
	private DataSource	dataSourceClimaLaboral;

	public DataBase()
	{

		try
		{
			context = new InitialContext();
			setDataSource(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setDataSource(int conexion)
	{

		try
		{
			switch (conexion)
			{
				case 0:

					if (getDataSource() == null)
					{
						setDataSource((DataSource) context.lookup("java:comp/env/webrh"));
					}

				break;

				case 1:

					if (getDataSourceArchivoDigital() == null)
					{
						setDataSourceArchivoDigital((DataSource) context.lookup("java:comp/env/archivodigitalweb"));
					}

				break;

				case 2:

					if (getDataSourceSIRI() == null)
					{
						setDataSourceSIRI((DataSource) context.lookup("java:comp/env/siri"));
					}

				break;

				case 3:

					if (getDataSourceNominas() == null)
					{
						setDataSourceNominas((DataSource) context.lookup("java:comp/env/nominas"));
					}

				break;

				case 4:

					if (getDataSourceTimbrado() == null)
					{
						setDataSourceTimbrado((DataSource) context.lookup("java:comp/env/timbrado"));
					}

				break;

				case 5:

					if (getDataSourceSpoolerPdf() == null)
					{
						setDataSourceSpoolerPdf((DataSource) context.lookup("java:comp/env/spoolerpdf"));
					}

				break;

				case 6:

					if (getDataSourceMinutas() == null)
					{
						setDataSourceMinutas((DataSource) context.lookup("java:comp/env/minutas"));
					}

				break;

				case 7:

					if (getDataSourceGestiones() == null)
					{
						setDataSourceGestiones((DataSource) context.lookup("java:comp/env/gestiones"));
					}

				break;
				case 8:

					if (getDataSourceClimaLaboral() == null)
					{
						setDataSourceClimaLaboral((DataSource) context.lookup("java:comp/env/climalaboral"));
					}

				break;

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * @return the connection
	 */
	public Connection getConnection()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSource().getConnection();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComando()
	{
		Statement comando = null;

		try
		{
			comando = getConnection().createStatement();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnectionArchivo()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceArchivoDigital().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(1);

			try
			{
				conexion = getDataSourceArchivoDigital().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del Archivo Digital");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoArchivo()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionArchivo().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(1);
			getComandoArchivo();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnectionSIRI()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceSIRI().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(2);

			try
			{
				conexion = getDataSourceSIRI().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo SIRI");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoSIRI()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionSIRI().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(2);
			getComandoSIRI();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	public Connection getConnectionNominas()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceNominas().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(3);

			try
			{
				conexion = getDataSourceNominas().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo Nóminas");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	public Connection getConnectionTimbrado()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceTimbrado().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(4);

			try
			{
				conexion = getDataSourceTimbrado().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo Timbrado");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoTimbrado()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionTimbrado().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(4);
			getComandoTimbrado();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	public Connection getConnectionSpoolerPDF()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceSpoolerPdf().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(5);

			try
			{
				conexion = getDataSourceSpoolerPdf().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo SpoolerPDF");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoSpoolerPDF()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionSpoolerPDF().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(5);
			getComandoSpoolerPDF();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	public Connection getConnectionMinutas()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceMinutas().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(6);

			try
			{
				conexion = getDataSourceMinutas().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo minutas");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoMinutas()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionMinutas().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(6);
			getComandoMinutas();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	public Connection getConnectionGestiones()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceGestiones().getConnection();
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(7);

			try
			{
				conexion = getDataSourceGestiones().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo gestiones");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoGestiones()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionGestiones().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(7);
			getComandoGestiones();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	public Connection getConnectionClimaLaboral()
	{
		Connection conexion = null;

		try
		{
			// Carga la conexión
			conexion = getDataSourceClimaLaboral().getConnection();
			conexion.setAutoCommit(true);
		}
		catch (NullPointerException nullEx)
		{
			setDataSource(8);

			try
			{
				conexion = getDataSourceClimaLaboral().getConnection();
			}
			catch (Exception e)
			{
				System.out.println("Fallo en la recuperación del DataSource del módulo de clima laboral");
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return conexion;
	}

	/**
	 * @return the comando
	 */
	public Statement getComandoClimaLaboral()
	{
		Statement comando = null;

		try
		{
			comando = getConnectionClimaLaboral().createStatement();

		}
		catch (NullPointerException nullEx)
		{
			setDataSource(8);
			getComandoClimaLaboral();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return comando;
	}

	/**
	 * @return the context
	 */
	public Context getContext()
	{
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context)
	{
		this.context = context;
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource()
	{
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	/**
	 * @return the dataSourceArchivoDigital
	 */
	public DataSource getDataSourceArchivoDigital()
	{
		return dataSourceArchivoDigital;
	}

	/**
	 * @param dataSourceArchivoDigital
	 *            the dataSourceArchivoDigital to set
	 */
	public void setDataSourceArchivoDigital(DataSource dataSourceArchivoDigital)
	{
		this.dataSourceArchivoDigital = dataSourceArchivoDigital;
	}

	/**
	 * @return the dataSourceSIRI
	 */
	public DataSource getDataSourceSIRI()
	{
		return dataSourceSIRI;
	}

	/**
	 * @param dataSourceSIRI
	 *            the dataSourceSIRI to set
	 */
	public void setDataSourceSIRI(DataSource dataSourceSIRI)
	{
		this.dataSourceSIRI = dataSourceSIRI;
	}

	public DataSource getDataSourceNominas()
	{
		return dataSourceNominas;
	}

	public void setDataSourceNominas(DataSource dataSourceNominas)
	{
		this.dataSourceNominas = dataSourceNominas;
	}

	public DataSource getDataSourceTimbrado()
	{
		return dataSourceTimbrado;
	}

	public void setDataSourceTimbrado(DataSource dataSourceTimbrado)
	{
		this.dataSourceTimbrado = dataSourceTimbrado;
	}

	public DataSource getDataSourceSpoolerPdf()
	{
		return dataSourceSpoolerPdf;
	}

	public void setDataSourceSpoolerPdf(DataSource dataSourceSpoolerPdf)
	{
		this.dataSourceSpoolerPdf = dataSourceSpoolerPdf;
	}

	public DataSource getDataSourceMinutas()
	{
		return dataSourceMinutas;
	}

	public void setDataSourceMinutas(DataSource dataSourceMinutas)
	{
		this.dataSourceMinutas = dataSourceMinutas;
	}

	public DataSource getDataSourceGestiones()
	{
		return dataSourceGestiones;
	}

	public void setDataSourceGestiones(DataSource dataSourceGestiones)
	{
		this.dataSourceGestiones = dataSourceGestiones;
	}

	public DataSource getDataSourceClimaLaboral()
	{
		return dataSourceClimaLaboral;
	}

	public void setDataSourceClimaLaboral(DataSource dataSourceClimaLaboral)
	{
		this.dataSourceClimaLaboral = dataSourceClimaLaboral;
	}

}
