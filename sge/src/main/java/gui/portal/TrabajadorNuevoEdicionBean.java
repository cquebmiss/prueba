/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.portal;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import modelo.Plaza;
import modelo.Trabajador;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import util.utilidades;

/**
 *
 * @author desarolloyc
 */

@ManagedBean
@SessionScoped

public class TrabajadorNuevoEdicionBean implements Serializable
{
	private Trabajador			trabajador;
	private List<SelectItem>	plazas;
	private StreamedContent		imagenT;
	private UploadedFile		imagenTrab;

	private int					idPlazaSelec;

	public TrabajadorNuevoEdicionBean()
	{
		setImagenT(null);

		System.out.println("Ver imagen");
	}

	public void inicializaBeanNuevoTrabajador()
	{
		setPlazas(utilidades.getPlazasSistema());
		//this.trabajador = new Trabajador(null, null, null, null, null);
	}

	public void inicializaBeanEdicionTrabajador(Trabajador trabEdicion)
	{
		setPlazas(utilidades.getPlazasSistema());
		// ya que se pasa la referencia del objeto, lo que se hace es copiar los
		// valores del objeto dentro del nuevo objeto que se usa para la edición
	/*	this.trabajador = new Trabajador(trabEdicion.getNumEmpleado(), trabEdicion.getRFC(), trabEdicion.getNombre(),
				trabEdicion.getCURP(),
				new Plaza(trabEdicion.getPlaza().getIdPlaza(), trabEdicion.getPlaza().getDescripcionPlaza()));*/
	}

	/**
	 * @return the trabajador
	 */
	public Trabajador getTrabajador()
	{
		return trabajador;
	}

	/**
	 * @param trabajador
	 *            the trabajador to set
	 */
	public void setTrabajador(Trabajador trabajador)
	{
		this.trabajador = trabajador;
	}

	/**
	 * @return the plazas
	 */
	public List<SelectItem> getPlazas()
	{
		return plazas;
	}

	/**
	 * @param plazas
	 *            the plazas to set
	 */
	public void setPlazas(List<SelectItem> plazas)
	{
		this.plazas = plazas;
	}

	/**
	 * @return the imagenT
	 */
	public StreamedContent getImagenT()
	{
		if (imagenT != null)
		{
			try
			{
				imagenT.getStream().available();
			} catch (Exception e)
			{
				setImagenT(utilidades.getImagenStreamed(
						utilidades.rutaServerArchivos + trabajador.getRFC() + "/" + trabajador.getPlaza().getIdPlaza()
								+ "/Imagen/" + trabajador.getRFC() + "-" + trabajador.getPlaza().getIdPlaza() + ".jpg",
						trabajador.getRFC() + " - " + trabajador.getNombre() + " - "
								+ trabajador.getPlaza().getDescripcionPlaza()));
			}
		}

		return imagenT;
	}

	/**
	 * @param imagenT
	 *            the imagenT to set
	 */
	public void setImagenT(StreamedContent imagenT)
	{
		this.imagenT = imagenT;
	}

	/**
	 * @return the idPlazaSelec
	 */
	public int getIdPlazaSelec()
	{
		return idPlazaSelec;
	}

	/**
	 * @param idPlazaSelec
	 *            the idPlazaSelec to set
	 */
	public void setIdPlazaSelec(int idPlazaSelec)
	{
		this.idPlazaSelec = idPlazaSelec;
	}

	/**
	 * @return the imagenTrab
	 */
	public UploadedFile getImagenTrab()
	{
		return imagenTrab;
	}

	/**
	 * @param imagenTrab
	 *            the imagenTrab to set
	 */
	public void setImagenTrab(UploadedFile imagenTrab)
	{
		this.imagenTrab = imagenTrab;
	}

}
