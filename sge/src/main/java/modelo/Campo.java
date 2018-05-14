/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author desarolloyc
 */
public class Campo implements Serializable
{
    private int idCampo;
    private String Descripcion;
    private String tipo;
    private int entero;
    private int decimal;
    
    //Se usan exclusivamente para mostrar la posición del campo en la visualización de la plantilla
    private int posInicioCampo;
    private int posFinCampo;

    public Campo(int idCampo, String Descripcion, String tipo, int entero, int decimal)
    {
        this.idCampo = idCampo;
        this.Descripcion = Descripcion;
        this.tipo = tipo;
        this.entero = entero;
        this.decimal = decimal;
    }

    
    public Campo getClone()
    {
        return new Campo(this.idCampo, this.Descripcion, this.tipo, this.entero, this.decimal);
    }
    
    /**
     * @return the idCampo
     */
    public int getIdCampo()
    {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo)
    {
        this.idCampo = idCampo;
    }

    /**
     * @return the Descripcion
     */
    public String getDescripcion()
    {
        return Descripcion;
    }

    /**
     * @param Descripcion the Descripcion to set
     */
    public void setDescripcion(String Descripcion)
    {
        this.Descripcion = Descripcion;
    }

    /**
     * @return the tipo
     */
    public String getTipo()
    {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    /**
     * @return the entero
     */
    public int getEntero()
    {
        return entero;
    }

    /**
     * @param entero the entero to set
     */
    public void setEntero(int entero)
    {
        this.entero = entero;
    }

    /**
     * @return the decimal
     */
    public int getDecimal()
    {
        return decimal;
    }

    /**
     * @param decimal the decimal to set
     */
    public void setDecimal(int decimal)
    {
        this.decimal = decimal;
    }

    /**
     * @return the posInicioCampo
     */
    public int getPosInicioCampo()
    {
        return posInicioCampo;
    }

    /**
     * @param posInicioCampo the posInicioCampo to set
     */
    public void setPosInicioCampo(int posInicioCampo)
    {
        this.posInicioCampo = posInicioCampo;
    }

    /**
     * @return the posFinCampo
     */
    public int getPosFinCampo()
    {
        return posFinCampo;
    }

    /**
     * @param posFinCampo the posFinCampo to set
     */
    public void setPosFinCampo(int posFinCampo)
    {
        this.posFinCampo = posFinCampo;
    }
    
}
