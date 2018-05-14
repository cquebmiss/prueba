package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Acumulado implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	private HashMap				nomina;
	private int					año;

	// Se verifica la mayor fecha de fin de periodo y fecha de inicio de periodo
	java.util.Calendar			fInicioCalendar;
	java.util.Calendar			fFinalCalendar;
	java.util.Calendar			fInicioRegCalendar;
	java.util.Calendar			fFinalRegCalendar;
	java.util.Calendar			fechaInicialCalendar;
	String						fInicioDAT;
	String						fFinalDAT;
	String						fInicioREG;
	String						fFinalREG;
	int							posFInicial;
	int							posFFinal;
	boolean						periodoListo;

	public Acumulado(int año)
	{
		super();
		this.año = año;

		// Se verifica la mayor fecha de fin de periodo y fecha de inicio de
		// periodo
		this.fInicioCalendar = java.util.Calendar.getInstance();
		this.fInicioCalendar.clear(Calendar.HOUR);
		this.fInicioCalendar.clear(Calendar.MINUTE);
		this.fInicioCalendar.clear(Calendar.SECOND);
		this.fInicioCalendar.clear(Calendar.MILLISECOND);
		this.fFinalCalendar = (Calendar) fInicioCalendar.clone();
		this.fInicioRegCalendar = (Calendar) fInicioCalendar.clone();
		this.fFinalRegCalendar = (Calendar) fInicioCalendar.clone();

		this.fechaInicialCalendar = java.util.Calendar.getInstance();
		this.fechaInicialCalendar.set(this.año, Calendar.JANUARY, 1);
		this.fechaInicialCalendar.clear(Calendar.HOUR);
		this.fechaInicialCalendar.clear(Calendar.MINUTE);
		this.fechaInicialCalendar.clear(Calendar.SECOND);
		this.fechaInicialCalendar.clear(Calendar.MILLISECOND);

		// TODO Auto-generated constructor stub
		this.nomina = new HashMap();

	}

	// Compruebo que la nomina no exista y en su caso lo añado
	public void addNomina(int idNomina)
	{
		if (!this.nomina.containsKey(idNomina))
		{
			this.nomina.put(idNomina, new HashMap());

		}

	}

	// Se comprueba que la unidad exista dentro de la nómina y en su caso se
	// añade
	public void addUnidadNomina(int idNomina, String unidad)
	{
		if (!((HashMap) this.nomina.get(idNomina)).containsKey(unidad))
		{
			((HashMap) this.nomina.get(idNomina)).put(unidad, new HashMap());
		}

	}

	public void addTrabajador(int idNomina, String unidad, PlantillaRegistro reg, String claveTrabajador,
			Concepto concepto)
	{
		if (!((HashMap) ((HashMap) this.nomina.get(idNomina)).get(unidad)).containsKey(claveTrabajador))
		{
			((HashMap) ((HashMap) this.nomina.get(idNomina)).get(unidad)).put(claveTrabajador, reg);

		}

		PlantillaRegistro regTrabajador = (PlantillaRegistro) ((Map) ((Map) this.nomina.get(idNomina)).get(unidad))
				.get(claveTrabajador);

		if (regTrabajador.getConceptosAcum() == null)
		{
			regTrabajador.setConceptosAcum(new HashMap());
		}

		if (!regTrabajador.getConceptosAcum()
				.containsKey(concepto.getTipoConcepto() + concepto.getClave() + concepto.getPartidaAntecedente()))
		{
			regTrabajador.getConceptosAcum()
					.put(concepto.getTipoConcepto() + concepto.getClave() + concepto.getPartidaAntecedente(), concepto);
		}
		else
		{
			((Concepto) regTrabajador.getConceptosAcum()
					.get(concepto.getTipoConcepto() + concepto.getClave() + concepto.getPartidaAntecedente()))
							.addValor(concepto.getValor());

		}

	}

	//Añade trabajadores y acumula el valor de sus conceptos un arreglo general de acuerdo a su nómina, unidad y número de empleado
	public void addTrabajadorVerificandoNominaUnidad(int idNomina, String unidad, PlantillaRegistro regNuevo,
			String claveTrabajador, Concepto concepto, boolean ajustarFechasPago)
	{
		addNomina(idNomina);

		addUnidadNomina(idNomina, unidad);

		this.fFinalDAT = regNuevo.getValorPorDescripcionContains("odo de pago fina");

		if (this.fFinalDAT.equals(""+año+"1215"))
		{
			this.posFFinal = regNuevo.getPosicionValorPorDescripcionContains("odo de pago fina");
			
			regNuevo.getCampo(this.posFFinal).setValor(""+año+"1231");
			
		}

		if (!((HashMap) ((HashMap) this.nomina.get(idNomina)).get(unidad)).containsKey(claveTrabajador))
		{
			regNuevo.setConceptosAcum(new HashMap<>());
			((HashMap) ((HashMap) this.nomina.get(idNomina)).get(unidad)).put(claveTrabajador, regNuevo);

		}

		PlantillaRegistro regGuardado = (PlantillaRegistro) ((Map) ((Map) this.nomina.get(idNomina)).get(unidad))
				.get(claveTrabajador);

		if (regGuardado.getConceptosAcum() == null)
		{
			regGuardado.setConceptosAcum(new HashMap());
		}
		else
		{

			if( ajustarFechasPago )
			{
				// Se verifica la mayor fecha de fin de periodo y fecha de inicio de
				// periodo
				this.fFinalCalendar = (Calendar) fInicioCalendar.clone();
				this.fInicioRegCalendar = (Calendar) fInicioCalendar.clone();
				this.fFinalRegCalendar = (Calendar) fInicioCalendar.clone();
				
				this.fInicioDAT = regNuevo.getValorPorDescripcionContains("odo de pago inicia");
				this.fFinalDAT = regNuevo.getValorPorDescripcionContains("odo de pago fina");
				
				this.fInicioCalendar.set(Integer.parseInt(fInicioDAT.substring(0, 4)),
						Integer.parseInt(fInicioDAT.substring(4, 6)) - 1, Integer.parseInt(fInicioDAT.substring(6, 8)));
				this.fFinalCalendar.set(Integer.parseInt(fFinalDAT.substring(0, 4)),
						Integer.parseInt(fFinalDAT.substring(4, 6)) - 1, Integer.parseInt(fFinalDAT.substring(6, 8)));
				
				this.fInicioREG = regGuardado.getValorPorDescripcionContains("odo de pago inicia");
				this.fFinalREG = regGuardado.getValorPorDescripcionContains("odo de pago fina");
				
				this.fInicioRegCalendar.set(Integer.parseInt(fInicioREG.substring(0, 4)),
						Integer.parseInt(fInicioREG.substring(4, 6)) - 1, Integer.parseInt(fInicioREG.substring(6, 8)));
				this.fFinalRegCalendar.set(Integer.parseInt(fFinalREG.substring(0, 4)),
						Integer.parseInt(fFinalREG.substring(4, 6)) - 1, Integer.parseInt(fFinalREG.substring(6, 8)));
				
				this.posFInicial = regGuardado.getPosicionValorPorDescripcionContains("odo de pago inicia");
				this.posFFinal = regGuardado.getPosicionValorPorDescripcionContains("odo de pago fina");
				
				if (this.fInicioCalendar.before(this.fInicioRegCalendar))
				{
					regGuardado.getCampo(this.posFInicial).setValor(this.fInicioDAT);
				}
				
				if (this.fFinalCalendar.after(this.fFinalRegCalendar))
				{
					regGuardado.getCampo(this.posFFinal).setValor(this.fFinalDAT);
				}
				
			}
			else
			{
				
				
			}

		}

		if (!regGuardado.getConceptosAcum().containsKey(
				"" + concepto.getTipoConcepto() + concepto.getClave() + " " + concepto.getPartidaAntecedente()))
		{
			regGuardado.getConceptosAcum().put(
					"" + concepto.getTipoConcepto() + concepto.getClave() + " " + concepto.getPartidaAntecedente(),
					concepto);
		}
		else
		{
			((Concepto) regGuardado.getConceptosAcum().get(
					"" + concepto.getTipoConcepto() + concepto.getClave() + " " + concepto.getPartidaAntecedente()))
							.addValor(concepto.getValor());
		}

	}

	//Añade trabajadores y acumula el valor de sus conceptos un arreglo general de acuerdo a su nómina, unidad y número de empleado
	//Éste método tiene la diferencia que va acumulando tomando como clave de trabajador el idnomina, unidad y número de cheque
	public void addTrabajadorVerificandoNominaUnidadPorRegDATProducto(int idNomina, String unidad, PlantillaRegistro regNuevo,
			String claveTrabajador, Concepto concepto)
	{
		addNomina(idNomina);
		
		addUnidadNomina(idNomina, unidad);
		
		this.fFinalDAT = regNuevo.getValorPorDescripcionContains("odo de pago fina");
		
		if (this.fFinalDAT.equals(""+año+"1215"))
		{
			this.posFFinal = regNuevo.getPosicionValorPorDescripcionContains("odo de pago fina");
			
			regNuevo.getCampo(this.posFFinal).setValor(""+año+"1231");
			
		}
		
		if (!((HashMap) ((HashMap) this.nomina.get(idNomina)).get(unidad)).containsKey(claveTrabajador))
		{
			regNuevo.setConceptosAcum(new HashMap<>());
			((HashMap) ((HashMap) this.nomina.get(idNomina)).get(unidad)).put(claveTrabajador, regNuevo);
			
		}
		
		PlantillaRegistro regGuardado = (PlantillaRegistro) ((Map) ((Map) this.nomina.get(idNomina)).get(unidad))
				.get(claveTrabajador);
		
		if (regGuardado.getConceptosAcum() == null)
		{
			regGuardado.setConceptosAcum(new HashMap());
		}
		else
		{
			
			// Se verifica la mayor fecha de fin de periodo y fecha de inicio de
			// periodo
			this.fFinalCalendar = (Calendar) fInicioCalendar.clone();
			this.fInicioRegCalendar = (Calendar) fInicioCalendar.clone();
			this.fFinalRegCalendar = (Calendar) fInicioCalendar.clone();
			
			this.fInicioDAT = regNuevo.getValorPorDescripcionContains("odo de pago inicia");
			this.fFinalDAT = regNuevo.getValorPorDescripcionContains("odo de pago fina");
			
			this.fInicioCalendar.set(Integer.parseInt(fInicioDAT.substring(0, 4)),
					Integer.parseInt(fInicioDAT.substring(4, 6)) - 1, Integer.parseInt(fInicioDAT.substring(6, 8)));
			this.fFinalCalendar.set(Integer.parseInt(fFinalDAT.substring(0, 4)),
					Integer.parseInt(fFinalDAT.substring(4, 6)) - 1, Integer.parseInt(fFinalDAT.substring(6, 8)));
			
			this.fInicioREG = regGuardado.getValorPorDescripcionContains("odo de pago inicia");
			this.fFinalREG = regGuardado.getValorPorDescripcionContains("odo de pago fina");
			
			this.fInicioRegCalendar.set(Integer.parseInt(fInicioREG.substring(0, 4)),
					Integer.parseInt(fInicioREG.substring(4, 6)) - 1, Integer.parseInt(fInicioREG.substring(6, 8)));
			this.fFinalRegCalendar.set(Integer.parseInt(fFinalREG.substring(0, 4)),
					Integer.parseInt(fFinalREG.substring(4, 6)) - 1, Integer.parseInt(fFinalREG.substring(6, 8)));
			
			this.posFInicial = regGuardado.getPosicionValorPorDescripcionContains("odo de pago inicia");
			this.posFFinal = regGuardado.getPosicionValorPorDescripcionContains("odo de pago fina");
			
			if (this.fInicioCalendar.before(this.fInicioRegCalendar))
			{
				regGuardado.getCampo(this.posFInicial).setValor(this.fInicioDAT);
			}
			
			if (this.fFinalCalendar.after(this.fFinalRegCalendar))
			{
				regGuardado.getCampo(this.posFFinal).setValor(this.fFinalDAT);
			}
			
		}
		
		if (!regGuardado.getConceptosAcum().containsKey(
				"" + concepto.getTipoConcepto() + concepto.getClave() + " " + concepto.getPartidaAntecedente()))
		{
			regGuardado.getConceptosAcum().put(
					"" + concepto.getTipoConcepto() + concepto.getClave() + " " + concepto.getPartidaAntecedente(),
					concepto);
		}
		else
		{
			((Concepto) regGuardado.getConceptosAcum().get(
					"" + concepto.getTipoConcepto() + concepto.getClave() + " " + concepto.getPartidaAntecedente()))
			.addValor(concepto.getValor());
		}
		
	}

	public List<String> ajustaPeriodosPago(PlantillaRegistro trabajador)
	{

		List<String> periodos = new ArrayList<>();
		periodos.add("");
		periodos.add("");

		this.fFinalCalendar = (Calendar) fInicioCalendar.clone();
		this.fInicioRegCalendar = (Calendar) fInicioCalendar.clone();
		this.fFinalRegCalendar = (Calendar) fInicioCalendar.clone();

		this.fInicioDAT = trabajador.getValorPorDescripcionContains("odo de pago inicia");
		this.fFinalDAT = trabajador.getValorPorDescripcionContains("odo de pago fina");

		this.fInicioCalendar.set(Integer.parseInt(fInicioDAT.substring(0, 4)),
				Integer.parseInt(fInicioDAT.substring(4, 6)) - 1, Integer.parseInt(fInicioDAT.substring(6, 8)));
		this.fFinalCalendar.set(Integer.parseInt(fFinalDAT.substring(0, 4)),
				Integer.parseInt(fFinalDAT.substring(4, 6)) - 1, Integer.parseInt(fFinalDAT.substring(6, 8)));

		this.posFInicial = trabajador.getPosicionValorPorDescripcionContains("odo de pago inicia");
		this.posFFinal = trabajador.getPosicionValorPorDescripcionContains("odo de pago fina");

		if (this.fFinalCalendar.before(this.fechaInicialCalendar))
		{
			periodos.set(1, "" + this.fechaInicialCalendar.get(Calendar.YEAR) + "0115");
		}
		else if (this.fFinalDAT.substring(4, 8).equals("1215"))
		{
			periodos.set(1, this.fFinalDAT.substring(0, 4) + "1231");
		}
		else
		{
			periodos.set(1, this.fFinalDAT);
		}

		if (this.fInicioCalendar.before(this.fechaInicialCalendar))
		{
			periodos.set(0, "" + this.fechaInicialCalendar.get(Calendar.YEAR) + "0101");
		}
		else
		{
			if (this.fInicioDAT.equals(this.fFinalDAT))
			{
				periodos.set(0, "" + this.fFinalDAT.substring(0, 6) + "01");
			}
			else
			{
				periodos.set(0, this.fFinalDAT);
			}

		}

		if (periodos.get(0).equals(periodos.get(1)))
		{
			periodos.set(0, this.fFinalDAT.substring(0, 6) + "01");
		}

		return periodos;

	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public HashMap getNomina()
	{
		return nomina;
	}

	public void setNomina(HashMap nomina)
	{
		this.nomina = nomina;
	}

}
