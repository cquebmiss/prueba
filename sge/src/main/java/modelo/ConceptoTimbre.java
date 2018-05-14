package modelo;

import java.math.BigDecimal;

public class ConceptoTimbre extends Concepto
{

	private BigDecimal	importeExento;
	private BigDecimal	importeGravado;
	private BigDecimal	importeDeduc;
	private String		tipoPercepDeduc;

	public ConceptoTimbre(int tipoConcepto, String clave, String descripcion, String partida, String partidaAntecedente)
	{
		super(tipoConcepto, clave, descripcion, partida, partidaAntecedente);
		// TODO Auto-generated constructor stub
	}

	public void addImporteExento(BigDecimal cantidad)
	{
		this.importeExento = this.importeExento.add(cantidad);
	}

	public void addImporteGravado(BigDecimal cantidad)
	{
		this.importeGravado = this.importeGravado.add(cantidad);
	}

	public void addImporteDeduc(BigDecimal cantidad)
	{
		this.importeDeduc = this.importeDeduc.add(cantidad);
	}

	// Realiza la suma de los importes y actualiza el atributo heredado de total
	public void updateTotal()
	{
		setValor(this.importeExento.add(this.importeGravado).add(this.importeDeduc));
	}

	public void addNCaso()
	{
		setTotalCasos((getTotalCasos() + 1));

	}

	public BigDecimal getImporteExento()
	{
		return importeExento;
	}

	public void setImporteExento(BigDecimal importeExento)
	{
		this.importeExento = importeExento;
	}

	public BigDecimal getImporteGravado()
	{
		return importeGravado;
	}

	public void setImporteGravado(BigDecimal importeGravado)
	{
		this.importeGravado = importeGravado;
	}

	public BigDecimal getImporteDeduc()
	{
		return importeDeduc;
	}

	public void setImporteDeduc(BigDecimal importeDeduc)
	{
		this.importeDeduc = importeDeduc;
	}

	public String getTipoPercepDeduc()
	{
		return tipoPercepDeduc;
	}

	public void setTipoPercepDeduc(String tipoPercepDeduc)
	{
		this.tipoPercepDeduc = tipoPercepDeduc;
	}

}
