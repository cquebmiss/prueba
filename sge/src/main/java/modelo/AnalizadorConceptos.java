package modelo;

public class AnalizadorConceptos
{
	private String expresion;

	public AnalizadorConceptos(String expresion)
	{
		this.expresion = expresion;

	}

	public boolean expresionValida()
	{
		String fragmento = "";

		for (int x = 0; x < this.expresion.length(); x++)
		{
			char caracter = this.expresion.charAt(x);

			switch ("" + caracter)
			{
				case "+":
				case "-":
				case "*":
				case "/":
				case "(":
				case ")":

					fragmento = "";
					break;

				default:
					break;
			}

		}
		
		if( ! fragmento.isEmpty() )
		{
			
			
		}

		return true;

	}
	
	//Analiza el fragmento en búsqueda que sea o un número double o una expresión de concepto correctamente elaborada
	private boolean analizaFragmento(String fragmento)
	{
		try
		{
		///	parse.Double(fragmento);
			
			
		}
		catch(Exception e)
		{
			System.out.println("El fragmento no es un número, se analizará en búsqueda de concepto");
		}
		
		
		
		return true;
		
	}
	

}
