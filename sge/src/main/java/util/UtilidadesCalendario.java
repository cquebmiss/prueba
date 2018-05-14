package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class UtilidadesCalendario
{
	public static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static java.util.Date getUtilDate(String fecha, String hora)
	{
		try
		{
			return UtilidadesCalendario.formatter.parse(fecha + " " + hora);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
