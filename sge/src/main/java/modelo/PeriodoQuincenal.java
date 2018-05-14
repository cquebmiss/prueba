package modelo;

import java.util.ArrayList;
import java.util.List;

public class PeriodoQuincenal
{
	private List<Quincena> quincenas;

	public PeriodoQuincenal()
	{
		super();
		this.quincenas = new ArrayList<>();
		// TODO Auto-generated constructor stub
	}

	public void addQuincena(Quincena qna)
	{
		this.quincenas.add(qna);
	}

	public List<Quincena> getQuincenas()
	{
		return quincenas;
	}

	public void setQuincenas(List<Quincena> quincenas)
	{
		this.quincenas = quincenas;
	}

}
