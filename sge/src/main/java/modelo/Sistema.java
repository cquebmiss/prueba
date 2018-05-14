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
public class Sistema implements Serializable
{
    private int idSistema;
    private String descripcion;

    
    public Sistema(int idSistema, String descripcion)
    {
        this.idSistema = idSistema;
        this.descripcion = descripcion;
    }
    
    
    /**
     * @return the idSistema
     */
    public int getIdSistema()
    {
        return idSistema;
    }

    /**
     * @param idSistema the idSistema to set
     */
    public void setIdSistema(int idSistema)
    {
        this.idSistema = idSistema;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion()
    {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }
    
}
