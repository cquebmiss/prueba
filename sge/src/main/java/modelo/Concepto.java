package modelo;

import java.io.Serializable;
import java.math.BigDecimal;

import util.utilidades;

public class Concepto implements Serializable, Cloneable
{
	private int tipoConcepto;
	private String clave;
	private String descripcion;
	private String partida;
	private String partidaAntecedente;
	private BigDecimal valor;
	private String valorString;
	private int totalCasos;

	private boolean asociadoAPlaza;

	// Nuevos campos de acuerdo a información solicitada para 2017 se ajusta el
	// catálogo de conceptos
	private String periodicidad;
	private String ambito;
	private String partidaEstatal;
	private String partidaRamo33;
	private String partidaRamo12;
	private String seGrava;
	private String tipoDeConcepto;
	private String tipoDeTrabajador;
	private String soporteDocumental;
	private String formulaOFormulacion;

	private String compensacionISR;
	private String ISR;

	@Override
	public Object clone()
	{
		Concepto clon = null;

		try
		{
			clon = (Concepto) super.clone();

			if (this.valor != null)
			{
				clon.valor = new BigDecimal(this.valor.toString());

			}

		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return clon;
	}

	public Concepto()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Concepto getClone()
	{
		return new Concepto(this.tipoConcepto, this.clave, this.descripcion, this.partida, this.partidaAntecedente,
				this.valor, this.valorString, this.totalCasos, this.asociadoAPlaza);
	}

	public Concepto(int tipoConcepto, String clave, String descripcion, String partida, String partidaAntecedente)
	{
		super();
		this.tipoConcepto = tipoConcepto;
		this.clave = clave;
		this.descripcion = descripcion;
		this.partida = partida;
		this.partidaAntecedente = partidaAntecedente;
	}

	public Concepto(int tipoConcepto, String clave, String descripcion, String partida, String partidaAntecedente,
			BigDecimal valor)
	{
		super();
		this.tipoConcepto = tipoConcepto;
		this.clave = clave;
		this.descripcion = descripcion;
		this.partida = partida;
		this.partidaAntecedente = partidaAntecedente;
		this.valor = new BigDecimal("0.00");
		this.totalCasos = 0;
		addValor(valor);
	}

	public Concepto(int tipoConcepto, String clave, String descripcion, String partida, String partidaAntecedente,
			BigDecimal valor, boolean asociadoAPlaza)
	{
		super();
		this.tipoConcepto = tipoConcepto;
		this.clave = clave;
		this.descripcion = descripcion;
		this.partida = partida;
		this.partidaAntecedente = partidaAntecedente;
		this.valor = new BigDecimal("0.00");
		this.totalCasos = 0;
		this.asociadoAPlaza = asociadoAPlaza;
		addValor(valor);
	}

	public Concepto(int tipoConcepto, String clave, String descripcion, String partida, String partidaAntecedente,
			BigDecimal valor, String valorString, int totalCasos, boolean asociadoAPlaza)
	{
		super();
		this.tipoConcepto = tipoConcepto;
		this.clave = clave;
		this.descripcion = descripcion;
		this.partida = partida;
		this.partidaAntecedente = partidaAntecedente;
		this.valor = valor;
		this.valorString = valorString;
		this.totalCasos = totalCasos;
	}

	public Concepto(int tipoConcepto, String clave, String descripcion, String partida, String partidaAntecedente,
			BigDecimal valor, String valorString, int totalCasos, boolean asociadoAPlaza, String periodicidad,
			String ambito, String partidaEstatal, String partidaRamo33, String partidaRamo12, String seGrava,
			String tipoDeConcepto, String tipoDeTrabajador, String soporteDocumental, String formulaOFormulacion)
	{
		super();
		this.tipoConcepto = tipoConcepto;
		this.clave = clave;
		this.descripcion = descripcion;
		this.partida = partida;
		this.partidaAntecedente = partidaAntecedente;
		this.valor = valor;
		this.valorString = valorString;
		this.totalCasos = totalCasos;
		this.asociadoAPlaza = asociadoAPlaza;
		this.periodicidad = periodicidad;
		this.ambito = ambito;
		this.partidaEstatal = partidaEstatal;
		this.partidaRamo33 = partidaRamo33;
		this.partidaRamo12 = partidaRamo12;
		this.seGrava = seGrava;
		this.tipoDeConcepto = tipoDeConcepto;
		this.tipoDeTrabajador = tipoDeTrabajador;
		this.soporteDocumental = soporteDocumental;
		this.formulaOFormulacion = formulaOFormulacion;
	}

	public void updateDatosAdicionales()
	{
		utilidades.updateConceptoDatosAdicionales(this);
	}

	public String getClave()
	{
		return clave;
	}

	public void setClave(String clave)
	{
		this.clave = clave;
	}

	public String getDescripcion()
	{
		return descripcion;
	}

	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}

	public BigDecimal getValor()
	{
		return valor;
	}

	public void addValor(BigDecimal valor)
	{
		setValor(this.valor.add(valor));
		this.totalCasos++;
	}

	public void setValor(BigDecimal valor)
	{
		this.valor = valor;
		this.valorString = utilidades.formato.format(this.valor);
	}

	public int getTipoConcepto()
	{
		return tipoConcepto;
	}

	public void setTipoConcepto(int tipoConcepto)
	{
		this.tipoConcepto = tipoConcepto;
	}

	public String getValorString()
	{
		return valorString;
	}

	public void setValorString(String valorString)
	{
		this.valorString = valorString;
	}

	public int getTotalCasos()
	{
		return totalCasos;
	}

	public void setTotalCasos(int totalCasos)
	{
		this.totalCasos = totalCasos;
	}

	public String getPartidaAntecedente()
	{
		return partidaAntecedente;
	}

	public void setPartidaAntecedente(String partidaAntecedente)
	{
		this.partidaAntecedente = partidaAntecedente;
	}

	public String getPartida()
	{
		return partida;
	}

	public void setPartida(String partida)
	{
		this.partida = partida;
	}

	public boolean isAsociadoAPlaza()
	{
		return asociadoAPlaza;
	}

	public void setAsociadoAPlaza(boolean asociadoAPlaza)
	{
		this.asociadoAPlaza = asociadoAPlaza;
	}

	public String getPeriodicidad()
	{
		return periodicidad;
	}

	public void setPeriodicidad(String periodicidad)
	{
		this.periodicidad = periodicidad;
	}

	public String getAmbito()
	{
		return ambito;
	}

	public void setAmbito(String ambito)
	{
		this.ambito = ambito;
	}

	public String getPartidaEstatal()
	{
		return partidaEstatal;
	}

	public void setPartidaEstatal(String partidaEstatal)
	{
		this.partidaEstatal = partidaEstatal;
	}

	public String getPartidaRamo33()
	{
		return partidaRamo33;
	}

	public void setPartidaRamo33(String partidaRamo33)
	{
		this.partidaRamo33 = partidaRamo33;
	}

	public String getPartidaRamo12()
	{
		return partidaRamo12;
	}

	public void setPartidaRamo12(String partidaRamo12)
	{
		this.partidaRamo12 = partidaRamo12;
	}

	public String getSeGrava()
	{
		return seGrava;
	}

	public void setSeGrava(String seGrava)
	{
		this.seGrava = seGrava;
	}

	public String getTipoDeConcepto()
	{
		return tipoDeConcepto;
	}

	public void setTipoDeConcepto(String tipoDeConcepto)
	{
		this.tipoDeConcepto = tipoDeConcepto;
	}

	public String getTipoDeTrabajador()
	{
		return tipoDeTrabajador;
	}

	public void setTipoDeTrabajador(String tipoDeTrabajador)
	{
		this.tipoDeTrabajador = tipoDeTrabajador;
	}

	public String getSoporteDocumental()
	{
		return soporteDocumental;
	}

	public void setSoporteDocumental(String soporteDocumental)
	{
		this.soporteDocumental = soporteDocumental;
	}

	public String getFormulaOFormulacion()
	{
		return formulaOFormulacion;
	}

	public void setFormulaOFormulacion(String formulaOFormulacion)
	{
		this.formulaOFormulacion = formulaOFormulacion;
	}

	public String getCompensacionISR()
	{
		return compensacionISR;
	}

	public void setCompensacionISR(String compensacionISR)
	{
		this.compensacionISR = compensacionISR;
	}

	public String getISR()
	{
		return ISR;
	}

	public void setISR(String iSR)
	{
		ISR = iSR;
	}

}
