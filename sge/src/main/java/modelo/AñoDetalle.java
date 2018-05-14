package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import util.utilidades;

public class AñoDetalle implements Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int año;

	private List<PlantillaRegistro> registrosDAT;

	// Sirve para almacenar los conceptos con sus valores totales
	private List<Concepto> conceptos;
	private List<Concepto> conceptosDeduc;

	private BigDecimal totalPercep;
	private BigDecimal totalDeduc;
	private BigDecimal totalLiq;
	private String totalPercepciones;
	private String totalDeducciones;
	private String totalLiquido;
	private int totalRegistros;

	public AñoDetalle(int año)
	{
		super();
		this.año = año;
		// TODO Auto-generated constructor stub
		registrosDAT = new ArrayList<>();
		this.totalPercep = new BigDecimal(0);
		this.totalDeduc = new BigDecimal(0);
		this.totalLiq = new BigDecimal(0);
		this.conceptos = new ArrayList<>();
		this.conceptosDeduc = new ArrayList<>();

	}

	@Override
	public Object clone()
	{
		AñoDetalle clon = null;

		try
		{
			clon = (AñoDetalle) super.clone();

			if (this.registrosDAT != null)
			{
				List<PlantillaRegistro> registrosDATClon = new ArrayList<>();

				this.registrosDAT.forEach(item ->
				{
					registrosDATClon.add((PlantillaRegistro) item.clone());
				});

				clon.setRegistrosDAT(registrosDATClon);

			}

			if (this.conceptos != null)
			{
				List<Concepto> conceptosClon = new ArrayList<>();

				this.conceptos.forEach(item ->
				{
					conceptosClon.add((Concepto) item.clone());

				});

				clon.setConceptos(conceptosClon);

			}

			if (this.conceptosDeduc != null)
			{
				List<Concepto> conceptosDeducClon = new ArrayList<>();

				this.conceptosDeduc.forEach(item ->
				{
					conceptosDeducClon.add((Concepto) item.clone());
				});

				clon.setConceptosDeduc(conceptosDeducClon);

			}

			if (this.totalPercep != null)
			{
				clon.setTotalPercep(new BigDecimal(this.totalPercep.toString()));
			}

			if (this.totalDeduc != null)
			{
				clon.setTotalDeduc(new BigDecimal(this.totalDeduc.toString()));
			}

			if (this.totalLiq != null)
			{
				clon.setTotalLiq(new BigDecimal(this.totalLiq.toString()));
			}

		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clon;
	}

	//Tipo concepto 0 percepción -1 deducción
	public void addConceptoTipoTimbre(Concepto conceptoAñadiendo, int tipoConcepto)
	{
		utilidades.addConcepto(conceptoAñadiendo, this.conceptos, this.conceptosDeduc, true, tipoConcepto);
	}

	public void addConcepto(Concepto conceptoAñadiendo)
	{

		utilidades.addConcepto(conceptoAñadiendo, this.conceptos, this.conceptosDeduc, false, -1);
		/*
		 * boolean añadidoConcepto = false;
		 * 
		 * switch (conceptoAñadiendo.getTipoConcepto()) { case 1: case 3: case 5: // se
		 * recorre toda la lista de conceptos para saber si ya se // encuentra dentro,
		 * si esta se suma, si no, se crea for (Concepto con : this.conceptos) { if
		 * (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto() &&
		 * con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave()) &&
		 * con.getPartidaAntecedente().trim()
		 * .equalsIgnoreCase(conceptoAñadiendo.getPartidaAntecedente().trim())) {
		 * añadidoConcepto = true; // se suma la cantidad del valor del concepto
		 * con.addValor(conceptoAñadiendo.getValor()); break; }
		 * 
		 * }
		 * 
		 * if (!añadidoConcepto) { this.conceptos.add(conceptoAñadiendo.getClone()); }
		 * 
		 * break;
		 * 
		 * case 2: case 4: case 6:
		 * 
		 * // se recorre toda la lista de conceptos para saber si ya se // encuentra
		 * dentro, si esta se suma, si no, se crea for (Concepto con :
		 * this.conceptosDeduc) { if (con.getTipoConcepto() ==
		 * conceptoAñadiendo.getTipoConcepto() &&
		 * con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave()) &&
		 * con.getPartidaAntecedente().trim()
		 * .equalsIgnoreCase(conceptoAñadiendo.getPartidaAntecedente().trim())) {
		 * añadidoConcepto = true; // se suma la cantidad del valor del concepto
		 * con.addValor(conceptoAñadiendo.getValor()); break; }
		 * 
		 * }
		 * 
		 * if (!añadidoConcepto) {
		 * this.conceptosDeduc.add(conceptoAñadiendo.getClone()); }
		 * 
		 * break; }
		 */

	}

	public void addConceptoExcepcion03(Concepto conceptoAñadiendo)
	{

		boolean añadidoConcepto = false;

		switch (conceptoAñadiendo.getTipoConcepto())
		{
			case 1:
			case 5:
				// se recorre toda la lista de conceptos para saber si ya se
				// encuentra dentro, si esta se suma, si no, se crea
				for (Concepto con : this.conceptos)
				{
					if (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto()
							&& con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave())
							&& con.getPartidaAntecedente().trim()
									.equalsIgnoreCase(conceptoAñadiendo.getPartidaAntecedente().trim()))
					{
						añadidoConcepto = true;

						con.setTotalCasos(con.getTotalCasos() + conceptoAñadiendo.getTotalCasos());
						con.setValor(con.getValor().add(conceptoAñadiendo.getValor()));

						break;
					}

				}

				if (!añadidoConcepto)
				{
					this.conceptos.add((Concepto) conceptoAñadiendo.clone());
				}

			break;

			case 2:
			case 3:
			case 4:
			case 6:

				// se recorre toda la lista de conceptos para saber si ya se
				// encuentra dentro, si esta se suma, si no, se crea
				for (Concepto con : this.conceptosDeduc)
				{
					if (con.getTipoConcepto() == conceptoAñadiendo.getTipoConcepto()
							&& con.getClave().equalsIgnoreCase(conceptoAñadiendo.getClave())
							&& con.getPartidaAntecedente().trim()
									.equalsIgnoreCase(conceptoAñadiendo.getPartidaAntecedente().trim()))
					{
						añadidoConcepto = true;
						// se suma la cantidad del valor del concepto
						con.setTotalCasos(con.getTotalCasos() + conceptoAñadiendo.getTotalCasos());
						con.setValor(con.getValor().add(conceptoAñadiendo.getValor()));

						break;
					}

				}

				if (!añadidoConcepto)
				{
					this.conceptosDeduc.add((Concepto) conceptoAñadiendo.clone());
				}

			break;
		}

	}

	public Concepto getConceptoPercep(Concepto conceptoBuscando)
	{
		for (Concepto con : this.conceptos)
		{
			if (con.getPartidaAntecedente().equalsIgnoreCase(conceptoBuscando.getPartidaAntecedente())
					&& con.getClave().equalsIgnoreCase(conceptoBuscando.getClave()))
			{
				return con;
			}

		}

		return null;
	}

	public Concepto getConceptoDeduc(Concepto conceptoBuscando)
	{
		for (Concepto con : this.conceptosDeduc)
		{
			if (con.getPartidaAntecedente().equalsIgnoreCase(conceptoBuscando.getPartidaAntecedente())
					&& con.getClave().equalsIgnoreCase(conceptoBuscando.getClave()))
			{
				return con;
			}

		}

		return null;
	}

	public List<PlantillaRegistro> getRegistrosDAT()
	{
		return registrosDAT;
	}

	public void setRegistrosDAT(List<PlantillaRegistro> registrosDAT)
	{
		this.registrosDAT = registrosDAT;
	}

	public List<Concepto> getConceptos()
	{
		return conceptos;
	}

	public void setConceptos(List<Concepto> conceptos)
	{
		this.conceptos = conceptos;
	}

	public List<Concepto> getConceptosDeduc()
	{
		return conceptosDeduc;
	}

	public void setConceptosDeduc(List<Concepto> conceptosDeduc)
	{
		this.conceptosDeduc = conceptosDeduc;
	}

	public BigDecimal getTotalPercep()
	{
		return totalPercep;
	}

	public void setTotalPercep(BigDecimal totalPercep)
	{
		this.totalPercep = totalPercep;
	}

	public BigDecimal getTotalDeduc()
	{
		return totalDeduc;
	}

	public void setTotalDeduc(BigDecimal totalDeduc)
	{
		this.totalDeduc = totalDeduc;
	}

	public BigDecimal getTotalLiq()
	{
		return totalLiq;
	}

	public void setTotalLiq(BigDecimal totalLiq)
	{
		this.totalLiq = totalLiq;
	}

	public String getTotalPercepciones()
	{
		return totalPercepciones;
	}

	public void setTotalPercepciones(String totalPercepciones)
	{
		this.totalPercepciones = totalPercepciones;
	}

	public String getTotalDeducciones()
	{
		return totalDeducciones;
	}

	public void setTotalDeducciones(String totalDeducciones)
	{
		this.totalDeducciones = totalDeducciones;
	}

	public String getTotalLiquido()
	{
		return totalLiquido;
	}

	public void setTotalLiquido(String totalLiquido)
	{
		this.totalLiquido = totalLiquido;
	}

	public int getTotalRegistros()
	{
		return totalRegistros;
	}

	public void setTotalRegistros(int totalRegistros)
	{
		this.totalRegistros = totalRegistros;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public int getAño()
	{
		return año;
	}

	public void setAño(int año)
	{
		this.año = año;
	}

}
